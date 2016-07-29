package com.mavenbox.model;

import java.io.File;

/**
 * This model represents an entry in the scroller
 */
public class Workspace {

  private String name;

  public Workspace(File dir) {
    this.name = dir.getName();
  }

  public String getName() {
    return name;
  }
}
