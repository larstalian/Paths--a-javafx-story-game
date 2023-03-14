package edu.ntnu.idatt2001.paths.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static edu.ntnu.idatt2001.paths.game.Player.PlayerConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlayerTest {

  private Player player;
  private List<String> inventory;

  @BeforeEach
  void setUp() {
    player = new Player.Builder("PlayerName")
            .health(100)
            .score(0)
            .gold(0)
            .inventory("Armor", "Sword")
            .build();
    inventory = new ArrayList<>();
    inventory.add("Armor");
    inventory.add("Sword");
  }


  @Test
  void testGetName() {
    assertThat("PlayerName", is(player.getName()));
  }

  @Test
  void testGetHealth() {
    assertThat(100, is(player.getHealth()));
  }

  @Test
  void testGetScore() {
    assertThat(0, is(player.getScore()));
  }

  @Test
  void testGetGold() {
    assertThat(0, is(player.getGold()));
  }

  @Test
  void testGetInventory() {
    assertThat(inventory, is(player.getInventory()));
  }

  @Test
  void testAddHealth() {
    player.addHealth(50);
    assertThat(150, is(player.getHealth()));
  }

  @Test
  void testAddScore() {
    player.addScore(50);
    assertThat(50, is(player.getScore()));
  }

  @Test
  void testAddGold() {
    player.addGold(50);
    assertThat(50, is(player.getGold()));
  }

  @Test
  void testAddToInventory() {
    inventory.add("Shield");
    player.addToInventory("Shield");
    assertThat(player.getInventory(), is(inventory));
  }

  @Test
  void testToString() {
    String expectedOutput = "PlayerName      100 HP     0 PTS     0 GOLD  INV: Armor, Sword";
    String actualOutput = player.toString();
    assertThat(expectedOutput, is(actualOutput));
  }

  @Test
  void testAddHealth_ShouldThrowExceptionWhenHealthIsUnder0() {
    assertThrows(IllegalArgumentException.class, () -> player.addHealth(-101));
  }

  @Test
  void testAddHealth_ShouldThrowExceptionWhenHealthIsOverDefined() {
    assertThrows(IllegalArgumentException.class, () -> player.addHealth(MAX_HEALTH + 1));
  }

  @Test
  void addGold_ShouldThrowExceptionWhenGoldIsUnder0() {
    assertThrows(IllegalArgumentException.class, () -> player.addGold(-1));
  }

  @Test
  void addGold_ShouldThrowExceptionWhenGoldIsOverDefined() {
    assertThrows(IllegalArgumentException.class, () -> player.addGold(MAX_GOLD + 1));
  }

  @Test
  void addScore_ShouldThrowExceptionWhenScoreIsUnder0() {
    assertThrows(IllegalArgumentException.class, () -> player.addScore(-1));
  }

  @Test
  void addScore_ShouldThrowExceptionWhenScoreIsOverDefined() {
    assertThrows(IllegalArgumentException.class, () -> player.addScore(MAX_SCORE + 1));
  }

  @Test
  void testAddToInventory_ShouldThrowExceptionWhenInventoryIsFull() {
    IntStream.range(0, MAX_INVENTORY_SIZE - player.getInventory().size())
        .forEach(i -> player.addToInventory("Item"));
    assertThrows(IllegalArgumentException.class, () -> player.addToInventory("Item"));
  }

  @Test
  void testAddToInventory_ItemLengthShouldNotBeLongerThanDefined() {
    String tooLongString =
        new String(new char[MAX_ITEM_LENGTH + 1]).replace("\0", "a");
    assertThrows(IllegalArgumentException.class, () -> player.addToInventory(tooLongString));
  }

  @Test
  void testAddToInventory_ItemLengthShouldNotBeShorterThanDefined() {
    String tooShortString =
        new String(new char[MIN_ITEM_LENGTH - 1]).replace("\0", "a");
    assertThrows(IllegalArgumentException.class, () -> player.addToInventory(tooShortString));
  }

  @Test
  void testNameLengthShouldNotBeLongerThanDefined() {
    String tooLongString =
        new String(new char[MAX_NAME_LENGTH + 1]).replace("\0", "a");
    assertThrows(IllegalArgumentException.class, ()
        -> new Player.Builder(tooLongString).build());
  }

  @Test
  void testNameLengthShouldNotBeShorterThanDefined() {
    String tooShortString =
        new String(new char[MIN_NAME_LENGTH - 1]).replace("\0", "a");
    assertThrows(IllegalArgumentException.class, ()
        -> new Player.Builder(tooShortString).build());
  }

  @Test
  void testBuilder_InventoryIsUpdated() {
    Player.Builder builder = new Player.Builder("TestPlayer")
            .health(100)
            .score(50)
            .gold(25)
            .inventory("Item1", "Item2", "Item3");

    Player builtPlayer = builder.build();
    List<String> expectedInventory = Arrays.asList("Item1", "Item2", "Item3");

    assertThat(builtPlayer.getInventory(), is(expectedInventory));
  }
}