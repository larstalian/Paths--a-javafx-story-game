package edu.ntnu.idatt2001.paths.view;

import edu.ntnu.idatt2001.paths.model.filehandlers.json.StoryFileHandler;
import edu.ntnu.idatt2001.paths.model.filehandlers.paths.StoryFileReader;
import edu.ntnu.idatt2001.paths.model.game.Game;
import edu.ntnu.idatt2001.paths.model.game.Player;
import edu.ntnu.idatt2001.paths.model.goals.HealthGoal;
import edu.ntnu.idatt2001.paths.model.goals.ScoreGoal;
import edu.ntnu.idatt2001.paths.model.story.Story;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Builder;

public class NewGame implements Builder<Region> {

  private final Button startNewGameButton;
  private final ComboBox<String> storySelect;
  private final Button goBackButton;
  private final TextField playerName;

  public NewGame() {
    startNewGameButton = new Button("Start New Game");
    storySelect = new ComboBox<>();
    goBackButton = new Button("Go Back");
    playerName = new TextField();
  }

  @Override
  public Region build() {
    BorderPane mainLayout = new BorderPane();
    mainLayout.getStyleClass().add("main-menu");
    mainLayout.setCenter(createCenter());
    configureGoBackButton();
    configureNewGameButton();
    configureStorySelect();
    configurePlayerName();
    return mainLayout;
  }

  private Node createCenter() {
    StackPane centerLayout = new StackPane();
    centerLayout.getChildren().add(createButtonVBox());
    return centerLayout;
  }

  private Node createButtonVBox() {
    VBox buttonLayout = new VBox();
    buttonLayout.getChildren().add(playerName);
    buttonLayout.getChildren().add(startNewGameButton);
    buttonLayout.getChildren().add(createHBox());
    buttonLayout.getChildren().add(goBackButton);
    buttonLayout.getStyleClass().add("button-vbox");
    return buttonLayout;
  }

  private Node createHBox() {
    HBox hboxLayout = new HBox();
    hboxLayout.getChildren().add(new Text("Select Story: "));
    hboxLayout.getChildren().add(storySelect);
    hboxLayout.getStyleClass().add("button-hbox");
    return hboxLayout;
  }

  private void configureNewGameButton() {
    startNewGameButton.setOnAction(
        event -> {
          String name = playerName.getText();
          String story = storySelect.getValue();

          if (isNameValid(name) && story != null) {
            startNewGame(name, story);
          } else {
            showWarningDialog();
          }
        });
  }

  private void configureGoBackButton() {
    goBackButton.setOnAction(
        event -> {
          Region mainMenuRoot = new MainMenu().build();
          goBackButton.getScene().setRoot(mainMenuRoot);
        });
  }

  private void configureStorySelect() {
    storySelect.getItems().addAll(StoryFileReader.getSavedStories());
    storySelect.getItems().addAll(StoryFileHandler.getSavedStories());
    storySelect.selectionModelProperty().get().selectFirst();
  }

  private void configurePlayerName() {
    playerName.getStyleClass().add("player-name-field");
    playerName.setPromptText("Enter your name");
    playerName.setFocusTraversable(false);
  }

  private boolean isNameValid(String name) {
    int minNameLength = Player.PlayerConstants.MIN_NAME_LENGTH;
    int maxNameLength = Player.PlayerConstants.MAX_NAME_LENGTH;
    return name != null && name.length() >= minNameLength && name.length() <= maxNameLength;
  }

  private void startNewGame(String name, String story) {
    Story loadedStory = loadStoryFromFile(story);
    if (loadedStory != null) {
      Player player = new Player.Builder(name).build();
      Game currentGame =
          new Game(player, loadedStory, List.of(new HealthGoal(100), new ScoreGoal(100)));
      edu.ntnu.idatt2001.paths.view.Game.setCurrentGame(currentGame);
      Region gameRoot = new edu.ntnu.idatt2001.paths.view.Game().build();
      startNewGameButton.getScene().setRoot(gameRoot);
    }
  }

  private Story loadStoryFromFile(String story) {
    Story loadedStory = null;
    try {
      if (story.endsWith(StoryFileReader.getFileEnding())) {
        story = story.replace(StoryFileReader.getFileEnding(), "");
        loadedStory = StoryFileReader.readStoryFromFile(story);

      } else if (story.endsWith(".json")) {
        story = story.replace(".json", "");
        loadedStory = new StoryFileHandler().loadStoryFromFile(story);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return loadedStory;
  }

  private void showWarningDialog() {
    Alert alert =
        new Alert(
            Alert.AlertType.WARNING, "Please enter a valid name and select a story", ButtonType.OK);
    alert.showAndWait();
  }
}
