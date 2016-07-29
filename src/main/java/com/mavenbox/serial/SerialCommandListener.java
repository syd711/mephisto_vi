package com.mavenbox.serial;

/**
 * Interface to be implemented by services that want to listen on Arduino events.
 */
public interface SerialCommandListener {

  void commandReceived(SerialCommand command);
}
