package edu.uob;

import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import edu.uob.entity.*;
import edu.uob.entity.Character;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.*;

public class GameModel {
    private final TreeMap<String, HashSet<GameAction>> actionsMap;
    private HashMap<String, Player> players;
    private Location birthPlace;
    private String currentPlayer;
    private Location storeroom;
    private String currentLocation; //todo delete
    private final HashMap<String, Location> locationsMap;

    public GameModel(Graph entities, NodeList actions) {
        actionsMap = new TreeMap<>();
        locationsMap = new HashMap<>();
        players = new HashMap<>();
        setGameEntities(entities);
        setGameAction(actions);
    }

    private void setGameEntities(Graph entities) {
        ArrayList<Graph> sections = entities.getSubgraphs();
        // The locations will always be in the first subgraph
        ArrayList<Graph> locations = sections.get(0).getSubgraphs();
        setGameLocations(locations);
        ArrayList<Edge> paths = sections.get(1).getEdges();
        setPaths(paths);
    }

    private void setGameLocations(ArrayList<Graph> locations) {
        for (Graph locationGraph : locations) {
            String id = locationGraph.getId().getId();
            String name = locationGraph.getNodes(true).get(0).getId().getId(); // the id of nodes[0] should be 'carbin'
            String description = locationGraph.getNodes(true).get(0).getAttribute("description");
            Location location = new Location(name, description);
            ArrayList<Graph> attributes = locationGraph.getSubgraphs();
            setAttributes(attributes, location);
            locationsMap.put(name, location);
            if (id.equalsIgnoreCase("cluster001")) {
                birthPlace = location;
                currentLocation = name;
            }
            if (id.equalsIgnoreCase("cluster999")) {
                storeroom = location;
            }
        }
    }

    private void setAttributes(ArrayList<Graph> attributes, Location location) {
        for (Graph attribute : attributes) {
            //这里面，就是characters、furniture和artefacts的graph，
            // 怎么分辨哪个graph是哪个呢？看id匹配string？有点蠢。
            // todo 有没有别的办法？
            String id = attribute.getId().getId();
            if (attribute.getNodes(true).size() > 0) {
                String attributeName = attribute.getNodes(true).get(0).getId().getId(); // the id of nodes[0] should be 'carbin'
                String attributeDescription = attribute.getNodes(true).get(0).getAttribute("description");
                if (id.equalsIgnoreCase("characters")) {
                    Character character = new Character(attributeName, attributeDescription);
                    location.addCharacter(character);
                } else if (id.equalsIgnoreCase("furniture")) {
                    Furniture furniture = new Furniture(attributeName, attributeDescription);
                    location.addFurniture(furniture);
                } else if (id.equalsIgnoreCase("artefacts")) {
                    Artefact artefact = new Artefact(attributeName, attributeDescription);
                    location.addArtefact(artefact);
                }
            }
        }
    }

    private void setPaths(ArrayList<Edge> paths) {
        for (Edge path : paths) {
            String source = path.getSource().getNode().getId().getId();
            Location sourceLocation = locationsMap.get(source);
            String target = path.getTarget().getNode().getId().getId();
            Location targetLocation = locationsMap.get(target);
            sourceLocation.addPaths(targetLocation);
        }
    }

    private void setGameAction(NodeList actions) {
        //todo 可能会用到的comments
//         Get the first action (only the odd items are actually actions - 1, 3, 5 etc.)
        // Get the first trigger phrase
        for (int i = 1; i < actions.getLength(); i+=2) {
            GameAction newAction = new GameAction();
            Element action = (Element) actions.item(i);
            setEntities(action, "subjects", newAction);
            setEntities(action, "consumed", newAction);
            setEntities(action, "produced", newAction);
            String narration = action.getElementsByTagName("narration").item(0).getTextContent();
            newAction.setNarration(narration);
            Element triggers = (Element) action.getElementsByTagName("triggers").item(0);
            setTriggers(triggers, newAction);
        }
    }

    private void setTriggers(Element triggers, GameAction newAction) {
        NodeList keywords = triggers.getElementsByTagName("keyword");
        for (int i = 0; i < keywords.getLength(); i++) {
            String keyword = keywords.item(i).getTextContent();
            if (actionsMap.containsKey(keyword)) {
                HashSet<GameAction> actions = actionsMap.get(keyword);
                actions.add(newAction);
            } else {
                HashSet<GameAction> actions = new HashSet<GameAction>();
                actions.add(newAction);
                actionsMap.put(keyword, actions);
            }
        }
    }

    private void setEntities(Element action, String tagName, GameAction newAction) {
        int n = action.getElementsByTagName(tagName).getLength();
        NodeList tags = action.getElementsByTagName(tagName);
        for (int i = 0; i < n; i++) {
            Element entities = (Element) tags.item(i);
            int length = entities.getElementsByTagName("entity").getLength();
            for (int j = 0; j < length; j++) {
                String name = entities.getElementsByTagName("entity").item(j).getTextContent();
                if (tagName.equalsIgnoreCase("subjects")) {
                    newAction.addSubjects(name);
                } else if (tagName.equalsIgnoreCase("consumed")) {
                    newAction.addConsumed(name);
                } else if (tagName.equalsIgnoreCase("produced")) {
                    newAction.addProduced(name);
                }
            }
        }
    }

    public HashMap<String, Location> getLocationsMap() {
        return locationsMap;
    }

    public TreeMap<String, HashSet<GameAction>> getActionsMap() {
        return actionsMap;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayer);
    }

    public Location getStoreroom() {
        return storeroom;
    }

    public Location getBirthPlace() {
        return birthPlace;
    }

    public Location getLocation(String name) {
        return locationsMap.get(name);
    }
// todo delete
//    public void setCurrentLocation(String locationName) {
//        currentLocation = locationName;
//    }

    public void setCurrentPlayer(String playerName) {
        currentPlayer = playerName;
    }

    public void addPlayer(String playerName) {
        Player newPlayer = new Player(playerName, "", birthPlace);
        players.put(playerName, newPlayer);
    }

    public HashMap<String, Player> getPlayers() {
        return players;
    }
}
