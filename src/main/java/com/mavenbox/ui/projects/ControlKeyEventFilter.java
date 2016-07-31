package com.mavenbox.ui.projects;

import com.mavenbox.ui.UIControl;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
      UIControl.getInstance().fireControlEvent(new ControlEvent(ControlEvent.Control.ROTARY_ENCODER, ControlEvent.Event.ROTATE_RIGHT));
    }
    else if(code == KeyCode.LEFT) {
      UIControl.getInstance().fireControlEvent(new ControlEvent(ControlEvent.Control.ROTARY_ENCODER, ControlEvent.Event.ROTATE_LEFT));
    }
    else if(code == KeyCode.UP) {
      UIControl.getInstance().fireControlEvent(new ControlEvent(ControlEvent.Control.ROTARY_ENCODER, ControlEvent.Event.PUSH));
    }
    else if(code == KeyCode.DOWN) {

    }
    else if(code == KeyCode.NUMPAD1) {
      UIControl.getInstance().fireControlEvent(new ControlEvent(ControlEvent.Control.GIT_PULL_SWITCH, ControlEvent.Event.ON));
    }
    else if(code == KeyCode.NUMPAD4) {
      UIControl.getInstance().fireControlEvent(new ControlEvent(ControlEvent.Control.GIT_PULL_SWITCH, ControlEvent.Event.OFF));
    }
    else if(code == KeyCode.NUMPAD2) {
      UIControl.getInstance().fireControlEvent(new ControlEvent(ControlEvent.Control.MAKE_SWITCH, ControlEvent.Event.ON));
    }
    else if(code == KeyCode.NUMPAD5) {
      UIControl.getInstance().fireControlEvent(new ControlEvent(ControlEvent.Control.MAKE_SWITCH, ControlEvent.Event.OFF));
    }
    else if(code == KeyCode.NUMPAD3) {
      UIControl.getInstance().fireControlEvent(new ControlEvent(ControlEvent.Control.GIT_PUSH_SWITCH, ControlEvent.Event.ON));
    }
    else if(code == KeyCode.NUMPAD6) {
      UIControl.getInstance().fireControlEvent(new ControlEvent(ControlEvent.Control.GIT_PUSH_SWITCH, ControlEvent.Event.OFF));
    }
    else if(code == KeyCode.NUMPAD7) {
      UIControl.getInstance().fireControlEvent(new ControlEvent(ControlEvent.Control.F1_PUSH_BUTTON, ControlEvent.Event.ON));
    }
    else if(code == KeyCode.NUMPAD8) {
      UIControl.getInstance().fireControlEvent(new ControlEvent(ControlEvent.Control.F2_PUSH_BUTTON, ControlEvent.Event.ON));
    }
    else if(code == KeyCode.NUMPAD9) {
      UIControl.getInstance().fireControlEvent(new ControlEvent(ControlEvent.Control.F3_PUSH_BUTTON, ControlEvent.Event.ON));
    }
    else if(code == KeyCode.NUMPAD0) {
      UIControl.getInstance().fireControlEvent(new ControlEvent(ControlEvent.Control.PIPELINE_PUSH_BUTTON, ControlEvent.Event.ON));
    }
    else if(code == KeyCode.Q || code == KeyCode.ESCAPE) {
      Projects.showProjects(null, false);
    }
  }
}