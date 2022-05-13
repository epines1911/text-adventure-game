package edu.uob;

import edu.uob.entity.*;
import edu.uob.entity.Character;

import java.util.*;

public class GameController {
    GameModel model;
    String message;
    public GameController(GameModel gameModel) {
        model = gameModel;
    }

    public void commandParser(String command) throws GameException {
        message = "";
        // get and check player's name
        String[] names = command.split(":");
        if (names.length < 2) {
            throw new GameException("Please enter a command");
        }
        setPlayer(names[0]);
        // get and check every command
        String[] originTokens = names[1].toLowerCase().split("\\s+");
        HashSet<String> tokens = getUniqueToken(originTokens);
        if (tokens.contains("and")) {
            throw new GameException("Please enter only one entity in one command.");
        }
        String trigger = checkDoubleTrigger(tokens);
        switch (trigger.toUpperCase()) {
            case "INV", "INVENTORY" -> inventoryAction(tokens);
            case "GET" -> getAction(tokens);
            case "DROP" -> dropAction(tokens);
            case "GOTO" -> gotoAction(tokens);
            case "LOOK" -> lookAction();
            case "HEALTH" -> healthAction(tokens);
            default -> normalActionParser(names[1].toLowerCase());
        }
    }
// add a new player or set current player
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
// delete empty string and duplicated string in a token array
    private HashSet<String> getUniqueToken(String[] tokens) {
        HashSet<String> filter = new HashSet<>();
        for (String token : tokens) {
            if (token.length() > 0) {
                filter.add(token);
            }
        }
        return filter;
    }

    private String checkDoubleTrigger(HashSet<String> tokens) throws GameException {
        HashSet<String> builtInTriggers = getBuiltInTriggerSet();
        int counter = 0;
        String aimTrigger = "";
        for (String trigger : builtInTriggers) {
            for (String token : tokens) {
                if (token.equalsIgnoreCase(trigger)) {
                    counter += 1;
                    if (counter > 1) {
                        throw new GameException("Find more than one built-in action to be executed.");
                    }
                    aimTrigger = trigger;
                }
            }
        }
        return aimTrigger;
    }
    // get a string set with all built-in command triggers
    private HashSet<String> getBuiltInTriggerSet() {
        HashSet<String> builtInTriggers = new HashSet<>();
        builtInTriggers.add("inventory");
        builtInTriggers.add("inv");
        builtInTriggers.add("get");
        builtInTriggers.add("drop");
        builtInTriggers.add("goto");
        builtInTriggers.add("look");
        builtInTriggers.add("health");
        return builtInTriggers;
    }
    // check if this string is a built-in command trigger
    private boolean isBuiltInTrigger(String token) {
        HashSet<String> builtInTriggers = getBuiltInTriggerSet();
        for (String trigger: builtInTriggers) {
            if (token.equalsIgnoreCase(trigger)) {
                return true;
            }
        }
        return false;
    }
// built-in command: inventory
    private void inventoryAction(HashSet<String> tokens) throws GameException {
        if (tokens.size() > 1) {
            throw new GameException("If you want to check your inventory, " +
                    "please enter: 'inv' or 'inventory'.");
        }
        HashMap<String, Artefact> inventory = model.getCurrentPlayer().getInventory();
        message += "There are " + inventory.size() + " artefacts in your inventory: \n";
        for (Artefact artefact : inventory.values()) {
            // get every artefact's name and description
            message += getEntityInfo(artefact);
        }
    }
// built-in command: get
    private void getAction(HashSet<String> tokens) throws GameException {
        if (tokens.size() < 2 || tokens.size() > 3) {
            throw new GameException("Please enter one item's name. e.g: get name");
        }
        // delete the string of 'get' in tokens
        tokens.remove("get");
        HashMap<String, Artefact> artefacts = model.getCurrentPlayer().getLocation().getArtefacts();
        Set<String> key = artefacts.keySet();
        // check if there are more than one valid subjects in a command
        String aimName = checkDoubleSubjects(tokens, key);
        Artefact newArtefact = artefacts.get(aimName);
        model.getCurrentPlayer().getInventory().put(aimName, newArtefact);
        artefacts.remove(aimName);
        message = "You picked up a " + aimName;
    }

