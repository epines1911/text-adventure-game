package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

public class GameAction
{
    private final ArrayList<String> subjects;
    private final ArrayList<String> consumed;
    private final ArrayList<String> produced;
    private String narration;

    public GameAction() {
        subjects = new ArrayList<String>();
        consumed = new ArrayList<String>();
        produced = new ArrayList<String>();
    }

    public void setNarration(String newNarration) {
        narration = newNarration;
    }

    public String getNarration() {
        return narration;
    }

    public ArrayList<String> getConsumed() {
        return consumed;
    }

    public ArrayList<String> getProduced() {
        return produced;
    }

    public ArrayList<String> getSubjects() {
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
