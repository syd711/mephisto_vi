package com.mavenbox.ui;

import callete.api.Callete;
import com.mavenbox.BuildController;
import com.mavenbox.serial.ArduinoClient;
import com.mavenbox.serial.SerialCommand;
import com.mavenbox.serial.SerialCommandListener;
import com.mavenbox.serial.StatusListener;
import com.mavenbox.ui.notifications.Notification;
import com.mavenbox.ui.notifications.Notifications;
import com.mavenbox.ui.projects.*;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton for handling all control communication
 */
public class UIControl implements ControlEventListener, SerialCommandListener, StatusListener {
  private final static Logger LOG = LoggerFactory.getLogger(UIControl.class);
  private static UIControl instance = new UIControl();

  private List<ControlEventListener> eventListeners = new ArrayList<>();

  private ArduinoClient arduinoClient;
  private Stage stage;
  private RotaryEncoderControlled rotaryEncoderControl;

  //force singleton
  private UIControl() {
  }

  public static UIControl getInstance() {
    return instance;
  }

  public void init(Stage stage) {
    Platform.setImplicitExit(false);

    initServices();

    this.stage = stage;
    this.stage.initStyle(StageStyle.TRANSPARENT);

    eventListeners.add(this);
  }

  private void initServices() {
    String port = Callete.getConfiguration().getString("arduino.port");
    arduinoClient = new ArduinoClient(port);
    arduinoClient.addSerialCommandListener(this);
    arduinoClient.addStatusListener(this);

    new Thread() {
      public void run() {
        Thread.currentThread().setName("Arduino Client");
        arduinoClient.connect();
      }
    }.start();

    //Monitoring.init();
    Workspaces.init();
  }

  public ArduinoClient getArduinoClient() {
    return arduinoClient;
  }



  public void fireControlEvent(ControlEvent event) {
    for(ControlEventListener eventListener : eventListeners) {
      eventListener.controlChanged(event);
    }
  }

  public void addEventListener(ControlEventListener listener) {
    this.eventListeners.add(listener);
  }

  @Override
  public void controlChanged(ControlEvent event) {
    ControlEvent.Control source = event.getSource();
    ControlEvent.Event e = event.getEvent();

    if(event.getMessage() != null) {
      LOG.info("Arduino says: " + event.getMessage());
    }

    if(event.getSource() == null) {
      return;
    }

    switch(source) {
      case ROTARY_ENCODER: {
        handleRotaryEncoderEvent(e);
        break;
      }
      case SWITCH_1: {
        BuildController.getInstance().setPull(e.equals(ControlEvent.Event.ON));
        Notification notification = new Notification("Notification", "Git Pull:", e.equals(ControlEvent.Event.ON));
        Notifications.showNotification(stage, notification);
        break;
      }
      case SWITCH_2: {
        BuildController.getInstance().setMake(e.equals(ControlEvent.Event.ON));
        Notification notification = new Notification("Notification", "Maven Build:", e.equals(ControlEvent.Event.ON), 250);
        Notifications.showNotification(stage, notification);
        break;
      }
      case SWITCH_3: {
        BuildController.getInstance().setPush(e.equals(ControlEvent.Event.ON));
        Notification notification = new Notification("Notification", "Git Push:", e.equals(ControlEvent.Event.ON));
        Notifications.showNotification(stage, notification);
        break;
      }
      case PIPELINE_PUSH_BUTTON: {
        break;
      }
      case F1_PUSH_BUTTON: {
        break;
      }
      case F2_PUSH_BUTTON: {
        break;
      }
      case F3_PUSH_BUTTON: {
        break;
      }
    }
  }

  private void handleRotaryEncoderEvent(ControlEvent.Event e) {
    switch(e) {
      case ROTATE_RIGHT: {
        if(rotaryEncoderControl != null) {
          rotaryEncoderControl.rotateLeft();
        }
        break;
      }
      case ROTATE_LEFT: {
        if(rotaryEncoderControl != null) {
          rotaryEncoderControl.rotateRight();
        }
        break;
      }
      case PUSH: {
        if(!stage.isShowing()) {
          Projects.showProjects(stage, true);
        }
        else {
          if(rotaryEncoderControl != null) {
            rotaryEncoderControl.push();
          }
        }
        break;
      }
    }
  }

  public void setRotaryEncoderControl(RotaryEncoderControlled rotaryEncoderControl) {
    this.rotaryEncoderControl = rotaryEncoderControl;
  }

  @Override
  public void commandReceived(SerialCommand command) {
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        controlChanged(new ControlEvent(command));
      }
    });
  }

  @Override
  public void statusChanged(boolean connected) {
    Platform.runLater(() -> {
      if(arduinoClient.isConnected()) {
        Notifications.showNotification(stage, new Notification("Maven Box Status", "Maven Box Status", connected, 350, 100));
      }
    });
  }
}
