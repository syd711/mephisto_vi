package com.mavenbox.ui;

import com.mavenbox.model.Branch;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 *
 */
public class BranchNavigation extends HBox {

  private Branch branch;

  public BranchNavigation(Branch branch) {
    this.branch = branch;
    init();
  }

  private void init() {
    getStyleClass().add("branch-node");
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
