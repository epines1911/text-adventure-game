package edu.uob;

import edu.uob.entity.Artefact;
import edu.uob.entity.Character;
import edu.uob.entity.Furniture;
import edu.uob.entity.Location;

import java.util.*;

public class GameController {
    GameModel model;
    String message;
    public GameController(GameModel gameModel) {
        model = gameModel;
    }

    public void commandParser(String command) throws GameException {
        message = "";
        String[] names = command.split(":");
        if (names.length < 2) {
            throw new GameException("Please enter a command");
        }
        setPlayer(names[0]);
        String[] tokens = names[1].toLowerCase().split("\\s+");
        if (Arrays.asList(tokens).contains("and")) {
            throw new GameException("Please enter only one entity in one command.");
        }
        String trigger = checkDoubleTrigger(names[1].toLowerCase());
        switch (trigger.toUpperCase()) {
            case "INV", "INVENTORY" -> inventoryAction();
            case "GET" -> getAction(tokens);
            case "DROP" -> dropAction(tokens);
            case "GOTO" -> gotoAction(tokens);
            case "LOOK" -> lookAction();
            case "HEALTH" -> healthAction();
            default -> normalActionParser(names[1].toLowerCase(), tokens);
        }
    }

    private void setPlayer(String name) throws GameException {
        if (!isValidName(name)) {
            throw new GameException("Player's name is invalid");
        }
        if (!model.getPlayers().containsKey(name)) {
            model.addPlayer(name);
            model.setCurrentPlayer(name);
        }
        if (!model.getCurrentPlayer().getName().equalsIgnoreCase(name)) {
            model.setCurrentPlayer(name);
        }
    }

    private boolean isValidName(String aimString) {
        if (aimString.length() < 1) {return false;}
        // ^[a-zA-Z]+ means the aimString should start with an english character,
        // and the uppercase is ignored.
        // [-'\sa-zA-Z]* means after the first character,
        // there would be 0 or more english characters or symbols,
        // including space, "-", and "'".
        return aimString.matches("^[a-zA-Z]+[-\'\\sa-zA-Z]*");
    }

    private String checkDoubleTrigger(String command) throws GameException {
        HashSet<String> builtInTrigger = new HashSet<>();
        builtInTrigger.add("inventory");
        builtInTrigger.add("inv");
        builtInTrigger.add("get");
        builtInTrigger.add("drop");
        builtInTrigger.add("goto");
        builtInTrigger.add("look");
        builtInTrigger.add("health");
        int counter = 0;
        String aimTrigger = "";
        for (String trigger : builtInTrigger) {
            if (command.contains(trigger)) {
                counter += 1;
                if (counter > 1) {
                    throw new GameException("Find more than one built-in action to be executed.");
                }
                aimTrigger = trigger;
            }
        }
        return aimTrigger;
    }

    private void inventoryAction() {
        HashMap<String, Artefact> inventory = model.getCurrentPlayer().getInventory();
        message += "There are " + inventory.size() + " artefacts in your inventory: \n";
        for (Artefact artefact : inventory.values()) {
            message += artefact.getName();
            message += ": ";
            message += artefact.getDescription();
            message += "\n";
        }
    }

    private void getAction(String[] tokens) throws GameException {
        if (tokens.length < 3) {
            throw new GameException("Please enter the item's name. e.g: get name");
        }
        tokens[Arrays.asList(tokens).indexOf("get")] = "";// delete the string of 'trigger' in tokens
        HashMap<String, Artefact> artefacts = model.getCurrentPlayer().getLocation().getArtefacts();
        Set<String> key = artefacts.keySet();
        String aimName = checkDoubleSubjects(tokens, key);
        Artefact newArtefact = artefacts.get(aimName);
        model.getCurrentPlayer().getInventory().put(aimName, newArtefact);
        artefacts.remove(aimName);
        message = "You picked up a " + aimName;
    }

    private String checkDoubleSubjects(String[] tokens, Set<String> key) throws GameException {
        String aimName = null;
        for (String token : tokens) {
            if (key.contains(token.toLowerCase())) {
                if (aimName == null || aimName.equalsIgnoreCase(token)) {
                    aimName = token.toLowerCase();
                } else {
                    throw new GameException("Find more than one artefact to get.");
                }
            }
        }
        if (aimName == null) {
            throw new GameException("Cannot find this artefact in the current location.");
        }
        return aimName;
    }