    private String checkDoubleSubjects(HashSet<String> tokens, Set<String> key) throws GameException {
        String aimName = null;
        for (String token : tokens) {
            if (key.contains(token)) {
                if (aimName == null || aimName.equalsIgnoreCase(token)) {
                    aimName = token.toLowerCase();
                } else {
            // if the program find more than one valid subject in a command, throw an exception.
                    throw new GameException("Find more than one subject to get.");
                }
            }
            //todo
//            else if (!isArticle(token) && !isBuiltInTrigger(token)) {
//                throw new GameException("Invalid word in command.");
//            }
        }
        if (aimName == null) {
            throw new GameException("Cannot find this subject in the current location.");
        }
        return aimName;
    }
// check if this string is an article (e.g. a, an, the, etc.)
    private boolean isArticle(String token) {
        HashSet<String> articles = new HashSet<>();
        articles.add("a");
        articles.add("the");
        articles.add("that");
        articles.add("this");
        articles.add("an");
        for (String article: articles) {
            if (token.equalsIgnoreCase(article)) {
                return true;
            }
        }
        return false;
    }
// built-in command: drop
    private void dropAction(HashSet<String> tokens) throws GameException {
        if (tokens.size() < 2 || tokens.size() > 3) {
            throw new GameException("Please enter one item's name. e.g: drop name");
        }
        // delete the string of 'drop' in tokens
        tokens.remove("drop");
        HashMap<String, Artefact> inventory = model.getCurrentPlayer().getInventory();
        Set<String> key = inventory.keySet();
        // check if there are more than one valid subjects in a command
        String aimName = checkDoubleSubjects(tokens, key);
        Artefact newArtefact = inventory.get(aimName);
        model.getCurrentPlayer().getLocation().addArtefact(newArtefact);
        model.getCurrentPlayer().getInventory().remove(aimName);
        message = "You dropped a " + aimName;
    }
// built-in command: goto
    private void gotoAction(HashSet<String> tokens) throws GameException {
        if (tokens.size() < 2) {
            throw new GameException("Please enter the location's name. e.g: goto name");
        }
        // delete the string of 'goto' in tokens
        tokens.remove("goto");
        Set<String> key = model.getCurrentPlayer().getLocation().getPaths().keySet();
        // check if there are more than one valid subjects in a command
        String aimName = checkDoubleSubjects(tokens, key);
        model.getCurrentPlayer().setLocation(model.getLocation(aimName));
        // return detailed information of a new location
        lookAction();
    }
// built-in command: look
    private void lookAction() {
        message += "You are in " + model.getCurrentPlayer().getLocation().getName() +": "
                + model.getCurrentPlayer().getLocation().getDescription() +"\n";
        HashMap<String, Artefact> artefacts = model.getCurrentPlayer().getLocation().getArtefacts();
        message += "There are " + artefacts.size() + " artefacts in this location: \n";
        for (Artefact artefact: artefacts.values()) {
            message += getEntityInfo(artefact);
        }
        HashMap<String, Furniture> furniture = model.getCurrentPlayer().getLocation().getFurniture();
        message += "There are " + furniture.size() + " furniture in this location: \n";
        for (Furniture furnitureItem: furniture.values()) {
            message += getEntityInfo(furnitureItem);
        }
        HashMap<String, Character> characters = model.getCurrentPlayer().getLocation().getCharacters();
        message += "There are " + characters.size() + " characters in this location: \n";
        for (Character character: characters.values()) {
            message += getEntityInfo(character);
        }
        HashMap<String, Player> players = model.getPlayers();
        message += "There are " + players.size() + " players in this location: \n";
        for (Player player: players.values()) {
            message += getEntityInfo(player);
        }
        Set<String> paths = model.getCurrentPlayer().getLocation().getPaths().keySet();
        message += "There are " + paths.size() + " paths: \n";
        for (String pathName : paths) {
            message += pathName;
            message += "\n";
        }
    }
// return a string including an entity's name and description
    private String getEntityInfo(GameEntity entity) {
        String information = "";
        information += entity.getName();
        information += ": ";
        information += entity.getDescription();
        information += "\n";
        return information;
    }
// built-in command: health
    private void healthAction(HashSet<String> tokens) throws GameException {
        if (tokens.size() > 1) {
            throw new GameException("If you want to check your health, " +
                    "please enter: 'health'.");
        }
        message = "Your health is " + model.getCurrentPlayer().getHealthLevel();
    }
// parse every non-built-in command
    private void normalActionParser(String command) throws GameException {
        TreeMap<String, HashSet<GameAction>> actionMap = model.getActionsMap();
        Set<String> triggers = actionMap.keySet();
        // check if there are more than one valid game action's set
        HashSet<GameAction> aimActionSet = null;
        for (String trigger : triggers) {
            if (command.toLowerCase().contains(trigger)) {
                HashSet<GameAction> oneActionSet = actionMap.get(trigger);
                if (aimActionSet == null || aimActionSet == oneActionSet) {
                    aimActionSet = oneActionSet;
                    command = command.toLowerCase().replaceFirst(trigger, "");
                } else {
                    throw new GameException("Find more than one valid trigger in command.");
                }
            }
        }
        if (aimActionSet == null) {
            throw new GameException("Cannot find a valid trigger in command.");
        }
        // compare subjects and find which action should be executed
        checkSubjects(aimActionSet, command);
    }

