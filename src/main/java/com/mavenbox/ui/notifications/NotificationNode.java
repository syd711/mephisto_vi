package com.mavenbox.ui.notifications;

import com.mavenbox.ui.ResourceLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Panel that contains the notification and only visible for a short time
 */
public class NotificationNode extends VBox {

  public NotificationNode(Notification notification) {
    getStyleClass().add("notification-node");
    setMaxHeight(50);

    HBox titleBox = new HBox();
    titleBox.setAlignment(Pos.BASELINE_CENTER);
    titleBox.getStyleClass().addAll("title-box");
    Label title = new Label(notification.getTitle());
    title.getStyleClass().addAll("font-default", "title-font");
    titleBox.getChildren().add(title);
    getChildren().add(titleBox);

    if(notification.getMessage() != null) {
      HBox notificationMessageBox = new HBox();
      notificationMessageBox.setAlignment(Pos.CENTER);
      notificationMessageBox.getStyleClass().add("notification-message-node");
      Label notificationMessage = new Label(notification.getMessage());
      notificationMessage.getStyleClass().add("notification-font");
      notificationMessageBox.getChildren().add(notificationMessage);

      if(notification.isStatus() != null) {
        if(notification.isStatus()) {
          ImageView img = new ImageView(new Image(ResourceLoader.getResource("verification24.png"), 35, 35, false, true));
          notificationMessageBox.getChildren().add(img);
        }
        else {
          ImageView img = new ImageView(new Image(ResourceLoader.getResource("clear5.png"), 35, 35, false, true));
          notificationMessageBox.getChildren().add(img);
        }
      }

      getChildren().add(notificationMessageBox);
    }
    else {
      getChildren().add(notification.getPanel());
    }
  }
}