    private void dropAction(String[] tokens) throws GameException {
        if (tokens.length < 3) {
            throw new GameException("Please enter the item's name. e.g: drop name");
        }
        tokens[Arrays.asList(tokens).indexOf("drop")] = "";// delete the string of 'trigger' in tokens
        HashMap<String, Artefact> inventory = model.getCurrentPlayer().getInventory();
        Set<String> key = inventory.keySet();
        String aimName = checkDoubleSubjects(tokens, key);
        HashMap<String, Artefact> artefacts = model.getCurrentPlayer().getLocation().getArtefacts();
        Artefact newArtefact = inventory.get(aimName);
        artefacts.put(aimName, newArtefact);
        model.getCurrentPlayer().getInventory().remove(aimName);
        message = "You dropped a " + aimName;
    }

    private void gotoAction(String[] tokens) throws GameException {
        if (tokens.length < 3) {
            throw new GameException("Please enter the location's name. e.g: goto name");
        }
        tokens[Arrays.asList(tokens).indexOf("goto")] = "";// delete the string of 'trigger' in tokens
        Set<String> key = model.getCurrentPlayer().getLocation().getPaths().keySet();
        String aimName = checkDoubleSubjects(tokens, key);
        model.getCurrentPlayer().setLocation(model.getLocation(aimName));
            message += "You are in " + aimName +": "
                + model.getCurrentPlayer().getLocation().getDescription() +"\n";
        lookAction();
    }

    private void lookAction() {
        //todo 这里面代码重复度高，说不定可以浓缩。
        HashMap<String, Artefact> artefacts = model.getCurrentPlayer().getLocation().getArtefacts();
        message += "There are " + artefacts.size() + " artefacts in this location: \n";
        for (Artefact artefact: artefacts.values()) {
            message += artefact.getName();
            message += ": ";
            message += artefact.getDescription();
            message += "\n";
        }
        HashMap<String, Furniture> furniture = model.getCurrentPlayer().getLocation().getFurniture();
        message += "There are " + furniture.size() + " furniture in this location: \n";
        for (Furniture furnitureItem: furniture.values()) {
            message += furnitureItem.getName();
            message += ": ";
            message += furnitureItem.getDescription();
            message += "\n";
        }
        HashMap<String, Character> characters = model.getCurrentPlayer().getLocation().getCharacters();
        message += "There are " + characters.size() + " characters in this location: \n";
        for (Character character: characters.values()) {
            message += character.getName();
            message += ": ";
            message += character.getDescription();
            message += "\n";
        }
        Set<String> paths = model.getCurrentPlayer().getLocation().getPaths().keySet();
        message += "There are " + paths.size() + " paths: \n";
        for (String pathName : paths) {
            message += pathName;
            message += "\n";
        }
    }

    private void healthAction() {
        message = "Your health is " + model.getCurrentPlayer().getHealthLevel();
    }

    private void normalActionParser(String command, String[] tokens) throws GameException {
        TreeMap<String, HashSet<GameAction>> actionMap = model.getActionsMap();
        Set<String> triggers = actionMap.keySet();
        HashSet<GameAction> aimActionAet = null;
        for (String trigger : triggers) {
            if (command.toLowerCase().contains(trigger)) {
                HashSet<GameAction> oneActionSet = actionMap.get(trigger);
                if (aimActionAet == null || aimActionAet == oneActionSet) {
                    aimActionAet = oneActionSet;
                    command = command.toLowerCase().replaceFirst(trigger, "");
                } else {
                    throw new GameException("Find more than one valid trigger in command.");
                }
            }
        }
        if (aimActionAet == null) {
            throw new GameException("Cannot find a valid trigger in command.");
        }
        // compare subjects and find which action should be executed
        checkSubjects(aimActionAet, command);
    }

    private void checkSubjects(HashSet<GameAction> actions, String command) throws GameException {
        GameAction aimAction = null;
        for (GameAction action: actions) {
            ArrayList<String> subjects = action.getSubjects();
            int counter = 0;
            for (String subject : subjects) {
                if (command.toLowerCase().contains(subject)) {
                    counter += 1;//todo action里的subjects用arraylist记录的，不咋地，无法防止重复。
                }
            }
            if (counter <= subjects.size()) {
                if (aimAction != null && aimAction != action) {
                    throw new GameException("Find more than one action to be executed.");
                }
                aimAction = action;
            }
        }
        if (aimAction == null) {
            throw new GameException("Cannot find a specific action.");
        }
        executeAction(aimAction);
    }

