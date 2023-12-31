package edu.ntnu.idatt2001.paths.model.game;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.ntnu.idatt2001.paths.model.goals.Goal;
import edu.ntnu.idatt2001.paths.model.goals.HealthGoal;
import edu.ntnu.idatt2001.paths.model.goals.InventoryGoal;
import edu.ntnu.idatt2001.paths.model.goals.ScoreGoal;
import edu.ntnu.idatt2001.paths.model.story.Link;
import edu.ntnu.idatt2001.paths.model.story.NoSuchPassageException;
import edu.ntnu.idatt2001.paths.model.story.Passage;
import edu.ntnu.idatt2001.paths.model.story.Story;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameTest {

  private List<Goal> goals;
  private Game game;
  private Story story;
  private Passage openingPassage;
  private Player player;

  @BeforeEach
  void setUp() {
    player = new Player.Builder("PlayerName").build();
    openingPassage = new Passage("The opening passage", "A test troll");
    story = new Story("Test Story", openingPassage);

    Goal scoreGoal = new ScoreGoal(100);
    Goal healthGoal = new HealthGoal(50);
    Goal inventoryGoal = new InventoryGoal(List.of("Armor", "Sword"));

    goals = List.of(scoreGoal, healthGoal, inventoryGoal);
    game = new Game(player, story, goals);
  }

  @Test
  void testGetPlayer() {
    assertThat(game.getPlayer(), is(player));
  }

  @Test
  void testGetStory() {
    assertThat(game.getStory(), is(story));
  }

  @Test
  void testGetGoals() {
    assertThat(game.getGoals(), containsInAnyOrder(goals.toArray(new Goal[0])));
  }

  @Test
  void testBegin() {
    assertThat(game.begin(), is(openingPassage));
  }

  @Test
  void testGo_GoToNewPassage() {
    Passage nextPassage = new Passage("Killed by Troll", "Game finished");
    story.addPassage(nextPassage);

    assertThat(game.go(new Link(nextPassage.getTitle(), nextPassage.getTitle())), is(nextPassage));
  }

  @Test
  void testConstructor_NullPlayer_ThrowsNullPointerException() {
    assertThrows(NullPointerException.class, () -> new Game(null, story, goals));
  }

  @Test
  void testConstructor_NullStory_ThrowsNullPointerException() {
    assertThrows(NullPointerException.class, () -> new Game(player, null, goals));
  }

  @Test
  void testConstructor_NullGoals_ThrowsNullPointerException() {
    assertThrows(NullPointerException.class, () -> new Game(player, story, null));
  }

  @Test
  void testGo_LinkToNonexistentPassage_ThrowsNullPointerException() {
    assertThrows(
        NoSuchPassageException.class,
        () -> game.go(new Link("Nonexistent Passage", "Nonexistent Passage")));
  }
}
