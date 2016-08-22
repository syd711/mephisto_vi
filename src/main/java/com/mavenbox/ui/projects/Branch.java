package com.mavenbox.ui.projects;

import org.eclipse.jgit.lib.Ref;

/**
 * GIT model for a branch
 */
public class Branch {

  private String name;
  private Workspace workspace;
  private Ref branch;

  public Branch(Workspace workspace, Ref branch) {
    this.name = branch.getName();
    this.workspace = workspace;
    this.branch = branch;
  }

  public String getName() {
    return name;
  }
}
