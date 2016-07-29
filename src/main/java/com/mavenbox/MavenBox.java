package com.mavenbox;

import com.mavenbox.ui.UIControl;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The JavaFX UI
 */
public class MavenBox extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(final Stage stage) {
    UIControl.getInstance().init(stage);
  }
}