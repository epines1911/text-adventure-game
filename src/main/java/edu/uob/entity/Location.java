package edu.uob.entity;

import edu.uob.GameEntity;

import java.util.HashMap;

public class Location extends GameEntity {
    private HashMap<String, Location> paths;
    private HashMap<String, Artefact> artefacts;
    private HashMap<String, Furniture> furniture;
    private HashMap<String, Character> characters;

    public Location(String name, String description) {
        super(name, description);
    }

    public HashMap<String, Location> getPaths() {
        return paths;
    }

    public HashMap<String, Artefact> getArtefacts() {
        return artefacts;
    }

    public HashMap<String, Character> getCharacters() {
        return characters;
    }

    public HashMap<String, Furniture> getFurniture() {
        return furniture;
    }

    public void addArtefact(Artefact entity) {
        artefacts.put(entity.getName(), entity);
    }

    public void addFurniture(Furniture entity) {
        furniture.put(entity.getName(), entity);
    }

    public void addCharacter(Character entity) {
        characters.put(entity.getName(), entity);
    }
}
