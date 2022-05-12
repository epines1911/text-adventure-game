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
        if (healthLevel < 3) {
            healthLevel += 1;
        }
    }

    public void healthLevelDown() {
        if (healthLevel < 1) {
            return;
        }
        healthLevel -= 1;
    }

    public int getHealthLevel() {
        return healthLevel;
    }

    public boolean isDeath() {
        return healthLevel < 1;
    }

    public void setFullHealth() {
        healthLevel = 3;
    }
}
