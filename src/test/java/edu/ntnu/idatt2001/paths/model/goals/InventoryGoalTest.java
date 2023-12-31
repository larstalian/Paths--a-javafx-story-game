package edu.ntnu.idatt2001.paths.model.goals;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import edu.ntnu.idatt2001.paths.model.game.Player;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InventoryGoalTest {

  private Player player;
  private InventoryGoal inventoryGoal;

  @BeforeEach
  void setUp() {
    player = new Player.Builder("PlayerName").build();
    inventoryGoal = new InventoryGoal(List.of("Sword", "Shield"));
  }

  @Test
  void isFulfilled() {
    player.addToInventory("Sword");
    player.addToInventory("Shield");
    assertThat(inventoryGoal.isFulfilled(player), is(true));
  }

  @Test
  void isFulfilled_ShouldReturnFalseIfNotFulfilled() {
    player.addToInventory("Sword");
    assertThat(inventoryGoal.isFulfilled(player), is(false));
  }
}
