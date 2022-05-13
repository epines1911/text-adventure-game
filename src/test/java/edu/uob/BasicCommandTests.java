package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
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

  @ParameterizedTest
  @ValueSource(strings = {
          "",
          " alice",
          "adl dlf*af",
          "1lksjf a",
          "aflj*% la"})
  void testInvalidPlayerName(String name) {
    String response = server.handleCommand(name + ": inventory");
    assertTrue(response.contains("ERROR"));
  }

  @Test
  void testInvAction() {
    String response = server.handleCommand("player a: inventory");
    assertTrue(response.contains("0"));
    server.handleCommand("player a: get potion");
    response = server.handleCommand("player a: inv");
    assertTrue(response.contains("potion"));
    response = server.handleCommand("player a: inv inventory");
    assertTrue(response.contains("ERROR"));
  }

  @Test
  void testGotoAction() {
    String response = server.handleCommand("player a: goto forest");
    assertTrue(response.contains("You are in forest"));
    response = server.handleCommand("player a: goto cabin goto");
    assertTrue(response.contains("You are in cabin"));
    response = server.handleCommand("player a: goto cellar");
    assertTrue(response.contains("ERROR"));
    response = server.handleCommand("player a: goto forest abc");
    assertTrue(response.contains("ERROR"));
  }

  @Test
  void testMultipleGetAndDrop() {
    // look, get potion, then look
    String response0 = server.handleCommand("player a: look");
    assertTrue(response0.contains("Magic potion"));
    response0 = server.handleCommand("player a: get potion");
    assertTrue(response0.contains("You picked up a potion"));
    response0 = server.handleCommand("player a: look");
    assertFalse(response0.contains("Magic potion"));
    // check inventory
    response0 = server.handleCommand("player a: inv");
    assertTrue(response0.contains("Magic potion"));
    // drop potion, then look and check inventory
    response0 = server.handleCommand("player a: drop potion");
    assertTrue(response0.contains("dropped a potion"));
    response0 = server.handleCommand("player a: look");
    assertTrue(response0.contains("Magic potion"));
    response0 = server.handleCommand("player a: inv");
    assertFalse(response0.contains("Magic potion"));
  }

  @Test
  void testMultipleActions() {
    // look, get potion
    String response0 = server.handleCommand("player a: look");
    assertTrue(response0.contains("Magic potion"));
    response0 = server.handleCommand("player a: get potion");
    assertTrue(response0.contains("You picked up a potion"));
    // goto forest, get key, then goto cabin
    String response1 = server.handleCommand("player a: goto forest");
    assertTrue(response1.contains("You are in forest"));
    String response2 = server.handleCommand("player a: get key");
    assertTrue(response2.contains("You picked up a key"));
    String response3 = server.handleCommand("player a: goto cabin");
    assertTrue(response3.contains("You are in cabin"));
    // unlock trapdoor, which will add cellar in paths, then goto cellar
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
          "player a: get potion forest",
          "player a: goto cut forest",
          "player a: open with key",
          "player a: open",
          "player a: get",
          "player a: get drop potion",
          "player a: look goto forest",
          "player a: look open trapdoor",
          "player a:",
          "player a: get potion and goto forest",
          "player a: get drink attack potion"})
  void testSingleInvalidActions(String command) {
    String response = server.handleCommand(command);
    assertTrue(response.contains("ERROR"));
  }

  @ParameterizedTest
  @ValueSource(strings = {
          "player a: drink potion qwe"})
  void testSingleValidActions(String command) {
    String response = server.handleCommand(command);
    assertFalse(response.contains("ERROR"));
  }

  //test commands with different case words
  @Test
  void testCaseSensitiveCommands() {
    String response = server.handleCommand("player a: gEt potIon");
    assertFalse(response.contains("ERROR"));
    response = server.handleCommand("player a: loOK");
    assertFalse(response.contains("ERROR"));
    response = server.handleCommand("player a: iNv");
    assertFalse(response.contains("ERROR"));
    response = server.handleCommand("player a: DRop PoTion");
    assertFalse(response.contains("ERROR"));
    response = server.handleCommand("player a: drINk potiON");
    assertFalse(response.contains("ERROR"));
  }

  // test drop the same artefact twice
  @Test
  void testDoubleDropAction() {
    server.handleCommand("player a: gEt potIon");
    String response = server.handleCommand("player a: DRop PoTion");
    assertTrue(response.contains("dropped a potion"));
    response = server.handleCommand("player a: drOP pOtiOn");
    assertTrue(response.contains("ERROR"));
  }

  // test drop the same artefact twice
  @Test
  void testDoubleGetAction() {
    String response = server.handleCommand("player a: gEt potIon");
    assertTrue(response.contains("picked up a potion"));
    response = server.handleCommand("player a: geT pOtIoN");
    assertTrue(response.contains("ERROR"));
  }

  @Test
  void testLookOtherPlayers() {
    server.handleCommand("Kun: get potion");
    String response = server.handleCommand("Rui: look");
    assertTrue(response.contains("2 players"));
    assertTrue(response.contains("Kun: A player"));
    server.handleCommand("Kun: goto forest");
    response = server.handleCommand("Rui: look");
    assertFalse(response.contains("Kun"));
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
    server.handleCommand("player a: attack elf");
    String response2 = server.handleCommand("player a: health");
    assertTrue(response2.contains("Your health is 1")); // the health should be 1
    String response3 = server.handleCommand("player a: fight elf");
    assertTrue(response3.contains("You died and lost all the artefacts")); // the health should be 0
    server.handleCommand("player a: goto cellar");
    String response4 = server.handleCommand("player a: look");
    assertTrue(response4.contains("potion"));
    response4 = server.handleCommand("player a: inventory");
    assertTrue(response4.contains("0 artefacts"));
  }
}
