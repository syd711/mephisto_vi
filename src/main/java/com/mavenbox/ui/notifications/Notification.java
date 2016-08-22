package com.mavenbox.ui.notifications;

/**
 * Contains the data to show for a notification
 */
public class Notification {
  private final static int WIDTH = 200;
  private final static int HEIGHT = 100;

  private String title;
  private String message;
  private boolean status;
  private int width = WIDTH;
  private int height = HEIGHT;

  public Notification(String title, String message, boolean status, int width, int height) {
    this.title = title;
    this.message = message;
    this.status = status;
    this.width = width;
    this.height = height;
  }

  public Notification(String title, String message, boolean status, int width) {
    this(title, message, status, width, HEIGHT);
  }

  public Notification(String title, String message, boolean status) {
    this(title, message, status, WIDTH, HEIGHT);
  }

  public boolean isStatus() {
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
}
