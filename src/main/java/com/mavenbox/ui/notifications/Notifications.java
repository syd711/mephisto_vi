package com.mavenbox.ui.notifications;

import callete.api.Callete;
import com.mavenbox.ui.ResourceLoader;
import com.mavenbox.ui.util.TransitionUtil;
import javafx.animation.FadeTransition;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Helper for notifications
 */
public class Notifications {
  private final static boolean ALWAYS_ON_TOP = Callete.getConfiguration().getBoolean("ui.alwaysOntop", true);
  private final static int NOTIFICATIONS_TIMEOUT = Callete.getConfiguration().getInt("notifications.timeout");

  public static void showNotification(Stage stage, Notification notification) {
    NotificationNode notificationNode = new NotificationNode(notification);
    final Scene scene = new Scene(notificationNode, notification.getWidth(), notification.getHeight(), true, SceneAntialiasing.BALANCED);
    scene.getStylesheets().add(ResourceLoader.getResource("theme.css"));
    stage.setScene(scene);
    stage.setAlwaysOnTop(ALWAYS_ON_TOP);
    scene.setFill(null);
    stage.setX(50);
    stage.setY(50);
    stage.show();

    FadeTransition outFader = TransitionUtil.createOutFader(notificationNode, 500);
    outFader.setDelay(Duration.millis(NOTIFICATIONS_TIMEOUT));
    outFader.setOnFinished(event -> stage.close());
    outFader.play();
  }
}
