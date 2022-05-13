package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ExtendedCommandTests {
    private GameServer server;

    // Make a new server for every @Test (i.e. this method runs before every @Test test case)
    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config/extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config/extended-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    // Test to spawn a new server and send a simple "look" command
    @Test
    void testLookingAroundStartLocation() {
        String response = server.handleCommand("player a: look").toLowerCase();
        assertTrue(response.contains("log cabin"), "Did not see description of artifacts in response to look");
        assertTrue(response.contains("3 artefacts"), "Did not see description of room in response to look");
        assertTrue(response.contains("silver coin"), "Did not see description of artifacts in response to look");
        assertTrue(response.contains("magic potion"), "Did not see description of artifacts in response to look");
        assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
    }

    // Add more unit tests or integration tests here.

    @Test
    void testLookOtherPlayers() {
        server.handleCommand("Kun: get potion");
        String response = server.handleCommand("Rui: look");
        assertTrue(response.contains("2 players"));
        assertTrue(response.contains("Kun: A player"));
    }

    @Test
    void testMultipleActions() {
        server.handleCommand("player a: get potion");
        server.handleCommand("player a: get axe");
        server.handleCommand("player a: get coin");
        String response = server.handleCommand("player a: inv");
        assertTrue(response.contains("potion"));
        assertTrue(response.contains("axe"));
        assertTrue(response.contains("coin"));
        // go to forest
        server.handleCommand("player a: goto forest");
        response = server.handleCommand("player a: chop tree with axe"); // it's valid
//        response = server.handleCommand("player a: cut tree"); // it's valid
//        response = server.handleCommand("player a: cutdown with axe"); // it's valid
//        response = server.handleCommand("player a: cut down with axe"); // it's valid
        assertTrue(response.contains("cut down the tree"));
        response = server.handleCommand("player a: look");
        assertFalse(response.contains("tree"));
        server.handleCommand("player a: get key");
        server.handleCommand("player a: get log");
        response = server.handleCommand("player a: inv");
        assertTrue(response.contains("log"));
        // go to cellar
        server.handleCommand("player a: goto cabin");
        server.handleCommand("player a: open trapdoor"); // it is valid
        server.handleCommand("player a: goto cellar");
        server.handleCommand("player a: hit elf");
        server.handleCommand("player a: hit elf");
        server.handleCommand("player a: drink potion"); // health should be 2
        response = server.handleCommand("player a: health");
        assertTrue(response.contains("health is 2"));
        server.handleCommand("player a: pay elf coin");
        response = server.handleCommand("player a: look");
        assertTrue(response.contains("shovel"));
        response = server.handleCommand("player a: inv");
        assertFalse(response.contains("potion"));
        assertFalse(response.contains("key"));
        // go to riverbank
        server.handleCommand("player a: goto cabin");
        server.handleCommand("player a: goto forest");
        server.handleCommand("player a: goto riverbank");
        server.handleCommand("player a: blow horn");
//        server.handleCommand("player a: bridge the river with the log"); // it's valid
        server.handleCommand("player a: bridge the river"); // it's valid
//        server.handleCommand("player a: bridge with the log"); // it's valid
        response = server.handleCommand("player a: look");
        assertTrue(response.contains("lumberjack"));
        assertTrue(response.contains("clearing"));
        // goto clearing
        server.handleCommand("player a: goto clearing");
//        server.handleCommand("player a: dig ground with shovel"); // it's valid
//        server.handleCommand("player a: dig ground"); // it's valid
        response = server.handleCommand("player a: dig with shovel"); // it's valid
        assertTrue(response.contains("dig into the soft ground and unearth a pot of gold"));
        response = server.handleCommand("player a: look");
        assertTrue(response.contains("gold"));
        assertTrue(response.contains("hole"));
        assertFalse(response.contains("looks like the soil has been recently disturbed"));
    }

//    @Test
//    void testMultipleInvalidActions() {
//        String response1 = server.handleCommand("player a: get potion and goto forest");
//        assertTrue(response1.contains("ERROR"));
////    String response2 = server.handleCommand("player a: get key and ");
////    assertTrue(response2.contains("You picked up a key"));
////    String response3 = server.handleCommand("player a: goto cabin");
////    assertTrue(response3.contains("You are in cabin"));
////    String response4 = server.handleCommand("player a: open trapdoor with the key");
////    assertTrue(response4.contains("unlock the trapdoor"));
//    }

    //todo 既然转了lowercase，那就测几个大小写混合的command。
}
