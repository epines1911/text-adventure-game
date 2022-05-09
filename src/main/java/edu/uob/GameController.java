package edu.uob;

import edu.uob.entity.Location;
import edu.uob.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;

public class GameController {
    GameModel model;
    String message;
    public GameController(GameModel gameModel) {
        model = gameModel;
    }

    public void commandParser(String[] tokens) {
        //
    }

    public String getMessage() {
        return message;
    }
}
