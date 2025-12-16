package wordtracker;

import implementations.BSTree;
import implementations.BSTreeNode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class WordParser {

    /**
     * Reads a text file line by line, extracts words, records line numbers,
     * and inserts or updates WordEntry objects in the provided BST.
     */
    public static void parseFile(String filename, BSTree<WordEntry> tree) throws IOException {

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                line = line.replaceAll("[^a-zA-Z0-9 ]", " ").toLowerCase();
                String[] words = line.split("\\s+");

                for (String word : words) {
                    if (word.isEmpty()) {
                        continue;
                    }

                    WordEntry entry = new WordEntry(word);

                    BSTreeNode<WordEntry> node = tree.search(entry);

                    if (node == null) {
                        // New word → add to BST
                        entry.addOccurrence(filename, lineNumber);
                        tree.add(entry);
                    } else {
                        node.getElement().addOccurrence(filename, lineNumber);
                    }
                }
            }
        }
    }
}