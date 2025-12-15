package application;

import implementations.BSTree;
import implementations.BSTreeNode;
import utilities.Iterator;
import utilities.RepositoryManager;

import java.io.*;
import java.util.*;

/**
 * WordTracker.java
 * Runs from command line:
 * java -jar WordTracker.jar <input.txt> -pf/-pl/-po [-f<output.txt>]
 */
public class WordTracker {

    // -----------------------------
    // Data stored in BST (must be Comparable + Serializable)
    // -----------------------------
    public static class WordEntry implements Comparable<WordEntry>, Serializable {
        private static final long serialVersionUID = 1L;

        private final String word;

        // filename -> info about that word in that file
        private final Map<String, FileInfo> files = new LinkedHashMap<>();

        public WordEntry(String word) {
            this.word = word;
        }

        public String getWord() {
            return word;
        }

        public Map<String, FileInfo> getFiles() {
            return files;
        }

        public void addOccurrence(String filename, int lineNumber) {
            FileInfo info = files.computeIfAbsent(filename, f -> new FileInfo());
            info.addLine(lineNumber);
        }

        public int getTotalFrequency() {
            int total = 0;
            for (FileInfo fi : files.values()) total += fi.getFrequency();
            return total;
        }

        @Override
        public int compareTo(WordEntry other) {
            return this.word.compareTo(other.word);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof WordEntry)) return false;
            return word.equals(((WordEntry) o).word);
        }

        @Override
        public int hashCode() {
            return word.hashCode();
        }
    }

    public static class FileInfo implements Serializable {
        private static final long serialVersionUID = 1L;

        private final List<Integer> lines = new ArrayList<>();
        private int frequency = 0;

        public void addLine(int lineNumber) {
            // Keep duplicates if word appears multiple times on same line (frequency matters).
            lines.add(lineNumber);
            frequency++;
        }

        public List<Integer> getLines() {
            return lines;
        }

        public int getFrequency() {
            return frequency;
        }
    }

    // -----------------------------
    // Main
    // -----------------------------
    public static void main(String[] args) {
        if (args.length < 2) {
            printUsageAndExit();
            return;
        }

        String inputPath = args[0];
        String mode = args[1]; // -pf or -pl or -po
        String outputPath = parseOptionalOutputPath(args); // may be null

        if (!mode.equals("-pf") && !mode.equals("-pl") && !mode.equals("-po")) {
            System.out.println("Invalid option: " + mode);
            printUsageAndExit();
            return;
        }

        // 1) Load repository.ser if it exists
        BSTree<WordEntry> tree = RepositoryManager.loadRepository();
        if (tree == null) tree = new BSTree<>();

        // 2) Scan input file and update tree
        File inputFile = new File(inputPath);
        if (!inputFile.exists() || !inputFile.isFile()) {
            System.out.println("Input file not found: " + inputPath);
            return;
        }

        try {
            scanFileIntoTree(inputFile, tree);
        } catch (IOException e) {
            System.out.println("Error reading input file: " + e.getMessage());
            return;
        }

        // 3) Produce report (console or file)
        try {
            writeReport(tree, mode, outputPath);
        } catch (IOException e) {
            System.out.println("Error writing report: " + e.getMessage());
        }

        // 4) Save repository.ser
        try {
            RepositoryManager.saveRepository(tree);
        } catch (IOException e) {
            System.out.println("Error saving repository: " + e.getMessage());
        }
    }

    private static void printUsageAndExit() {
        System.out.println("Usage:");
        System.out.println("  java -jar WordTracker.jar <input.txt> -pf|-pl|-po [-f<output.txt>]");
        System.out.println("Examples:");
        System.out.println("  java -jar WordTracker.jar test1.txt -pf");
        System.out.println("  java -jar WordTracker.jar test2.txt -pl");
        System.out.println("  java -jar WordTracker.jar test3.txt -po -fresults.txt");
    }

    private static String parseOptionalOutputPath(String[] args) {
        // Spec shows -f<output.txt> (like -fresults.txt). We'll support both "-fresults.txt" and "-f results.txt".
        if (args.length < 3) return null;

        for (int i = 2; i < args.length; i++) {
            String a = args[i];
            if (a.startsWith("-f") && a.length() > 2) {
                return a.substring(2); // "-fresults.txt" -> "results.txt"
            }
            if (a.equals("-f") && i + 1 < args.length) {
                return args[i + 1]; // "-f results.txt"
            }
        }
        return null;
    }

    private static void scanFileIntoTree(File inputFile, BSTree<WordEntry> tree) throws IOException {
        String filename = inputFile.getName();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            int lineNumber = 1;

            while ((line = br.readLine()) != null) {
                // Extract "words" (keeps letters/digits and apostrophes).
                // You can adjust the regex if your group decides different rules.
                java.util.regex.Matcher m = java.util.regex.Pattern
                        .compile("[A-Za-z0-9']+")
                        .matcher(line);

                while (m.find()) {
                    String word = m.group().toLowerCase(Locale.ROOT);

                    // Find or create WordEntry in BST
                    WordEntry key = new WordEntry(word);
                    BSTreeNode<WordEntry> node = tree.search(key);

                    if (node == null) {
                        // not present -> add then search to update
                        tree.add(key);
                        node = tree.search(key);
                    }

                    // Update the stored entry
                    if (node != null) {
                        node.getElement().addOccurrence(filename, lineNumber);
                    }
                }
                lineNumber++;
            }
        }
    }

    private static void writeReport(BSTree<WordEntry> tree, String mode, String outputPath) throws IOException {
        Writer writer = null;
        try {
            if (outputPath != null && !outputPath.isBlank()) {
                writer = new BufferedWriter(new FileWriter(outputPath));
            } else {
                writer = new OutputStreamWriter(System.out);
            }

            // Alphabetical order: inorder iterator
            Iterator<WordEntry> it = tree.inorderIterator();

            while (it.hasNext()) {
                WordEntry entry = it.next();

                if (mode.equals("-pf")) {
                    // Print word + file list
                    writer.write(entry.getWord() + " -> files: ");
                    writer.write(String.join(", ", entry.getFiles().keySet()));
                    writer.write(System.lineSeparator());
                } else if (mode.equals("-pl")) {
                    // Print word + files + line numbers
                    writer.write(entry.getWord() + " -> ");
                    writer.write(formatFilesAndLines(entry, false));
                    writer.write(System.lineSeparator());
                } else { // -po
                    // Print word + files + line numbers + frequency
                    writer.write(entry.getWord() + " -> ");
                    writer.write(formatFilesAndLines(entry, true));
                    writer.write(" | totalFreq=" + entry.getTotalFrequency());
                    writer.write(System.lineSeparator());
                }
            }

            writer.flush();
        } finally {
            // Don’t close System.out
            if (writer != null && !(writer instanceof OutputStreamWriter)) {
                writer.close();
            }
        }
    }

    private static String formatFilesAndLines(WordEntry entry, boolean includeFreq) {
        StringBuilder sb = new StringBuilder();
        boolean firstFile = true;

        for (Map.Entry<String, FileInfo> fe : entry.getFiles().entrySet()) {
            if (!firstFile) sb.append(" ; ");
            firstFile = false;

            String file = fe.getKey();
            FileInfo info = fe.getValue();

            sb.append(file).append(" lines=").append(info.getLines());
            if (includeFreq) sb.append(" freq=").append(info.getFrequency());
        }
        return sb.toString();
    }
}
