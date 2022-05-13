package edu.uob.entity;

import edu.uob.GameEntity;

import java.util.HashMap;

public class Location extends GameEntity {
    private final HashMap<String, Location> paths;
    private final HashMap<String, Artefact> artefacts;
    private final HashMap<String, Furniture> furniture;
    private final HashMap<String, Character> characters;

    public Location(String name, String description) {
        super(name, description);
        paths = new HashMap<>();
        artefacts = new HashMap<>();
        furniture = new HashMap<>();
        characters = new HashMap<>();
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

    public void addPaths(Location entity) {
        paths.put(entity.getName(), entity);
    }
// find the type of an entity according to its name
    public String getEntityType(String name) {
        if (artefacts.containsKey(name)) {
            return "artefact";
        } else if (furniture.containsKey(name)) {
            return "furniture";
        } else if (characters.containsKey(name)) {
            return "character";
        } else {
            return "";
        }
    }
}
