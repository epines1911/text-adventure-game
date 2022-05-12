package edu.uob;

import edu.uob.entity.Artefact;
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
            throw new GameException("Please enter name and command");
        }
        setPlayer(names[0]);
        String[] tokens = names[1].split("\\s+");
        String trigger = tokens[1].toLowerCase();
        switch (trigger.toUpperCase()) {
            case "INV", "INVENTORY" -> inventoryAction();
            case "GET" -> getAction(tokens);
            case "DROP" -> dropAction(tokens);
            case "GOTO" -> gotoAction(tokens);
            case "LOOK" -> lookAction();
            default -> normalActionParser(trigger, tokens);
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

    public boolean isValidName(String aimString) {
        if (aimString.length() < 1) {return false;}
        // ^[a-zA-Z]+ means the aimString should start with an english character,
        // and the uppercase is ignored.
        // [-'\sa-zA-Z]* means after the first character,
        // there would be 0 or more english characters or symbols,
        // including space, "-", and "'".
        return aimString.matches("^[a-zA-Z]+[-\'\\sa-zA-Z]*");
    }

    private void inventoryAction() {
        HashMap<String, Artefact> inventory = model.getCurrentPlayer().getInventory();
        message += "There are " + inventory.size() + " artefacts in your inventory: \n";
        for (Artefact artefact :
                inventory.values()) {
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
        String name = tokens[2]; //todo 如果允许一次性捡多个东西的话就挨个匹配tokens里面的string
        HashMap<String, Artefact> artefacts = model.getCurrentLocation().getArtefacts();
        if (!artefacts.containsKey(name)) {
            throw new GameException("There is no artefact named " + name + " in the current location");
        }
        Artefact newArtefact = artefacts.get(name);
        model.getCurrentPlayer().getInventory().put(name, newArtefact);
        artefacts.remove(name);
        message = "You picked up a " + name;
    }

    private void dropAction(String[] tokens) throws GameException {
        if (tokens.length < 3) {
            throw new GameException("Please enter the item's name. e.g: drop name");
        }
        String name = tokens[2]; //todo 如果允许一次性扔多个东西的话就挨个匹配tokens里面的string
        HashMap<String, Artefact> inventory = model.getCurrentPlayer().getInventory();
        if (!inventory.containsKey(name)) {
            throw new GameException("There is no artefact named " + name + " in the inventory");
        }
        HashMap<String, Artefact> artefacts = model.getCurrentLocation().getArtefacts();
        Artefact newArtefact = inventory.get(name);
        artefacts.put(name, newArtefact);
        model.getCurrentPlayer().getInventory().remove(name);
        message = "You dropped a " + name;
    }

    private void gotoAction(String[] tokens) throws GameException {
        if (tokens.length < 3) {
            throw new GameException("Please enter the location's name. e.g: goto name");
        }
        String locationName = tokens[2];
        //todo 为了减少complexity把两个错误项合并了。可能信息不够准确。不知道要不要改回来。
//        if (!model.getLocationsMap().containsKey(locationName)) {
//            throw new GameException("There is no location named " + locationName);
//        }
        if (!model.getLocationsMap().containsKey(locationName) ||
                !model.getCurrentLocation().getPaths().containsKey(locationName)) {
            throw new GameException("There is no path to go to " + locationName);
        }
        model.setCurrentLocation(locationName);
        message += "You are in " + locationName + "\n";
        lookAction();
    }

    private void lookAction() {
        HashMap<String, Artefact> artefacts = model.getCurrentLocation().getArtefacts();
        message += "There are " + artefacts.size() + " artefacts in this location: \n";
        for (Artefact artefact: artefacts.values()) {
            message += artefact.getName();
            message += ": ";
            message += artefact.getDescription();
            message += "\n";
        }
        Set<String> paths = model.getCurrentLocation().getPaths().keySet();
        message += "There are " + paths.size() + " paths: \n";
        for (String pathName :
                paths) {
            message += pathName;
            message += "\n";
        }
    }

    private void normalActionParser(String trigger, String[] tokens) throws GameException {
        if (!model.getActionsMap().containsKey(trigger)) {
            throw new GameException("Cannot find " + trigger + ", please check this word.");
        }
        HashSet<GameAction> actions = model.getActionsMap().get(trigger);
        // find which action should be executed
        GameAction aimAction = null;
        for (GameAction action: actions) {
            ArrayList<String> subjects = action.getSubjects();
            int counter = 0;
            for (int i = 0; i < subjects.size(); i++) {
                if (Arrays.asList(tokens).contains(subjects.get(i))) {
                    counter += 1;//todo action里的subjects用arraylist记录的，不咋地，无法防止重复。
                }
                if (counter == subjects.size()) {
                    aimAction = action;
                }
            }
        }
        if (aimAction == null) {
            throw new GameException("Cannot find an action related to " + trigger);
        } else {
            executeAction(aimAction);
        }
    }

    private void executeAction(GameAction action) throws GameException {
        HashMap<String, Artefact> inventory = model.getCurrentPlayer().getInventory();
        ArrayList<String> consumedItems = action.getConsumed();
        for (String item : consumedItems) {
            if (!inventory.containsKey(item)) {
                throw new GameException(item + " is not in your inventory.");
            }
        }
        // Now I confirmed all the consumed entities are in the player's inventory, then execute this action
        Location storeroom = model.getStoreroom();
        // move consumed artefacts from inventory to storeroom
        for (String item : consumedItems) {
            Artefact consumed = inventory.get(item);
            storeroom.addArtefact(consumed);
            inventory.remove(item);
        }
        ArrayList<String> producedItems = action.getProduced();
        // move produced artefacts from storeroom to inventory
        for (String item : producedItems) {
            Artefact produced = storeroom.getArtefacts().get(item);
            inventory.put(item, produced);
            storeroom.getArtefacts().remove(item);
        }
        message = action.getNarration();
    }

    public String getMessage() {
        return message;
    }
}
