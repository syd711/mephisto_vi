package com.mavenbox.serial;

/**
 * Interface to listen on the Arduino connection state
 */
public interface StatusListener {

  void statusChanged(boolean connected);
}
