package edu.ntnu.idatt2001.paths.view;

import edu.ntnu.idatt2001.paths.model.media.BackgroundHandler;
import edu.ntnu.idatt2001.paths.model.media.SoundHandler;
import edu.ntnu.idatt2001.paths.model.story.Link;
import edu.ntnu.idatt2001.paths.model.story.Passage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Builder;
import javafx.util.Duration;

/**
 * The {@code Game} class represents the game view and its UI components. It manages the game flow,
 * user input, and updating the UI based on the game state.
 *
 * <p>This class utilizes JavaFX components to build the user interface and the game logic. It
 * handles user input via arrow keys, enter, and space-bar for navigation and interaction. It also
 * animates the text content for each passage and allows users to skip the animation.
 *
 * <p>The {@code Game} class contains methods to create UI elements and manage the game flow. For
 * example, it creates the content bar, link choices, and handles scene changes.
 *
 * <p>Usage example:
 *
 * <pre>{@code
 * Game game = new Game();
 * game.setCurrentGame(chosenGame);
 * Region gameUI = game.build();
 * }</pre>
 */
public class Game implements Builder<Region> {

  private static Passage currentPassage;
  private static edu.ntnu.idatt2001.paths.model.game.Game currentGame;
  private final StringProperty contentBar;
  private final AtomicBoolean isAnimationSkipped;
  private final VBox links;
  private final SoundHandler soundHandler;
  private final BackgroundHandler backgroundHandler;
  private Label skipLabel;
  private BorderPane root;

  /**
   * Constructs a new {@code Game} instance, initializing the required properties and handlers.
   *
   * <p>This constructor initializes the {@link BackgroundHandler}, {@link SoundHandler}, {@code
   * VBox} for links, {@code StringProperty} for the content bar, and an {@code AtomicBoolean} for
   * tracking the animation skipping state.
   */
  public Game() {
    backgroundHandler = BackgroundHandler.getInstance();
    soundHandler = SoundHandler.getInstance();
    links = new VBox();
    contentBar = new SimpleStringProperty();
    isAnimationSkipped = new AtomicBoolean(false);
  }

  /**
   * Sets the current game.
   *
   * @param chosenGame the chosen game to be set as the current game.
   */
  public static void setCurrentGame(edu.ntnu.idatt2001.paths.model.game.Game chosenGame) {
    currentGame = chosenGame;
  }

  /**
   * Returns the root node.
   *
   * @return the root node of the game.
   */
  public BorderPane getRoot() {
    return root;
  }

  /**
   * Builds the game UI and sets up the game.
   *
   * @return a Region containing the game UI.
   */
  @Override
  public Region build() {
    currentPassage = currentGame.begin();
    soundHandler.updateMusic(currentPassage, currentGame.getStory().getTitle());
    root = createRoot();
    addSceneListener(root);
    return root;
  }

  /**
   * Creates the root node.
   *
   * @return the BorderPane representing the root node.
   */
  private BorderPane createRoot() {
    BorderPane root = new BorderPane();
    root.getStyleClass().add("main-menu");
    root.setCenter(createCenter());
    root.setBottom(createBottom());
    root.setRight(createRight());
    return root;
  }

  /**
   * Creates the right side UI element.
   *
   * @return a Node representing the right side of the game UI.
   */
  private Node createRight() {
    StackPane results = new StackPane();
    results.getChildren().add(createLinkChoices());
    return results;
  }

  /**
   * Creates the bottom UI element.
   *
   * @return a Node representing the bottom side of the game UI.
   */
  private Node createBottom() {
    StackPane bottom = new StackPane();
    ScrollPane contentBarText = createContentBar();
    contentBarText.getStyleClass().add("content-bar");
    bottom.getChildren().add(contentBarText);
    StackPane.setAlignment(contentBarText, Pos.BOTTOM_CENTER);

    skipLabel = new Label("(click to skip)");
    skipLabel.getStyleClass().add("skip-label");
    skipLabel.setVisible(false);

    bottom.getChildren().add(skipLabel);
    StackPane.setAlignment(skipLabel, Pos.BOTTOM_RIGHT);
    StackPane.setMargin(skipLabel, new Insets(0, 10, 10, 0));

    return bottom;
  }

  /**
   * Creates the center UI element.
   *
   * @return a Node representing the center of the game UI.
   */
  private Node createCenter() {
    return new AnchorPane();
  }

