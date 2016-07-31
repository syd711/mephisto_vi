package com.mavenbox;

/**
 * Singleton that manages the maven builds and keeps the build configuration status
 */
public class BuildController {
  private static BuildController instance = new BuildController();

  private boolean pull = false;
  private boolean make = false;
  private boolean push = false;

  public static BuildController getInstance() {
    return instance;
  }

  private BuildController() {
    //force singleton
  }


  public void setMake(boolean make) {
    this.make = make;
  }

  public void setPull(boolean pull) {
    this.pull = pull;
  }

  public void setPush(boolean push) {
    this.push = push;
  }
}
