package edu.ntnu.idatt2001.paths.controller;

import edu.ntnu.idatt2001.paths.model.media.SoundHandler;
import edu.ntnu.idatt2001.paths.view.MainMenuView;
import edu.ntnu.idatt2001.paths.view.util.Widgets;
import javafx.application.Platform;
import javafx.scene.layout.Region;

public class MainMenuViewController {
  private final MainMenuView mainMenuView;

  public MainMenuViewController() {
    mainMenuView = new MainMenuView();
    configureNewGameButton();
    configureLoadGameButton();
    configureStoriesButton();
    configureCreateStoryButton();
    configureExitButton();
    SoundHandler.getInstance().playMenuMusic();
  }

  public Region getRoot() {
    return mainMenuView.getRoot();
  }

  private void configureNewGameButton() {
    mainMenuView
        .getNewGameButton()
        .setOnAction(
            event -> {
              NewGameViewController newGameViewController = new NewGameViewController();
              Region newGameRoot = newGameViewController.getRoot();
              mainMenuView.getNewGameButton().getScene().setRoot(newGameRoot);
            });
  }

  private void configureLoadGameButton() {
    mainMenuView
        .getLoadGameButton()
        .setOnAction(
            event -> {
              Region newGameRoot = new LoadGameViewController().getRoot();
              mainMenuView.getLoadGameButton().getScene().setRoot(newGameRoot);
            });
  }

  private void configureStoriesButton() {
    mainMenuView
        .getStoriesButton()
        .setOnAction(
            event -> {
              try {
                Region newGameRoot = new StoriesViewController().getRoot();
                mainMenuView.getStoriesButton().getScene().setRoot(newGameRoot);
              } catch (NullPointerException e) {
                e.printStackTrace();
                Widgets.createAlert(
                        "Error",
                        "There are no stories",
                        "Are you sure the stories are saved in the correct folder?")
                    .showAndWait();
              }
            });
  }

  private void configureCreateStoryButton() {
    mainMenuView
        .getCreateStoryButton()
        .setOnAction(
            event ->
                mainMenuView
                    .getCreateStoryButton()
                    .getScene()
                    .setRoot(new NewStoryViewController().getRoot()));
  }

  private void configureExitButton() {
    mainMenuView.getExitButton().setOnAction(event -> Platform.exit());
  }
}