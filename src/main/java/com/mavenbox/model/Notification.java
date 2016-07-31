package com.mavenbox.model;

/**
 * Contains the data to show for a notification
 */
public class Notification {

  private String title;
  private String message;
  private boolean status;

  public Notification(String title, String message, boolean status) {
    this.title = title;
    this.message = message;
    this.status = status;
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
}
