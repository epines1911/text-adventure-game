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
        String trigger = tokens[0].toLowerCase();
        if (model.getActionsMap().containsKey(trigger)) {
            HashSet<GameAction> actions = model.getActionsMap().get(trigger);
            // find which action should be executed
            GameAction aimAction = null;
            for (GameAction action: actions) {
                ArrayList<String> subjects = action.getSubjects();
                int counter = 0;
                for (int i = 0; i < subjects.size(); i++) {
                    if (command.contains(subjects.get(i))) {
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
        } else {
            throw new GameException("Cannot find " + trigger + ", please check this word.");
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
