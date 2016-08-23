package com.mavenbox.ui.projects;

import com.mavenbox.serial.SerialCommand;

/**
 * Contains all data of a user input
 */
public class ControlEvent {
  public enum Event {
    ON,
    OFF,
    PUSH,
    ROTATE_LEFT,
    ROTATE_RIGHT
  }

  public enum Control {
    ROTARY_ENCODER,
    SWITCH_1,
    SWITCH_2,
    SWITCH_3,
    PIPELINE_PUSH_BUTTON,
    F1_PUSH_BUTTON,
    F2_PUSH_BUTTON,
    F3_PUSH_BUTTON
  }

  private Control source;
  private Event event;
  private String message;
  private boolean silent;

  public ControlEvent(SerialCommand command) {
    if(command.getSource() != null) {
      this.source = Control.valueOf(command.getSource());
    }
    if(command.getEvent() != null) {
      this.event = Event.valueOf(command.getEvent());
    }

    this.message = command.getMessage();
    this.silent = command.isSilent() == 1;
  }

  /**
   * This constructor is only used for keyboard emulation.
   */
  public ControlEvent(Control source, Event event) {
    this.source = source;
    this.event = event;
  }

  public String getMessage() {
    return message;
  }

  public Event getEvent() {
    return event;
  }

  public Control getSource() {
    return source;
  }

  public boolean isSilent() {
    return silent;
  }
}
