package edu.uob;

import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Id;
import edu.uob.entity.Location;
import edu.uob.entity.Player;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

public class GameModel {
    private TreeMap<String, HashSet<GameAction>> actionsMap = new TreeMap<String, HashSet<GameAction>>();
    private ArrayList<Player> players;
    private Location birthPlace;
    private HashMap<String, Location> locationsMap = new HashMap<String, Location>();

    public GameModel(Graph entities, NodeList actions) {
        setGameEntities(entities);
        setGameAction(actions);
    }

    private void setGameEntities(Graph entities) {
        ArrayList<Graph> sections = entities.getSubgraphs();
        // The locations will always be in the first subgraph
        ArrayList<Graph> locations = sections.get(0).getSubgraphs();
        setGameLocations(locations);
    }

    private void setGameLocations(ArrayList<Graph> locations) {
        for (int i = 0; i < locations.size(); i++) {
            Graph locationGraph = locations.get(i);
            String name = locationGraph.getNodes(true).get(0).getId().getId(); // the id of nodes[0] should be 'carbin'
            String description = locationGraph.getNodes(true).get(0).getAttribute("description");
            Location location = new Location(name, description);
            // set entities and path
            ArrayList<Graph> attributes = locationGraph.getSubgraphs();
            for (int j = 0; j < attributes.size(); j++) {
                //这里面，就是characters、furniture和artefacts的graph，
                // 怎么分辨哪个graph是哪个呢？看id匹配string？有点蠢。
                // 有没有别的办法？
            }
            locationsMap.put(name, location);
        }
    }

    private void setBasicActions() {
        //todo
        setInventory();
    }

    private void setInventory() {
        GameAction inventory = new GameAction();
        //todo 肯定不是新建一个player，记得改。
        Player currentPlayer = new Player("", "");
//        inventory.subjects = (HashMap<String, GameEntity>) currentPlayer.getInventory();


    }

    private void setGameAction(NodeList actions) {
        // Get the first action (only the odd items are actually actions - 1, 3, 5 etc.)
        Element firstAction = (Element)actions.item(1);
        Element triggers = (Element)firstAction.getElementsByTagName("triggers").item(0);
        // Get the first trigger phrase
        String firstTriggerPhrase = triggers.getElementsByTagName("keyword").item(0).getTextContent();
    }
}
