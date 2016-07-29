package com.mavenbox.ui;

/**
 * Contains all data of a user input
 */
public class ControlEvent {
  public static enum Event {PUSH, ROTATE_LEFT, ROTATE_RIGHT};

  public static enum Control {
    ROTARY_ENCODER
  }

  private Control source;
  private Event event;

  public ControlEvent(Control source, Event event) {
    this.source = source;
    this.event = event;
  }

  public Event getEvent() {
    return event;
  }

  public Control getSource() {
    return source;
  }
}
