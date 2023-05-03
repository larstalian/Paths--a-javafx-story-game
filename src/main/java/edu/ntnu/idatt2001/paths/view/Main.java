package edu.ntnu.idatt2001.paths.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class Main extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    Region sceneRoot = new MainMenu().build();
    Scene scene = new Scene(sceneRoot);
    primaryStage.setScene(scene);
    scene.getStylesheets().add("/css/default.css");
    primaryStage.show();
  }
}