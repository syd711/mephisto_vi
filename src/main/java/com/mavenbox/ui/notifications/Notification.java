package com.mavenbox.ui.notifications;

import callete.api.Callete;
import javafx.scene.Node;

/**
 * Contains the data to show for a notification
 */
public class Notification {
  private final static int WIDTH = 200;
  private final static int HEIGHT = 100;
  private final static int NOTIFICATIONS_TIMEOUT = Callete.getConfiguration().getInt("notifications.timeout");

  private String title;
  private String message;
  private Boolean status;
  private int timeout;
  private int width = WIDTH;
  private int height = HEIGHT;
  private Node panel;

  public Notification(String title, Node panel, int width, int height, int timeout) {
    this.title = title;
    this.width = width;
    this.height = height;
    this.timeout = timeout;
    this.panel = panel;
  }

  public Notification(String title, String message, Boolean status, int width, int height, int timeout) {
    this.title = title;
    this.message = message;
    this.status = status;
    this.width = width;
    this.height = height;
    this.timeout = timeout;
  }

  public Notification(String title, String message, Boolean status, int width, int timeout) {
    this(title, message, status, width, HEIGHT, timeout);
  }

  public Notification(String title, String message, Boolean status, int width) {
    this(title, message, status, width, HEIGHT, NOTIFICATIONS_TIMEOUT);
  }

  public Notification(String title, String message, Boolean status) {
    this(title, message, status, WIDTH, HEIGHT, NOTIFICATIONS_TIMEOUT);
  }

  public Notification(String title, String message) {
    this(title, message, null, WIDTH, HEIGHT, NOTIFICATIONS_TIMEOUT);
  }

  public Boolean isStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }

  public String getTitle() {
    return title;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public int getTimeout() {
    return timeout;
  }

  @Override
  public String toString() {
    return "Notification{" +
        "title='" + title + '\'' +
        ", message='" + message + '\'' +
        ", timeout=" + timeout +
        ", status=" + status +
        ", width=" + width +
        ", height=" + height +
        '}';
  }

  public Node getPanel() {
    return panel;
  }
}
