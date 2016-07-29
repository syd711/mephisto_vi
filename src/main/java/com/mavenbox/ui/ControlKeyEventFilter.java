package com.mavenbox.ui;

import com.mavenbox.serial.SerialCommand;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mavenbox.ui.ControlEvent.Control.ROTARY_ENCODER;
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
    else if(code == KeyCode.DOWN) {

    }
    else if(code == KeyCode.UP) {
      UIControl.getInstance().fireControlEvent(new ControlEvent(ROTARY_ENCODER, PUSH));
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