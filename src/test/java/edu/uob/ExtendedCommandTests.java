package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Paths;

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
        assertTrue(response.contains("empty room"), "Did not see description of room in response to look");
        assertTrue(response.contains("magic potion"), "Did not see description of artifacts in response to look");
        assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
    }

    // Add more unit tests or integration tests here.

    @Test
    void testMultiplePlayers() {
        server.handleCommand("player a: look");
        server.handleCommand("player b: look");
        //todo 我想在这里测一下，我成功加入了两个玩家，但我不知道怎么检测……debug里确实是两个玩家了。
//    assertTrue();
//    assertTrue();
//    assertTrue();
    }

    @Test
    void testMultipleActions() {
        String response1 = server.handleCommand("player a: goto forest");
        assertTrue(response1.contains("You are in forest"));
        String response2 = server.handleCommand("player a: get key");
        assertTrue(response2.contains("You picked up a key"));
        String response3 = server.handleCommand("player a: goto cabin");
        assertTrue(response3.contains("You are in cabin"));
        String response4 = server.handleCommand("player a: open trapdoor with the key");
        assertTrue(response4.contains("unlock the trapdoor"));
    }

    @Test
    void testMultipleInvalidActions() {
        String response1 = server.handleCommand("player a: get potion and goto forest");
        assertTrue(response1.contains("ERROR"));
//    String response2 = server.handleCommand("player a: get key and ");
//    assertTrue(response2.contains("You picked up a key"));
//    String response3 = server.handleCommand("player a: goto cabin");
//    assertTrue(response3.contains("You are in cabin"));
//    String response4 = server.handleCommand("player a: open trapdoor with the key");
//    assertTrue(response4.contains("unlock the trapdoor"));
    }

    //todo 既然转了lowercase，那就测几个大小写混合的command。
}
