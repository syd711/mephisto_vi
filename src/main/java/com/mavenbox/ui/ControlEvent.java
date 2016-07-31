package com.mavenbox.ui;

/**
 * Contains all data of a user input
 */
public class ControlEvent {
  public enum Event {ON, OFF, PUSH, ROTATE_LEFT, ROTATE_RIGHT}

  public enum Control {
    ROTARY_ENCODER,
    GIT_PUSH_SWITCH,
    GIT_PULL_SWITCH,
    MAKE_SWITCH,
    PIPELINE_PUSH_BUTTON,
    F1_PUSH_BUTTON,
    F2_PUSH_BUTTON,
    F3_PUSH_BUTTON
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