    private void checkSubjects(HashSet<GameAction> actions, String command) throws GameException {
        GameAction aimAction = null;
        for (GameAction action: actions) {
            HashSet<String> subjects = action.getSubjects();
            int counter = 0;
            // check if the command contains at least one required subject
            for (String subject : subjects) {
                if (command.toLowerCase().contains(subject)) {
                    counter += 1;
                }
            }
            if (counter <= subjects.size() && counter > 0) {
        // if the program find more than one action could be executed, throw an exception
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
        HashSet<String> consumedItems = action.getConsumed();
// check if all the consumed entities are available, then reduce health value or move them to storeroom.
        for (String item : consumedItems) {
            if (item.equalsIgnoreCase("health")) {
                model.getCurrentPlayer().healthLevelDown();
            } else if (!checkInventory(item) && !checkArtefacts(item) && !checkFurniture(item)
                    && !checkCharacters(item) && !checkPaths(item)) {
// if this consumed subject isn't 'health' or isn't available as an entity, throw an exception
                throw new GameException(item + " is not available.");
            }
        }
        if (model.getCurrentPlayer().isDeath()) {
            restartGame();
        } else {
            HashSet<String> producedItems = action.getProduced();
            // produce new entities and add them to current location
            for (String item : producedItems) {
                produceEntity(item);
            }
            message = action.getNarration();
        }
    }
// check if the consumed subject is available in inventory, then remove it.
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
// check if the consumed subject is available as an artefact in current location, then remove it.
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
// check if the consumed subject is available as furniture in current location, then remove it.
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
// check if the consumed subject is available as a character in current location, then remove it.
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
// check if the consumed subject is available as a location in current location's path, then remove it.
    private boolean checkPaths(String name) {
        HashMap<String, Location> paths = model.getCurrentPlayer().getLocation().getPaths();
        if (!paths.containsKey(name)) {
            return false;
        }
        paths.remove(name);
        return true;
    }
// produce a new entity according to its type, then add it to current location
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

    private void restartGame() {
        // when a player died, empty the player's inventory
        HashMap<String, Artefact> inventory = model.getCurrentPlayer().getInventory();
        Location currentLocation = model.getCurrentPlayer().getLocation();
        for (Artefact artefact : inventory.values()) {
            currentLocation.addArtefact(artefact);
        }
        inventory.clear();
        // then send the player back to birthplace with full health status
        model.getCurrentPlayer().setFullHealth();
        model.getCurrentPlayer().setLocation(model.getBirthPlace());
        message = "You died and lost all the artefacts in your inventory. When you open your eyes, " +
                "you found you are in " + model.getCurrentPlayer().getLocation().getName();
    }

    public String getMessage() {
        return message;
    }
}
