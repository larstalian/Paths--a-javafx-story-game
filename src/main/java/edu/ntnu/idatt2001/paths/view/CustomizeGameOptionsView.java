package edu.ntnu.idatt2001.paths.view;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class CustomizeGameOptionsView {

  @lombok.Getter private final TextField healthGoal;
  @lombok.Getter private final TextField scoreGoal;
  @lombok.Getter private final TextField goldGoal;
  @lombok.Getter private final TextField inventoryGoal;
  @lombok.Getter private final TextField startingHealth;
  @lombok.Getter private final TextField startingScore;
  @lombok.Getter private final TextField startingGold;
  @lombok.Getter private final TextField startingInventory;

  public CustomizeGameOptionsView() {
    healthGoal = new TextField();
    scoreGoal = new TextField();
    goldGoal = new TextField();
    inventoryGoal = new TextField();
    startingHealth = new TextField();
    startingScore = new TextField();
    startingGold = new TextField();
    startingInventory = new TextField();
  }

  public Dialog<Void> createDialog() {
    Dialog<Void> popup = new Dialog<>();
    popup.setTitle("Customize Game Options");

    HBox hbox = new HBox();
    hbox.getChildren().add(createCustomPlayerSettings());
    hbox.getChildren().add(createCustomGameSettings());

    popup.getDialogPane().setContent(hbox);

    return popup;
  }

  private Node createCustomGameSettings() {
    GridPane results = new GridPane();
    results.add(new Label("Game Settings"), 0, 0);
    results.add(new Label("Health goal:"), 0, 1);
    results.add(new Label("Score goal:"), 0, 2);
    results.add(new Label("Gold goal"), 0, 3);
    results.add(new Label("Inventory goal:"), 0, 4);

    results.add(healthGoal, 1, 1);
    results.add(scoreGoal, 1, 2);
    results.add(goldGoal, 1, 3);
    results.add(inventoryGoal, 1, 4);

    return results;
  }

  private Node createCustomPlayerSettings() {
    GridPane results = new GridPane();
    results.add(new Label("Player Settings"), 0, 0);
    results.add(new Label("Starting health:"), 0, 1);
    results.add(new Label("Starting score:"), 0, 2);
    results.add(new Label("Starting gold"), 0, 3);
    results.add(new Label("Starting inventory items:"), 0, 4);

    results.add(startingHealth, 1, 1);
    results.add(startingScore, 1, 2);
    results.add(startingGold, 1, 3);
    results.add(startingInventory, 1, 4);

    return results;
  }
}
