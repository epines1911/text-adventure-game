package edu.uob.entity;

import edu.uob.GameEntity;

import java.util.HashMap;

public class Player extends GameEntity {
    HashMap<String, Artefact> inventory;
//    Boolean isCurrentPlayer;
    public Player(String name, String description) {
        super(name, description);
    }

    public HashMap<String, Artefact> getInventory() {
        return inventory;
    }

}
