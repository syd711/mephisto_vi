package com.mavenbox.ui.projects;

import org.eclipse.jgit.lib.Ref;

/**
 * GIT model for a branch
 */
public class Branch {

  private Workspace workspace;
  private Ref branch;

  public Branch(Workspace workspace, Ref branch) {
    this.workspace = workspace;
    this.branch = branch;
  }

  public String getName() {
    String selectedBranch = branch.getName();
    if(selectedBranch.contains("/")) {
      selectedBranch = selectedBranch.substring(selectedBranch.lastIndexOf("/")+1, selectedBranch.length());
    }
    return selectedBranch;
  }

  public Ref getRef() {
    return branch;
  }
}
