package edu.uob.entity;

import edu.uob.GameEntity;

import java.util.HashMap;

public class Player extends GameEntity {
    private final HashMap<String, Artefact> inventory;
    private int healthLevel = 3;
    private Location currentLocation;

    public Player(String name, String description, Location birthPlace) {
        super(name, description);
        inventory = new HashMap<>();
        currentLocation = birthPlace;
    }

    public void setLocation(Location aimLocation) {
        currentLocation = aimLocation;
    }

    public Location getLocation() {
        return currentLocation;
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
