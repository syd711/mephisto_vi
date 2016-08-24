package com.mavenbox.ui.projects;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 *
 */
public class BranchNavigationNode extends HBox {

  private Branch branch;
  private boolean active;

  public BranchNavigationNode(Branch branch, boolean active) {
    this.branch = branch;
    this.active = active;
    init();
  }

  public Branch getBranch() {
    return branch;
  }

  private void init() {
    String cls = "branch-node";
    if(active) {
      cls+= "--active";
    }
    getStyleClass().add(cls);
    setMinHeight(30);
    Label branchName = new Label(branch.getName());
    getChildren().add(branchName);
  }

  public void select(boolean selected) {
    if(selected) {
      getStyleClass().add("branch-node--selected");
    }
    else {
      getStyleClass().remove("branch-node--selected");
    }
  }
}
