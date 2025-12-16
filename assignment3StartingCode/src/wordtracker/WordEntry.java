package wordtracker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordEntry implements Serializable, Comparable<WordEntry> {

    private static final long serialVersionUID = 1L;

    private String word;

    private Map<String, List<Integer>> occurrences;

    public WordEntry(String word) {
        this.word = word.toLowerCase();
        this.occurrences = new HashMap<>();
    }

    public String getWord() {
        return word;
    }

    public void addOccurrence(String filename, int lineNumber) {
        occurrences
                .computeIfAbsent(filename, f -> new ArrayList<>())
                .add(lineNumber);
    }

    public Map<String, List<Integer>> getOccurrences() {
        return occurrences;
    }

    @Override
    public int compareTo(WordEntry other) {
        return this.word.compareTo(other.word);
    }
}