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

    public void commandParser(String command, String[] tokens) throws GameException {
        message = "";
        String trigger = tokens[0].toLowerCase();
        if (!model.getActionsMap().containsKey(trigger)) {
            throw new GameException("Cannot find " + trigger + ", please check this word.");
        }
        switch (trigger.toUpperCase()) {
            case "INV", "INVENTORY" -> inventoryAction();
            case "GET" -> getAction(tokens);
            case "DROP" -> dropAction(tokens);
            case "GOTO" -> gotoAction(tokens);
            case "LOOK" -> lookAction();
            default -> normalActionParser(trigger, tokens);
        }
    }

    private void inventoryAction() {
        HashMap<String, Artefact> inventory = model.getCurrentPlayer().getInventory();
        message += "There are " + inventory.size() + " artefacts in your inventory: \n";
        Iterator<String> names = inventory.keySet().iterator();
        while (names.hasNext()) {
            message += names;
            message += "\n";
        }
    }

    private void getAction(String[] tokens) throws GameException {
        if (tokens.length < 2) {
            throw new GameException("Please enter the item's name. e.g: get name");
        }
        String name = tokens[1]; //todo 如果允许一次性捡多个东西的话就挨个匹配tokens里面的string
        HashMap<String, Artefact> artefacts = model.getCurrentLocation().getArtefacts();
        if (!artefacts.containsKey(name)) {
            throw new GameException("There is no artefact named " + name + " in the current location");
        }
        Artefact newArtefact = artefacts.get(name);
        model.getCurrentPlayer().getInventory().put(name, newArtefact);
        artefacts.remove(name);
        message = "You picked up " + name;
    }

    private void dropAction(String[] tokens) throws GameException {
        if (tokens.length < 2) {
            throw new GameException("Please enter the item's name. e.g: drop name");
        }
        String name = tokens[1]; //todo 如果允许一次性扔多个东西的话就挨个匹配tokens里面的string
        HashMap<String, Artefact> inventory = model.getCurrentPlayer().getInventory();
        if (!inventory.containsKey(name)) {
            throw new GameException("There is no artefact named " + name + " in the inventory");
        }
        HashMap<String, Artefact> artefacts = model.getCurrentLocation().getArtefacts();
        Artefact newArtefact = inventory.get(name);
        artefacts.put(name, newArtefact);
        model.getCurrentPlayer().getInventory().remove(name);
        message = "You dropped " + name;
    }

    private void gotoAction(String[] tokens) throws GameException {
        if (tokens.length < 2) {
            throw new GameException("Please enter the location's name. e.g: goto name");
        }
        String locationName = tokens[1];
        //todo 为了减少complexity把两个错误项合并了。可能信息不够准确。不知道要不要改回来。
//        if (!model.getLocationsMap().containsKey(locationName)) {
//            throw new GameException("There is no location named " + locationName);
//        }
        if (!model.getLocationsMap().containsKey(locationName) ||
                !model.getCurrentLocation().getPaths().containsKey(locationName)) {
            throw new GameException("There is no path to go to " + locationName);
        }
        model.setCurrentLocation(locationName);
        message = "Now you are at " + locationName;
    }

    private void lookAction() {
        HashMap<String, Artefact> artefacts = model.getCurrentLocation().getArtefacts();
        message += "There are " + artefacts.size() + " artefacts in this location: \n";
        Iterator<String> names = artefacts.keySet().iterator();
        while (names.hasNext()) {
            message += names;
            message += "\n";
        }
    }

    private void normalActionParser(String trigger, String[] tokens) throws GameException {
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
