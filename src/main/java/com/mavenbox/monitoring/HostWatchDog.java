package com.mavenbox.monitoring;

import callete.api.Callete;
import com.mavenbox.ui.UIControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Thread for checking if a host is available via HTTP request
 */
public class HostWatchDog extends Thread {
  private final static Logger LOG = LoggerFactory.getLogger(HostWatchDog.class);
  private final static int TIMEOUT = Callete.getConfiguration().getInt("monitoring.timeout.millis");

  private String name;
  private String host;
  private boolean running = true;
  private int index;

  public HostWatchDog(int index, String name, String host) {
    this.index = index;
    this.name = name;
    this.host = host;
  }

  @Override
  public void run() {
    Thread.currentThread().setName("Host Watchdog '" + name + "'");
    while(running) {
      try {
        Callete.getMonitoringService().httpPing(host, 80);
      } catch (IOException e) {
        LOG.error("Failed to ping " + host + ": " + e.getMessage());
        updateMonitoringStatus(false);
      }

      try {
        Thread.sleep(TIMEOUT);
      } catch (InterruptedException e) {
        //ignore
      }
      updateMonitoringStatus(true);
    }
  }

  private void updateMonitoringStatus(boolean available) {
    UIControl.getInstance().getArduinoClient().updateMonitoringState(index, available);
  }
}
