package com.mavenbox.ui.projects;

/**
 * Interface to be implemented by components that want to be controller by the rotary encoder.
 */
public interface RotaryEncoderControlled {

  void rotateLeft();

  void rotateRight();

  void push();
}
