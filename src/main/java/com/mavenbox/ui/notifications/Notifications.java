package com.mavenbox.ui.notifications;

import callete.api.Callete;
import com.mavenbox.model.Notification;
import com.mavenbox.ui.ResourceLoader;
import com.mavenbox.ui.util.TransitionUtil;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.stage.Stage;

/**
 * Helper for notifications
 */
public class Notifications {

  private final static int NOTIFICATIONS_TIMEOUT = Callete.getConfiguration().getInt("notifications.timeout");

  public static void showNotification(Stage stage, Notification notification) {
    NotificationNode notificationNode = new NotificationNode(notification);
    final Scene scene = new Scene(notificationNode, 250, 100, true, SceneAntialiasing.BALANCED);
    scene.getStylesheets().add(ResourceLoader.getResource("theme.css"));
    stage.setScene(scene);
    stage.setAlwaysOnTop(true);
    scene.setFill(null);
    stage.setX(50);
    stage.setY(50);
    stage.show();

    Platform.runLater(() -> {
      try {
        Thread.sleep(NOTIFICATIONS_TIMEOUT);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      FadeTransition outFader = TransitionUtil.createOutFader(notificationNode, 500);
      outFader.setOnFinished(event -> stage.hide());
      outFader.play();
    });
  }
}
