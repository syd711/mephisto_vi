package com.mavenbox.ui.monitoring;

/**
 * Model for a pipeline
 */
public class Pipeline {
  private int index;
  private String name;
  private String host;
  private boolean status;
  private String link;

  public Pipeline(int index, String name, String host, String link) {
    this.index = index;
    this.name = name;
    this.host = host;
    this.link = link;
  }

  public int getIndex() {
    return index;
  }

  public String getName() {
    return name;
  }

  public boolean isStatus() {
    return status;
  }

  public String getHost() {
    return host;
  }

  public void setStatus(boolean status) {
    this.status = status;
  }

  public String getLink() {
    return link;
  }
}
