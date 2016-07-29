package com.mavenbox.ui;

import com.mavenbox.model.Workspace;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Represents on node in the scroller
 */
public class WorkspaceNavigation extends VBox {
  public static final int WIDTH = 350;

  private Label title;

  public WorkspaceNavigation(Workspace workspace) {
    setMinWidth(WIDTH);
    setAlignment(Pos.TOP_CENTER);
    getStyleClass().addAll("job-node");
    title = new Label(workspace.getName());
    title.getStyleClass().addAll("font-default", "job-font");
    ImageView cover = new ImageView(new Image(ResourceLoader.getResource("gitcat.png"), 50, 50, false, true));
    getChildren().addAll(cover, title);
  }

  public Label getTitle() {
    return title;
  }
}
