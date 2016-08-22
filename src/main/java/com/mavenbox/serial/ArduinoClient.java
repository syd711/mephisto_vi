package com.mavenbox.serial;

import callete.api.Callete;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


/**
 * Connects to the Arduino and uses a listener thread to read commands
 * from the serial port.
 */
public class ArduinoClient {
  private final static Logger LOG = LoggerFactory.getLogger(ArduinoClient.class);
  private final static int TIME_OUT = Callete.getConfiguration().getInt("arduino.connect.timeout");

  private SerialIO serialIO;
  private BufferedOutputStream output;
  private String port;
  private List<SerialCommandListener> commandListeners = new ArrayList<>();
  private List<StatusListener> statusListeners = new ArrayList<>();
  private boolean connected = false;

  public ArduinoClient(String port) {
    this.port = port;
  }

  public void connect() {
    try {
      Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();
      while(portIdentifiers.hasMoreElements()) {
        portIdentifiers.nextElement();
      }
      LOG.info("Connecting to Arduino on port " + port);
      CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(port);
      SerialPort serialPort = (SerialPort) portIdentifier.open(this.getClass().getName(), TIME_OUT);
      serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
      //Let's wait a little bit, otherwise the initial data connection doesn't work
      Thread.sleep(2000);
      serialIO = new SerialIO(this, serialPort);
      serialIO.start();
      this.output = new BufferedOutputStream(serialPort.getOutputStream());
      connected = true;
      sendCommand(ArduinoCommandFactory.createStatusCommand());

      for(StatusListener listener : statusListeners) {
        listener.statusChanged(true);
      }
      LOG.info("Arduino Client connect successful!");
    } catch (Exception e) {
      connected = false;
      LOG.error("Failed to open port '" + port + "': " + e.toString());
      restart();
    }
  }

  public void addSerialCommandListener(SerialCommandListener listener) {
    this.commandListeners.add(listener);
  }

  public void addStatusListener(StatusListener listener) {
    this.statusListeners.add(listener);
  }

  /**
   * Sends the given command to the Arduino.
   * The command string must match one of the command constants
   * of this class
   * @param command the command to send to the Arduino.
   */
  public void sendCommand(final String command) {
    if(!connected) {
      LOG.info("Skipped command send, because Arduino is not available.");
      return;
    }

    new Thread() {
      @Override
      public void run() {
        try {
          Thread.currentThread().setName("Arduino Serial Output");
          LOG.info("Sending command string '" + command + "'");
          output.write(command.getBytes());
          output.flush();
        } catch (IOException e) {
          LOG.error("Failed to send command '" + command + "' to Arduino: " + e.getMessage());
        }
      }
    }.start();
  }

  /**
   * Terminates the serial IO communication
   */
  public void shutdown() {
    connected = false;
    for(StatusListener listener : statusListeners) {
      listener.statusChanged(false);
    }
    if(serialIO != null) {
      serialIO.destroyIO();
    }
  }

  /**
   * Executed if the serial connection fails, a restart is triggered every n seconds.
   */
  public void restart() {
    shutdown();
    LOG.info("Terminating serial connecting, waiting for reconnect attempt...");
    try {
      Thread.sleep(TIME_OUT);
      connect();
    } catch (InterruptedException e) {
      //ignore
    }
  }

  protected void notifyCommand(SerialCommand cmd) {
    for(SerialCommandListener commandListener : commandListeners) {
      commandListener.commandReceived(cmd);
    }
  }

  public boolean isConnected() {
    return connected;
  }
}
