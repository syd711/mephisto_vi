package com.mavenbox.ui;

import com.mavenbox.serial.SerialCommand;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mavenbox.ui.ControlEvent.Control.*;
import static com.mavenbox.ui.ControlEvent.Event.*;

/**
 *
 */
public class ControlKeyEventFilter implements EventHandler<KeyEvent> {
  private final static Logger LOG = LoggerFactory.getLogger(ControlKeyEventFilter.class);

  @Override
  public void handle(KeyEvent keyEvent) {
    LOG.info("Firing key code event for key " + keyEvent.getCode().getName());

    KeyCode code = keyEvent.getCode();
    if(code == KeyCode.RIGHT) {
      UIControl.getInstance().fireControlEvent(new ControlEvent(ROTARY_ENCODER, ROTATE_RIGHT));
    }
    else if(code == KeyCode.LEFT) {
      UIControl.getInstance().fireControlEvent(new ControlEvent(ROTARY_ENCODER, ROTATE_LEFT));
    }
    else if(code == KeyCode.UP) {
      UIControl.getInstance().fireControlEvent(new ControlEvent(ROTARY_ENCODER, PUSH));
    }
    else if(code == KeyCode.DOWN) {

    }
    else if(code == KeyCode.NUMPAD1) {
      UIControl.getInstance().fireControlEvent(new ControlEvent(GIT_PULL_SWITCH, ON));
    }
    else if(code == KeyCode.NUMPAD4) {
      UIControl.getInstance().fireControlEvent(new ControlEvent(GIT_PULL_SWITCH, OFF));
    }
    else if(code == KeyCode.NUMPAD2) {
      UIControl.getInstance().fireControlEvent(new ControlEvent(MAKE_SWITCH, ON));
    }
    else if(code == KeyCode.NUMPAD5) {
      UIControl.getInstance().fireControlEvent(new ControlEvent(MAKE_SWITCH, OFF));
    }
    else if(code == KeyCode.NUMPAD3) {
      UIControl.getInstance().fireControlEvent(new ControlEvent(GIT_PUSH_SWITCH, ON));
    }
    else if(code == KeyCode.NUMPAD6) {
      UIControl.getInstance().fireControlEvent(new ControlEvent(GIT_PUSH_SWITCH, OFF));
    }
    else if(code == KeyCode.NUMPAD7) {
      UIControl.getInstance().fireControlEvent(new ControlEvent(F1_PUSH_BUTTON, ON));
    }
    else if(code == KeyCode.NUMPAD8) {
      UIControl.getInstance().fireControlEvent(new ControlEvent(F2_PUSH_BUTTON, ON));
    }
    else if(code == KeyCode.NUMPAD9) {
      UIControl.getInstance().fireControlEvent(new ControlEvent(F3_PUSH_BUTTON, ON));
    }
    else if(code == KeyCode.NUMPAD0) {
      UIControl.getInstance().fireControlEvent(new ControlEvent(PIPELINE_PUSH_BUTTON, ON));
    }
    else if(code == KeyCode.Q || code == KeyCode.ESCAPE) {
      System.exit(0);
    }
  }

  public static ControlEvent toControlEvent(SerialCommand command) {
    if(command.getCmd().equals("push")) {
      return new ControlEvent(ROTARY_ENCODER, PUSH);
    }
    else if(command.getDirection() != null && command.getDirection().equals("left")) {
      return new ControlEvent(ROTARY_ENCODER, ROTATE_LEFT);
    }
    else if(command.getDirection() != null && command.getDirection().equals("right")) {
      return new ControlEvent(ROTARY_ENCODER, ROTATE_RIGHT);
    }
    return null;
  }
}