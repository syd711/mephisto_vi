package com.mavenbox.serial;

/**
 * Factory for creating command string that can be read by the Arduino.
 */
public class ArduinoCommandFactory {
  private final static String CMD_TERMINATOR = "|";

  private final static String CMD_STATUS_AVAILABLE = "status";
  private final static String CMD_MONITORING_WATCH_DOG = "monitoring";

  public static String createStatusCommand() {
    return CMD_STATUS_AVAILABLE + ":true" + CMD_TERMINATOR;
  }

  public static String createMonitoringStatusCommand(int index, boolean available) {
    return CMD_MONITORING_WATCH_DOG + ":" + index + ":" + available + CMD_TERMINATOR;
  }
}
