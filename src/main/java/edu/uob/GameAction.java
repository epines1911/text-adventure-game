package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

public class GameAction
{
    HashMap<String, GameEntity> subjects;
    HashMap<String, GameEntity> consumed;
    HashMap<String, GameEntity> produced;
    String narration;
    public GameAction() {
        subjects = new HashMap<String, GameEntity>();
        consumed = new HashMap<String, GameEntity>();
        produced = new HashMap<String, GameEntity>();
    }

}
