package com.mavenbox.ui.monitoring;

import callete.api.Callete;
import com.mavenbox.serial.ArduinoClient;
import com.mavenbox.serial.ArduinoCommandFactory;
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

  private boolean running = true;
  private Pipeline pipeline;

  public HostWatchDog(Pipeline pipeline) {
    this.pipeline = pipeline;
  }

  @Override
  public void run() {
    Thread.currentThread().setName("Host Watchdog [" + pipeline.getIndex() + "] '" + pipeline.getName() + "'");
    LOG.info("Started new monitoring watchdog [" + pipeline.getIndex() + "] '" + pipeline.getName() + "' for " + pipeline.getHost());
    while(running) {
      try {
        int returnCode = Callete.getMonitoringService().httpPing(pipeline.getHost());
        boolean available = returnCode == 200;
        updateMonitoringStatus(available);
        pipeline.setStatus(available);
      } catch (IOException e) {
        LOG.error("Failed to ping " + pipeline.getHost() + ": " + e.getMessage());
        updateMonitoringStatus(false);
        pipeline.setStatus(false);
      }

      try {
        Thread.sleep(TIMEOUT);
      } catch (InterruptedException e) {
        //ignore
      }
    }
  }

  private void updateMonitoringStatus(boolean available) {
    String cmd = ArduinoCommandFactory.createMonitoringStatusCommand(pipeline.getIndex(), available);
    ArduinoClient arduinoClient = UIControl.getInstance().getArduinoClient();
    if(arduinoClient != null && arduinoClient.isConnected()) {
      arduinoClient.sendCommand(cmd);
    }
  }
}
