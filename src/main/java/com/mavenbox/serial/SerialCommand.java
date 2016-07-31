package com.mavenbox.serial;

/**
 * Pojo created from the JSON send by the arduino.
 */
public class SerialCommand {
  private String source;
  private String event;

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getEvent() {
    return event;
  }

  public void setEvent(String event) {
    this.event = event;
  }
}
