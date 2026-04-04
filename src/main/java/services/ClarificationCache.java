package services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClarificationCache {

    // Stored by clarification number to avoid duplicates
    private static final Map<Integer, ClarificationEntry> cache = new ConcurrentHashMap<>();

    public static void store(int number, String problem, String question, String answer, boolean answered) {
        cache.put(number, new ClarificationEntry(number, problem, question, answer, answered));
    }

    public static List<ClarificationEntry> getAll() {
        return new ArrayList<>(cache.values());
    }

    public static void clear() {
        cache.clear();
    }

    // Inner class to hold clarification data
    public static class ClarificationEntry {
        public int number;
        public String problem;
        public String question;
        public String answer;
        public boolean answered;

        public ClarificationEntry(int number, String problem, String question, String answer, boolean answered) {
            this.number   = number;
            this.problem  = problem;
            this.question = question;
            this.answer   = answer;
            this.answered = answered;
        }
    }
}