  /**
   * Creates the content bar.
   *
   * @return a ScrollPane containing the content bar.
   */
  private ScrollPane createContentBar() {
    Text contentBarText = new Text();
    contentBarText.textProperty().bind(contentBar);
    TextFlow textFlow = new TextFlow(contentBarText);
    ScrollPane scrollPane = new ScrollPane(textFlow);

    textFlow
        .prefWidthProperty()
        .bind(Bindings.selectDouble(scrollPane.viewportBoundsProperty(), "width"));

    scrollPane.getStyleClass().add("scroll-pane");
    textFlow.getStyleClass().add("super-text-flow");
    scrollPane.setOnMouseClicked(event -> isAnimationSkipped.set(true));
    return scrollPane;
  }

  /** Creates and animates the content string of the current passage. */
  private void createContentString() {
    contentBar.set("");
    char[] charArray = currentPassage.getContent().toCharArray();
    AtomicInteger index = new AtomicInteger(0);

    skipLabel.setVisible(true);
    links.setVisible(false);

    Timeline timeline = new Timeline();
    KeyFrame keyFrame =
        new KeyFrame(
            Duration.millis(30),
            event -> {
              if (index.get() < charArray.length) {
                contentBar.set(contentBar.getValue() + charArray[index.get()]);
                index.incrementAndGet();
              }
              if (isAnimationSkipped.get()) {
                contentBar.set(currentPassage.getContent());
                links.setVisible(true);
                timeline.stop();
              }
            });

    timeline.getKeyFrames().add(keyFrame);
    timeline.setCycleCount(charArray.length);

    timeline
        .statusProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue == Animation.Status.STOPPED) {
                skipLabel.setVisible(false);
                links.setVisible(true);
                isAnimationSkipped.set(false);
              }
            });

    timeline.play();
  }

  /**
   * Creates the link choices UI element.
   *
   * @return a Node containing the link choices.
   */
  private Node createLinkChoices() {
    links.setAlignment(Pos.BOTTOM_CENTER);
    links.setPrefHeight(VBox.USE_COMPUTED_SIZE);
    VBox.setMargin(links, new Insets(10, 10, 10, 10));
    links.getStyleClass().add("link-view");
    updateLinkChoices();
    final AnchorPane anchorPane = new AnchorPane();
    AnchorPane.setBottomAnchor(links, 0.0);
    AnchorPane.setLeftAnchor(links, 0.0);
    AnchorPane.setRightAnchor(links, 0.0);
    anchorPane.getChildren().add(links);

    return anchorPane;
  }

  /** Updates the link choices UI element. */
  private void updateLinkChoices() {
    links.getChildren().clear();
    for (Link link : currentPassage.getLinks()) {
      Button button = new Button(link.getText());
      button.setFocusTraversable(true);
      button.getStyleClass().add("link-button");
      button.setOnAction(
          e -> {
            currentPassage = currentGame.go(link);
            createContentString();
            updateLinkChoices();
            backgroundHandler.updateBackground(
                root, currentPassage, currentGame.getStory().getTitle());
            soundHandler.updateMusic(currentPassage, currentGame.getStory().getTitle());
          });
      links.getChildren().add(button);
    }
  }

  /**
   * Adds a listener to the root node's scene property.
   *
   * @param root the BorderPane to add the listener to.
   */
  private void addSceneListener(BorderPane root) {
    root.sceneProperty()
        .addListener(
            new ChangeListener<>() {
              @Override
              public void changed(
                  ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
                if (newValue != null) {
                  createContentString();
                  updateLinkChoices();
                  root.sceneProperty().removeListener(this);
                  setupArrowKeysNavigation(newValue);
                }
              }
            });
  }

  /**
   * Sets up arrow key navigation in the scene.
   *
   * @param scene the scene to set up arrow key navigation.
   */
  private void setupArrowKeysNavigation(Scene scene) {
    AtomicInteger selectedIndex = new AtomicInteger(0);
    scene.addEventFilter(
        KeyEvent.KEY_PRESSED,
        keyEvent -> {
          int previousIndex = selectedIndex.get();
          switch (keyEvent.getCode()) {
            case UP -> selectedIndex.updateAndGet(
                i -> (i - 1 + links.getChildren().size()) % links.getChildren().size());
            case DOWN -> selectedIndex.updateAndGet(i -> (i + 1) % links.getChildren().size());
            case ENTER -> {
              Button selectedButton = (Button) links.getChildren().get(selectedIndex.get());
              selectedButton.fire();
            }
            case SPACE -> isAnimationSkipped.set(true);
            default -> {}
          }
          if (previousIndex != selectedIndex.get()) {
            links.getChildren().get(previousIndex).getStyleClass().remove("link-button-selected");
            links
                .getChildren()
                .get(selectedIndex.get())
                .getStyleClass()
                .add("link-button-selected");
          }
          keyEvent.consume();
        });
  }
}
