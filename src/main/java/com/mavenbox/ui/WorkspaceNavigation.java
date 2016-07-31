package com.mavenbox.ui;

import com.mavenbox.model.Branch;
import com.mavenbox.model.Workspace;
import com.mavenbox.ui.util.TransitionUtil;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents on node in the scroller
 */
public class WorkspaceNavigation extends VBox implements RotaryEncoderControlled {
  public static final int WIDTH = 350;

  private Label title;
  private Workspace workspace;
  private VBox branchesBox;
  private BranchNavigation selectedBranch;
  private List<BranchNavigation> branchNavigations = new ArrayList<>();
  private Navigation navigation;
  private int index = 0;

  public WorkspaceNavigation(Navigation navigation, Workspace workspace) {
    this.workspace = workspace;
    this.navigation = navigation;

    setMinWidth(WIDTH);
    setMaxWidth(WIDTH);
    setAlignment(Pos.TOP_CENTER);
    getStyleClass().addAll("workspace-node");
    title = new Label(workspace.getName());
    title.getStyleClass().addAll("font-default", "workspace-font");
    ImageView cover = new ImageView(new Image(ResourceLoader.getResource("gitcat.png"), 50, 50, false, true));
    getChildren().addAll(cover, title);

    branchesBox = new VBox();
    setMinWidth(WIDTH);
    setMaxWidth(WIDTH);
    getChildren().add(branchesBox);
  }

  //---------------------- rotary control ---------------------------------


  @Override
  public void rotateLeft() {
    this.selectedBranch.select(false);
    if(index < branchNavigations.size()-1) {
      index++;
      this.selectedBranch = branchNavigations.get(index);
      this.selectedBranch.select(true);
    }
    else {
      this.select(false);
      UIControl.getInstance().setRotaryEncoderControl(navigation);
    }
  }

  @Override
  public void rotateRight() {
    this.selectedBranch.select(false);
    if(index > 0) {
      index--;
      this.selectedBranch = branchNavigations.get(index);
      this.selectedBranch.select(true);
    }
    else {
      this.select(false);
      UIControl.getInstance().setRotaryEncoderControl(navigation);
    }
  }

  @Override
  public void push() {
    FadeTransition blink = TransitionUtil.createBlink(selectedBranch);
    blink.setOnFinished(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        select(false);
        UIControl.getInstance().showProjects(false);
      }
    });
    blink.play();
  }

  //----------------------- UI -----------------------------------------------


  public void select(boolean select) {
    if(select) {
      List<Branch> branches = workspace.getBranches();
      for(Branch branch : branches) {
        BranchNavigation branchNavigation = new BranchNavigation(branch);
        branchNavigations.add(branchNavigation);
      }
      branchesBox.getChildren().addAll(branchNavigations);
      this.selectedBranch = branchNavigations.get(index);
      this.selectedBranch.select(true);
      TransitionUtil.createInFader(branchesBox).play();
    }
    else {
      index = 0;
      branchesBox.getChildren().removeAll(branchNavigations);
      branchNavigations.clear();
      UIControl.getInstance().setRotaryEncoderControl(navigation);
    }
  }

  public Label getTitle() {
    return title;
  }
}
