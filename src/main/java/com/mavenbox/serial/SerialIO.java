package com.mavenbox.serial;

import com.google.gson.Gson;
import gnu.io.SerialPort;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Listener to receive the commands send by the Arduino.
 */
public class SerialIO extends Thread {
  private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(SerialIO.class);

  private SerialPort serialPort;
  private ArduinoClient arduinoClient;
  private boolean running = true;
  private BufferedReader input;

  public SerialIO(ArduinoClient arduinoClient, SerialPort serialPort) throws IOException {
    this.arduinoClient = arduinoClient;
    this.serialPort = serialPort;
    this.input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
  }

  public void run() {
    String cmd = null;
    while(running) {
      try {
        cmd = input.readLine();
        LOG.info("Received command '" + cmd + "'");
        SerialCommand serialCommand = new Gson().fromJson(cmd, SerialCommand.class);
        new Thread() {
          @Override
          public void run() {
            arduinoClient.notifyCommand(serialCommand);
          }
        }.start();
      }
      catch (IOException e) {
        LOG.error("Failed to read serial command: " + e.getMessage() + ", restarting...");
        arduinoClient.restart();
      }
      catch(IllegalStateException ise) {
        LOG.warn("Failed to parse json string '" + cmd + "': " + ise.getMessage());
      }
      catch(Exception ise) {
        LOG.warn("Failed to parse json string '" + cmd + "': " + ise.getMessage());
      }
    }
  }

  public void destroyIO() {
    this.running = false;
    this.serialPort.close();
  }
}
