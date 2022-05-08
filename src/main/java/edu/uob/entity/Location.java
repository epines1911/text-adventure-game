package edu.uob.entity;

import edu.uob.GameEntity;

import java.util.HashMap;

public class Location extends GameEntity {
    HashMap<String, Location> paths;
    HashMap<String, Artefact> artefacts;
    HashMap<String, Furniture> furniture;
    HashMap<String, Character> characters;
    HashMap<String, Player> players;
    public Location(String name, String description) {
        super(name, description);
    }
}
