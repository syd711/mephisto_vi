package com.mavenbox;

import com.mavenbox.ui.UIControl;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The JavaFX UI
 */
public class MavenBoxService extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(final Stage stage) {
    String bit = System.getProperty("sun.arch.data.model");
    if(!bit.equals("64")) {
      System.out.println("Not a 64 bit JDK");
      System.exit(-1);
    }
    UIControl.getInstance().init(this, stage);
  }
}