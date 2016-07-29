package com.mavenbox.ui;

import callete.api.Callete;
import com.mavenbox.model.Workspaces;
import com.mavenbox.monitoring.Monitoring;
import com.mavenbox.serial.ArduinoClient;
import com.mavenbox.serial.SerialCommand;
import com.mavenbox.serial.SerialCommandListener;
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

  private Navigation navigation;
  private ArduinoClient arduinoClient;
  private Stage stage;

  //force singleton
  private UIControl() {
  }

  public static UIControl getInstance() {
    return instance;
  }

  public void init(Stage stage) {
    initServices();

    this.stage = stage;
    stage.initStyle(StageStyle.TRANSPARENT);
    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

    Navigation navigation = new Navigation();
    final Scene scene = new Scene(navigation, primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight(), true, SceneAntialiasing.BALANCED);
    stage.setScene(scene);
    scene.setFill(null);
    scene.getStylesheets().add(ResourceLoader.getResource("theme.css"));
//    stage.setAlwaysOnTop(true);
    stage.centerOnScreen();
    stage.addEventFilter(KeyEvent.KEY_PRESSED, new ControlKeyEventFilter());
    //TODO disable
    stage.show();

    this.navigation = navigation;
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
    if(event.getEvent().equals(ControlEvent.Event.ROTATE_RIGHT)) {
      this.navigation.scrollRight();
    }
    else if(event.getEvent().equals(ControlEvent.Event.ROTATE_LEFT)) {
      this.navigation.scrollLeft();
    }
    else if(event.getEvent().equals(ControlEvent.Event.PUSH)) {
      if(stage.isShowing()) {

      }
      else {
        stage.show();
      }

    }
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
