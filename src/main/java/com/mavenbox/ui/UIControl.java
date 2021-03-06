package com.mavenbox.ui;

import callete.api.Callete;
import callete.api.util.SystemCommandExecutor;
import callete.api.util.SystemUtils;
import com.mavenbox.serial.ArduinoClient;
import com.mavenbox.serial.SerialCommand;
import com.mavenbox.serial.SerialCommandListener;
import com.mavenbox.serial.StatusListener;
import com.mavenbox.ui.monitoring.MonitoringService;
import com.mavenbox.ui.monitoring.PipelinesNode;
import com.mavenbox.ui.notifications.Notification;
import com.mavenbox.ui.notifications.NotificationService;
import com.mavenbox.ui.projects.*;
import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import com.sun.javafx.application.HostServicesDelegate;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
  private Application application;

  private RotaryEncoderControlled rotaryEncoderControl;
  private boolean pullEnabled;
  private boolean makeEnabled;
  private boolean pushEnabled;

  private NotificationService notificationService;
  private MonitoringService monitoringService;
  private WorkspaceService workspaceService;

  //force singleton
  private UIControl() {
  }

  public static UIControl getInstance() {
    return instance;
  }

  public void init(Application application, Stage stage) {
    this.application = application;
    stage.setTitle("Maven Box");
    stage.getIcons().add(new Image(ResourceLoader.getResource("logo.png"), 48, 48, true, true));
    Platform.setImplicitExit(false);

    initServices();

    this.stage = stage;
    this.stage.initStyle(StageStyle.TRANSPARENT);

    eventListeners.add(this);
  }

  public Stage getStage() {
    return stage;
  }


  private void initServices() {
    notificationService = new NotificationService();
    monitoringService = new MonitoringService();
    workspaceService = new WorkspaceService();

    String port = Callete.getConfiguration().getString("arduino.port");
    arduinoClient = new ArduinoClient(port);
    arduinoClient.addSerialCommandListener(this);
    arduinoClient.addStatusListener(this);

    new Thread() {
      public void run() {
        Thread.currentThread().setName("Arduino Client");
        arduinoClient.connect();

        monitoringService.init();
      }
    }.start();
  }

  public MonitoringService getMonitoringService() {
    return monitoringService;
  }

  public ArduinoClient getArduinoClient() {
    return arduinoClient;
  }

  public WorkspaceService getWorkspaceService() {
    return workspaceService;
  }

  public void fireControlEvent(ControlEvent event) {
    for(ControlEventListener eventListener : eventListeners) {
      eventListener.controlChanged(event);
    }
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
        pullEnabled = e.equals(ControlEvent.Event.ON);
        if(!event.isSilent()) {
          Notification notification = new Notification("Build Control", "Git Pull", pullEnabled);
          notificationService.showNotification(notification);
        }
        break;
      }
      case SWITCH_2: {
        makeEnabled = e.equals(ControlEvent.Event.ON);
        if(!event.isSilent()) {
          Notification notification = new Notification("Build Control", "Maven Build", makeEnabled, 250);
          notificationService.showNotification(notification);
        }
        break;
      }
      case SWITCH_3: {
        pushEnabled = e.equals(ControlEvent.Event.ON);
        if(!event.isSilent()) {
          Notification notification = new Notification("Build Control", "Git Push", pushEnabled);
          notificationService.showNotification(notification);
        }
        break;
      }
      case PIPELINE_PUSH_BUTTON: {
        Notification notification = new Notification("Pipelines", new PipelinesNode(), 460, 530, 5000);
        notificationService.showNotification(notification);
        break;
      }
      case F1_PUSH_BUTTON: {
        notificationService.showState();
        break;
      }
      case F2_PUSH_BUTTON: {
        LOG.info("Max Memory: " + SystemUtils.humanReadableByteCount(Callete.getSystemService().getMaxMemory()) + " bytes");
        LOG.info("Free Memory: " + SystemUtils.humanReadableByteCount(Callete.getSystemService().getFreeMemory()) + " bytes");
        LOG.info("Performing GC...");
        System.gc();
        LOG.info("Free Memory: " + SystemUtils.humanReadableByteCount(Callete.getSystemService().getFreeMemory()) + " bytes");
        break;
      }
      case F3_PUSH_BUTTON: {
        executeFunctionKey("f3");
        break;
      }
    }
  }

  private void executeFunctionKey(String key) {
    String path = Callete.getConfiguration().getString("function.keys.home");
    SystemCommandExecutor executor = new SystemCommandExecutor(Arrays.asList(path + key+".bat"));
    executor.setDir(new File("conf/"));
    executor.executeCommandAsync();
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
    notificationService.showNotification(new Notification("Maven Box Status", "Maven Box Status", connected, 350));
  }

  public NotificationService getNotificationService() {
    return notificationService;
  }

  public boolean isPushEnabled() {
    return pushEnabled;
  }

  public boolean isMakeEnabled() {
    return makeEnabled;
  }

  public boolean isPullEnabled() {
    return pullEnabled;
  }

  public void open(String link) {
    HostServicesDelegate hostServices = HostServicesFactory.getInstance(application);
    hostServices.showDocument(link);
  }
}
