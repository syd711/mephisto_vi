package com.mavenbox.serial;

import gnu.io.SerialPort;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Listener to receive the commands send by the Arduino.
 */
public class ArduinoIOListener extends Thread {
  private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(ArduinoIOListener.class);

  private ArduinoClient arduinoClient;
  private boolean running = true;
  private InputStream input;
  private SerialPort serialPort;

  public ArduinoIOListener(ArduinoClient arduinoClient, SerialPort serialPort) throws IOException {
    this.arduinoClient = arduinoClient;
    this.serialPort = serialPort;
    this.input = serialPort.getInputStream();
//    this.input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
  }

  public void run() {
    String cmd = null;
    while(running) {
//      try {
//        cmd = input.readLine();
//        LOG.info("Received command '" + cmd + "'");
//        SerialCommand serialCommand = new Gson().fromJson(cmd, SerialCommand.class);
//        Thread thread = new Thread()  {
//          @Override
//          public void run() {
//            arduinoClient.notifyCommand(serialCommand);
//          }
//        };
//        thread.setName("Arduino Notification Thread");
//        thread.start();
//      }
      byte[] buffer = new byte[1024];
      int len = -1;
      try
      {
        while ( ( len = this.input.read(buffer)) > -1 )
        {
          System.out.print(new String(buffer,0,len));
        }
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
