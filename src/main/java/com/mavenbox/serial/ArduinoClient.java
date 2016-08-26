package com.mavenbox.serial;

import callete.api.Callete;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Connects to the Arduino and uses a listener thread to read commands
 * from the serial port.
 */
public class ArduinoClient {
  private final static Logger LOG = LoggerFactory.getLogger(ArduinoClient.class);
  private final static int TIME_OUT = Callete.getConfiguration().getInt("arduino.connect.timeout");

  private BufferedOutputStream output;
  private String port;
  private List<SerialCommandListener> commandListeners = new ArrayList<>();
  private List<StatusListener> statusListeners = new ArrayList<>();
  private boolean connected = false;
  private StringBuilder buffer = new StringBuilder();

  public ArduinoClient(String port) {
    this.port = port;
  }

  public void connect() {
    try {
      SerialPort serialPort = SerialPort.getCommPort(port);
      serialPort.setBaudRate(9600);
      serialPort.openPort();
      LOG.info("Connecting to Arduino on port " + port);
      serialPort.addDataListener(new SerialPortDataListener() {
        @Override
        public int getListeningEvents() {
          return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
        }

        @Override
        public void serialEvent(SerialPortEvent event) {
          if(event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
            return;
          }
          byte[] newData = new byte[serialPort.bytesAvailable()];
          serialPort.readBytes(newData, newData.length);

          String part = new String(newData);
          buffer.append(part);

          //format command string from buffer
          checkCommandBuffer();
        }
      });

      //Let's wait a little bit, otherwise the initial data connection doesn't work
      Thread.sleep(2000);
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

  private void checkCommandBuffer() {
    String commands = buffer.toString();
    if(commands.contains("}") && commands.contains("{") && (commands.lastIndexOf("{") < commands.lastIndexOf("}"))) {
      buffer = new StringBuilder();
      String cmd = null;

      try {
        cmd = commands.substring(commands.lastIndexOf("{"), commands.lastIndexOf("}") + 1).trim();
        LOG.info("Received command '" + cmd + "'");
        final SerialCommand serialCommand = new Gson().fromJson(cmd, SerialCommand.class);
        Thread thread = new Thread() {
          @Override
          public void run() {
            notifyCommand(serialCommand);
          }
        };
        thread.setName("Arduino Notification Thread");
        thread.start();
      } catch (Exception e) {
        LOG.error("Failed to parse JSON: " + e.getMessage(), e);
      }
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
   *
   * @param command the command to send to the Arduino.
   */
  public void sendCommand(final String command) {
    if(!connected) {
      LOG.info("Skipped command send, because Arduino is not available.");
      return;
    }

    Thread thread = new Thread() {
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
    };
    thread.setName("Arduino Serial Output");
    thread.start();
  }

  /**
   * Terminates the serial IO communication
   */
  public void shutdown() {
    connected = false;
    for(StatusListener listener : statusListeners) {
      listener.statusChanged(false);
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
