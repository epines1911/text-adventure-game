package edu.uob;

import java.util.ArrayList;
import java.util.HashSet;

public class GameAction
{
    private final HashSet<String> subjects;
    private final HashSet<String> consumed;
    private final HashSet<String> produced;
    private String narration;

    public GameAction() {
        subjects = new HashSet<>();
        consumed = new HashSet<>();
        produced = new HashSet<>();
    }

    public void setNarration(String newNarration) {
        narration = newNarration;
    }

    public String getNarration() {
        return narration;
    }

    public HashSet<String> getConsumed() {
        return consumed;
    }

    public HashSet<String> getProduced() {
        return produced;
    }

    public HashSet<String> getSubjects() {
        return subjects;
    }

    public void addSubjects(String name) {
        subjects.add(name);
    }

    public void addConsumed(String name) {
        consumed.add(name);
    }

    public void addProduced(String name) {
        produced.add(name);
    }
}
