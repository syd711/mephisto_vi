package com.mavenbox.ui.projects;

import callete.api.Callete;
import com.mavenbox.ui.ResourceLoader;
import com.mavenbox.ui.UIControl;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.input.KeyEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Static helper for projects
 */
public class Projects {
  private final static boolean ALWAYS_ON_TOP = Callete.getConfiguration().getBoolean("ui.alwaysOntop", true);

  private static Stage activeStage;

  public static void showProjects(Stage stage, boolean visible) {
    if(visible) {
      Projects.activeStage = stage;
      Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

      Navigation navigation = new Navigation();
      final Scene scene = new Scene(navigation, primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight(), true, SceneAntialiasing.BALANCED);
      scene.getStylesheets().add(ResourceLoader.getResource("theme.css"));
      stage.setScene(scene);
      scene.setFill(null);
      stage.setAlwaysOnTop(ALWAYS_ON_TOP);
      stage.centerOnScreen();
      stage.addEventFilter(KeyEvent.KEY_PRESSED, new ControlKeyEventFilter());
      stage.setX(0);
      stage.setY(0);
      UIControl.getInstance().setRotaryEncoderControl(navigation);
      stage.show();
    }
    else {
      if(activeStage != null) {
        //TODO mpf
        UIControl.getInstance().setRotaryEncoderControl(null);
        activeStage.hide();
      }
    }
  }
}