    private void executeAction(GameAction action) throws GameException {
//        Location currentLocation = model.getCurrentPlayer().getLocation();//todo
        HashMap<String, Artefact> inventory = model.getCurrentPlayer().getInventory();
//        HashMap<String, Furniture> furniture = currentLocation.getFurniture();//todo
//        HashMap<String, Character> characters = currentLocation.getCharacters();//todo
//        HashMap<String, Location> paths = currentLocation.getPaths();//todo
        Location storeroom = model.getStoreroom();
        ArrayList<String> consumedItems = action.getConsumed();
        // check all the consumed entities are available,
        // then move them to storeroom.
        for (String item : consumedItems) {
            if (item.equalsIgnoreCase("health")) {
                model.getCurrentPlayer().healthLevelDown();
            } else if (!checkInventory(item) && !checkArtefacts(item) && !checkFurniture(item)
                    && !checkCharacters(item) && !checkPaths(item)) {
                throw new GameException(item + " is not available.");
            }
        }
        ArrayList<String> producedItems = action.getProduced();
        // move produced entities to current location
        for (String item : producedItems) {
            produceEntity(item);
        }
        message = action.getNarration();
    }
    //todo 这几个check或许可以参照produceEntities给合并起来。
    private boolean checkInventory(String name) {
        HashMap<String, Artefact> inventory = model.getCurrentPlayer().getInventory();
        if (!inventory.containsKey(name)) {
            return false;
        }
        Artefact consumed = inventory.get(name);
        model.getStoreroom().addArtefact(consumed);
        inventory.remove(name);
        return true;
    }

    private boolean checkArtefacts(String name) {
        HashMap<String, Artefact> artefacts = model.getCurrentPlayer().getLocation().getArtefacts();
        if (!artefacts.containsKey(name)) {
            return false;
        }
        Artefact consumed = artefacts.get(name);
        model.getStoreroom().addArtefact(consumed);
        artefacts.remove(name);
        return true;
    }

    private boolean checkFurniture(String name) {
        HashMap<String, Furniture> furniture = model.getCurrentPlayer().getLocation().getFurniture();
        if (!furniture.containsKey(name)) {
            return false;
        }
        Furniture consumed = furniture.get(name);
        model.getStoreroom().addFurniture(consumed);
        furniture.remove(name);
        return true;
    }

    private boolean checkCharacters(String name) {
        HashMap<String, Character> character = model.getCurrentPlayer().getLocation().getCharacters();
        if (!character.containsKey(name)) {
            return false;
        }
        Character consumed = character.get(name);
        model.getStoreroom().addCharacter(consumed);
        character.remove(name);
        return true;
    }

    private boolean checkPaths(String name) {
        HashMap<String, Location> paths = model.getCurrentPlayer().getLocation().getPaths();
        if (!paths.containsKey(name)) {
            return false;
        }
        Location consumed = paths.get(name);//todo delete
        paths.remove(name);
        return true;
    }

    private void produceEntity(String name) {
        Location currentLocation = model.getCurrentPlayer().getLocation();
        Location storeroom = model.getStoreroom();
        String type = model.getEntityType(name);
        switch (type) {
            case "artefact" -> {
                Artefact entity = storeroom.getArtefacts().get(name);
                currentLocation.addArtefact(entity);
                storeroom.getArtefacts().remove(name);
            }
            case "furniture" -> {
                Furniture entity = storeroom.getFurniture().get(name);
                currentLocation.addFurniture(entity);
                storeroom.getFurniture().remove(name);
            }
            case "character" -> {
                Character entity = storeroom.getCharacters().get(name);
                currentLocation.addCharacter(entity);
                storeroom.getCharacters().remove(name);
            }
            case "location" -> {
                Location entity = model.getLocationsMap().get(name);
                currentLocation.addPaths(entity);
            }
            default -> {
                if (name.equalsIgnoreCase("health")) {
                    model.getCurrentPlayer().healthLevelUp();
                }
            }
        }
    }

    public String getMessage() {
        return message;
    }
}
