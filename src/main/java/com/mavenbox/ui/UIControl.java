package com.mavenbox.ui;

import callete.api.Callete;
import com.mavenbox.BuildController;
import com.mavenbox.model.Notification;
import com.mavenbox.model.Workspaces;
import com.mavenbox.monitoring.Monitoring;
import com.mavenbox.serial.ArduinoClient;
import com.mavenbox.serial.SerialCommand;
import com.mavenbox.serial.SerialCommandListener;
import com.mavenbox.ui.notifications.Notifications;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.input.KeyEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton for handling all control communication
 */
public class UIControl implements ControlEventListener, SerialCommandListener {

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
    initServices();

    this.stage = stage;
    this.stage.initStyle(StageStyle.TRANSPARENT);

    showProjects(true);
    eventListeners.add(this);
  }

  private void initServices() {
    Monitoring.init();
    Workspaces.init();

    String port = Callete.getConfiguration().getString("arduino.port");
    arduinoClient = new ArduinoClient(port);
    arduinoClient.addSerialCommandListener(this);

    new Thread() {
      public void run() {
        arduinoClient.connect();
      }
    }.start();
  }

  public ArduinoClient getArduinoClient() {
    return arduinoClient;
  }

  public void showProjects(boolean visible) {
    if(visible) {
      Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

      Navigation navigation = new Navigation();
      final Scene scene = new Scene(navigation, primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight(), true, SceneAntialiasing.BALANCED);
      scene.getStylesheets().add(ResourceLoader.getResource("theme.css"));
      stage.setScene(scene);
      scene.setFill(null);
      stage.setAlwaysOnTop(true);
      stage.centerOnScreen();
      stage.addEventFilter(KeyEvent.KEY_PRESSED, new ControlKeyEventFilter());
      stage.setX(0);
      stage.setY(0);
      stage.show();

      setRotaryEncoderControl(navigation);
      stage.show();
    }
    else {
      stage.hide();
    }
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

    switch(source) {
      case ROTARY_ENCODER: {
        handleRotaryEncoderEvent(e);
        break;
      }
      case GIT_PULL_SWITCH: {
        BuildController.getInstance().setPull(e.equals(ControlEvent.Event.ON));
        Notification notification = new Notification("Notification", "Git Pull:", e.equals(ControlEvent.Event.ON));
        Notifications.showNotification(stage, notification);
        break;
      }
      case MAKE_SWITCH: {
        BuildController.getInstance().setMake(e.equals(ControlEvent.Event.ON));
        Notification notification = new Notification("Notification", "Maven Build:", e.equals(ControlEvent.Event.ON));
        Notifications.showNotification(stage, notification);
        break;
      }
      case GIT_PUSH_SWITCH: {
        BuildController.getInstance().setPush(e.equals(ControlEvent.Event.ON));
        Notification notification = new Notification("Notification", "Git Push:", e.equals(ControlEvent.Event.ON));
        Notifications.showNotification(stage, notification);
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
        if(rotaryEncoderControl != null) {
          if(!stage.isShowing()) {
            stage.show();
          }
          else {
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
        controlChanged(ControlKeyEventFilter.toControlEvent(command));
      }
    });
  }
}
