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
//        String trigger = tokens[1].toLowerCase();
        String trigger = checkDoubleTrigger(names[1].toLowerCase());
        switch (trigger.toUpperCase()) {
            case "INV", "INVENTORY" -> inventoryAction();
            case "GET" -> getAction(tokens);
            case "DROP" -> dropAction(tokens);
            case "GOTO" -> gotoAction(tokens);
            case "LOOK" -> lookAction();
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
        int counter = 0;
        String aimTrigger = null;
        for (String trigger : builtInTrigger) {
            if (command.contains(trigger)) {
                counter += 1;
            }
            if (counter > 1) {
                throw new GameException("Find more than one built-in action to be executed.");
            }
            if (aimTrigger == null) {
                aimTrigger = trigger;
            }
        }
        if (counter == 0) {
            throw new GameException("Cannot find an built-in action.");
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
        tokens[1] = ""; // delete the string of 'trigger' in tokens
        HashMap<String, Artefact> artefacts = model.getCurrentLocation().getArtefacts();
        Set<String> key = artefacts.keySet();
        //todo 拆成小函数，从这开始
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
        // TODO: 2022/5/12 拆成小函数，到这结束
//        String name = tokens[2]; //todo delete
//        if (!artefacts.containsKey(name)) {
//            throw new GameException("There is no artefact named " + name + " in the current location");
//        } //todo delete
        Artefact newArtefact = artefacts.get(aimName);
        model.getCurrentPlayer().getInventory().put(aimName, newArtefact);
        artefacts.remove(aimName);
        message = "You picked up a " + aimName;
    }

    private void dropAction(String[] tokens) throws GameException {
        if (tokens.length < 3) {
            throw new GameException("Please enter the item's name. e.g: drop name");
        }
        tokens[1] = ""; // delete the string of 'trigger' in tokens
        HashMap<String, Artefact> inventory = model.getCurrentPlayer().getInventory();
        Set<String> key = inventory.keySet();
        String aimName = null;
        for (String token : tokens) {
            if (key.contains(token.toLowerCase())) {
                if (aimName == null || aimName.equalsIgnoreCase(token)) {
                    aimName = token.toLowerCase();
                } else {
                    throw new GameException("Find more than one artefact to drop.");
                }
            }
        }
        if (aimName == null) {
            throw new GameException("Cannot find this artefact in the inventory.");
        }
//        String name = tokens[2];//todo delete
//        if (!inventory.containsKey(name)) {
//            throw new GameException("There is no artefact named " + name + " in the inventory");
//        }//todo delete
        HashMap<String, Artefact> artefacts = model.getCurrentLocation().getArtefacts();
        Artefact newArtefact = inventory.get(aimName);
        artefacts.put(aimName, newArtefact);
        model.getCurrentPlayer().getInventory().remove(aimName);
        message = "You dropped a " + aimName;
    }

    private void gotoAction(String[] tokens) throws GameException {
        if (tokens.length < 3) {
            throw new GameException("Please enter the location's name. e.g: goto name");
        }
        tokens[1] = ""; // delete the string of 'trigger' in tokens
        Set<String> key = model.getCurrentLocation().getPaths().keySet();
        String aimName = null;
        for (String token : tokens) {
            if (key.contains(token.toLowerCase())) {
                if (aimName == null || aimName.equalsIgnoreCase(token)) {
                    aimName = token.toLowerCase();
                } else {
                    throw new GameException("Find more than one location to go.");
                }
            }
        }
        if (aimName == null) {
            throw new GameException("Cannot find a path to go to this location.");
        }
//        String locationName = tokens[2];
//        if (!model.getCurrentLocation().getPaths().containsKey(locationName)) {
//            throw new GameException("There is no path to go to " + locationName);
//        }
        model.setCurrentLocation(aimName);
            message += "You are in " + aimName +": "
                + model.getCurrentLocation().getDescription() +"\n";
        lookAction();
    }

    private void lookAction() {
        //todo 这里面代码重复度高，说不定可以浓缩。
        HashMap<String, Artefact> artefacts = model.getCurrentLocation().getArtefacts();
        message += "There are " + artefacts.size() + " artefacts in this location: \n";
        for (Artefact artefact: artefacts.values()) {
            message += artefact.getName();
            message += ": ";
            message += artefact.getDescription();
            message += "\n";
        }
        HashMap<String, Furniture> furniture = model.getCurrentLocation().getFurniture();
        message += "There are " + furniture.size() + " furniture in this location: \n";
        for (Furniture furnitureItem: furniture.values()) {
            message += furnitureItem.getName();
            message += ": ";
            message += furnitureItem.getDescription();
            message += "\n";
        }
        HashMap<String, Character> characters = model.getCurrentLocation().getCharacters();
        message += "There are " + characters.size() + " characters in this location: \n";
        for (Character character: characters.values()) {
            message += character.getName();
            message += ": ";
            message += character.getDescription();
            message += "\n";
        }
        Set<String> paths = model.getCurrentLocation().getPaths().keySet();
        message += "There are " + paths.size() + " paths: \n";
        for (String pathName : paths) {
            message += pathName;
            message += "\n";
        }
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

//        if (!model.getActionsMap().containsKey(trigger)) {
//            throw new GameException("Cannot find " + trigger + ", please check this word.");
//        }
//        HashSet<GameAction> actions = model.getActionsMap().get(trigger);

        // find which action should be executed
        checkSubjects(aimActionAet, command);



//        GameAction aimAction = null;
//        for (GameAction action: actions) {
//            ArrayList<String> subjects = action.getSubjects();
//            int counter = 0;
//            for (int i = 0; i < subjects.size(); i++) {
//                if (Arrays.asList(tokens).contains(subjects.get(i))) {
//                    counter += 1;//todo action里的subjects用arraylist记录的，不咋地，无法防止重复。
//                }
//                if (counter == subjects.size()) {
//                    aimAction = action;
//                }
//            }
//        }
//        if (aimAction == null) {
//            throw new GameException("Cannot find an action related to " + trigger);
//        } else {
//            executeAction(aimAction);
//        }
    }

    private void checkSubjects(HashSet<GameAction> actions, String command) throws GameException {
        GameAction aimAction = null;
        for (GameAction action: actions) {
            ArrayList<String> subjects = action.getSubjects();
            int counter = 0;
            for (int i = 0; i < subjects.size(); i++) {
                if (command.toLowerCase().contains(subjects.get(i))) {
                    counter += 1;//todo action里的subjects用arraylist记录的，不咋地，无法防止重复。
                }
            }
            if (counter == subjects.size()) {
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
