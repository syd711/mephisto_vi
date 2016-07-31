package com.mavenbox.ui.projects;

/**
 * Listener to be implemented by all components that listen on input events
 */
public interface ControlEventListener {

  /**
   * Fired when an input control of the box has changed.
   * @param event  the event that encapsulates all information about the input the user has made
   */
  void controlChanged(ControlEvent event);
}
