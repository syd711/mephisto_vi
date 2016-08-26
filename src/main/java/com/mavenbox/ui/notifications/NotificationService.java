package com.mavenbox.ui.notifications;

import callete.api.Callete;
import com.mavenbox.ui.ResourceLoader;
import com.mavenbox.ui.UIControl;
import com.mavenbox.ui.util.TransitionUtil;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Helper for notifications
 */
public class NotificationService extends Task<Void> {
  private final static Logger LOG = LoggerFactory.getLogger(NotificationService.class);

  private final static boolean ALWAYS_ON_TOP = Callete.getConfiguration().getBoolean("ui.alwaysOntop", true);

  private boolean running = true;
  private Queue<Notification> notificationQueue = new ArrayBlockingQueue<Notification>(100, true);
  private boolean blocked = false;

  public NotificationService() {

  }

  public void showNotification(Notification notification) {
    notificationQueue.add(notification);
    LOG.info(notification.toString());

    Thread thread = new Thread(this);
    thread.setName("Notification Thread for " + notification);
    thread.start();
  }


  @Override
  protected Void call() throws Exception {
    if(!blocked && !notificationQueue.isEmpty()) {
      blocked = true;

      Platform.runLater(() -> {
        Notification notification = notificationQueue.poll();
        NotificationNode notificationNode = new NotificationNode(notification);
        final Scene scene = new Scene(notificationNode, notification.getWidth(), notification.getHeight(), true, SceneAntialiasing.BALANCED);
        scene.getStylesheets().add(ResourceLoader.getResource("theme.css"));
        Stage stage = UIControl.getInstance().getStage();
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        scene.setFill(null);
        stage.setX(50);
        stage.setY(50);
        stage.show();

        FadeTransition outFader = TransitionUtil.createOutFader(notificationNode, 500);
        outFader.setDelay(Duration.millis(notification.getTimeout()));
        outFader.setOnFinished(event -> {
          stage.close();
          blocked = false;
        });
        outFader.play();
      });
    }
    return null;
  }
}
