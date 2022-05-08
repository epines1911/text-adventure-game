package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

public class GameAction
{
    //todo 对于trigger这个set，我感觉应该是，一个能保证元素不重复的data structure。先用arraylist试试。
    ArrayList<String> triggers;
    HashMap<String, GameEntity> subjects;
    HashMap<String, GameEntity> consumed;
    HashMap<String, GameEntity> produced;
    String narration;
    public GameAction() {
        triggers = new ArrayList<String>();
        subjects = new HashMap<String, GameEntity>();
        consumed = new HashMap<String, GameEntity>();
        produced = new HashMap<String, GameEntity>();
    }

}
