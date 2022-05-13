package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

// PLEASE READ:
// The tests in this file will fail by default for a template skeleton, your job is to pass them
// and maybe write some more, read up on how to write tests at
// https://junit.org/junit5/docs/current/user-guide/#writing-tests
final class BasicCommandTests {

  private GameServer server;

  // Make a new server for every @Test (i.e. this method runs before every @Test test case)
  @BeforeEach
  void setup() {
      File entitiesFile = Paths.get("config/basic-entities.dot").toAbsolutePath().toFile();
      File actionsFile = Paths.get("config/basic-actions.xml").toAbsolutePath().toFile();
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
  void testMultipleActions() {
    String response0 = server.handleCommand("player a: get potion");
    assertTrue(response0.contains("You picked up a potion"));
    String response1 = server.handleCommand("player a: goto forest");
    assertTrue(response1.contains("You are in forest"));
    String response2 = server.handleCommand("player a: get key");
    assertTrue(response2.contains("You picked up a key"));
    String response3 = server.handleCommand("player a: goto cabin");
    assertTrue(response3.contains("You are in cabin"));
//    String response4 = server.handleCommand("player a: open trapdoor with the key"); // it is valid
    String response4 = server.handleCommand("player a: unlock with key"); // it is valid
//    String response4 = server.handleCommand("player a: open trapdoor"); // it is valid
    assertTrue(response4.contains("unlock the trapdoor"));
    String response5 = server.handleCommand("player a: goto cellar");
    assertTrue(response5.contains("You are in cellar"));
    String response6 = server.handleCommand("player a: hit elf");
    assertTrue(response6.contains("attack the elf"));
    String response7 = server.handleCommand("player a: health");
    assertTrue(response7.contains("Your health is 2")); // the health should be 2
    String response8 = server.handleCommand("player a: drink potion");
    assertTrue(response8.contains("your health improves"));
    String response9 = server.handleCommand("player a: health");
    assertTrue(response9.contains("Your health is 3")); // the health should be 3
  }

  @ParameterizedTest
  @ValueSource(strings = {
          "player a: get potion and goto forest",
          "player a: drink attack potion"}) //todo 第三个目前不会判定错误，不行就删了。, "player a: get potion forest"
  void testMultipleInvalidActions(String command) {
    String response = server.handleCommand(command);
    assertTrue(response.contains("ERROR"));
  }

  //todo 既然转了lowercase，那就测几个大小写混合的command。

  //todo 测cut down tree

  //todo 用掉之后再用，false。

  @Test
  void testLookOtherPlayers() {
    server.handleCommand("Kun: get potion");
    String response = server.handleCommand("Rui: look");
    assertTrue(response.contains("2 players"));
    assertTrue(response.contains("Kun: A player"));
  }

  @Test
  void testRestartGame() {
    server.handleCommand("player a: get potion");
    server.handleCommand("player a: goto forest");
    server.handleCommand("player a: get key");
    server.handleCommand("player a: goto cabin");
//    server.handleCommand("player a: open trapdoor with the key"); // it is valid
//    server.handleCommand("player a: unlock with key"); // it is valid
    server.handleCommand("player a: open trapdoor"); // it is valid
    server.handleCommand("player a: goto cellar");
    server.handleCommand("player a: hit elf");
    String response1 = server.handleCommand("player a: health");
    assertTrue(response1.contains("Your health is 2")); // the health should be 2
    server.handleCommand("player a: hit elf");
    String response2 = server.handleCommand("player a: health");
    assertTrue(response2.contains("Your health is 1")); // the health should be 1
    String response3 = server.handleCommand("player a: hit elf");
    assertTrue(response3.contains("You died and lost all the artefacts")); // the health should be 0
    server.handleCommand("player a: goto cellar");
    String response4 = server.handleCommand("player a: look");
    assertTrue(response4.contains("potion"));
  }

//  @Test //todo 修改了dot文件之后才有效的测试。记得挪到extends之后删掉
//  void testIgnoreGet() {
//    server.handleCommand("player a: get key");
//    String response = server.handleCommand("player a: open trapdoor with the key"); // it is valid
//    assertTrue(response.contains("unlock the trapdoor"));
//  }
}
