package edu.uob.entity;

import edu.uob.GameEntity;

import java.util.HashMap;

public class Player extends GameEntity {
    HashMap<String, Artefact> inventory;
    int healthLevel = 3;
    public Player(String name, String description) {
        super(name, description);
        inventory = new HashMap<>();
    }

    public HashMap<String, Artefact> getInventory() {
        return inventory;
    }

    public void healthLevelUp() {
        this.healthLevel = healthLevel + 1;
    }

    public void healthLevelDown() {
        if (healthLevel < 1) {
            return;
        }
        this.healthLevel = healthLevel - 1;
    }

    public int getHealthLevel() {
        return healthLevel;
    }
}
