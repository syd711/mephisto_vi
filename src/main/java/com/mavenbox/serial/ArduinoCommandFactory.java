package com.mavenbox.serial;

/**
 * Factory for creating command string that can be read by the Arduino.
 */
public class ArduinoCommandFactory {
  private final static String CMD_TERMINATOR = "|";

  private final static String CMD_STATUS_AVAILABLE = "status";
  private final static String CMD_BLINK = "blink";
  private final static String CMD_MONITORING_WATCH_DOG = "monitoring";

  public static String createStatusCommand() {
    return CMD_STATUS_AVAILABLE + ":true" + CMD_TERMINATOR;
  }

  public static String createMonitoringStatusCommand(int index, boolean available) {
    return CMD_MONITORING_WATCH_DOG + ":" + index + ":" + available + CMD_TERMINATOR;
  }

  public static String createBlinkCommand(boolean blink) {
    if(blink) {
      return CMD_BLINK + ":1";
    }
    return CMD_BLINK + ":0";
  }
}
