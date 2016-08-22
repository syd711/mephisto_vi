package com.mavenbox.ui.projects;

import com.mavenbox.ui.UIControl;
import com.mavenbox.ui.util.TransitionQueue;
import com.mavenbox.ui.util.TransitionUtil;
import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;

import java.util.ArrayList;
import java.util.List;

/**
 * The main class of the project controller
 */
public class Navigation extends HBox implements RotaryEncoderControlled {
  private final static String CLS_SELECTED = "workspace-node--selected";

  public static final double ZOOM_FACTOR = 1.3;
  private HBox scroller;
  private TransitionQueue transitionQueue;
  private int index = 0;
  private List<WorkspaceNavigation> jobNodes = new ArrayList<>();
  private WorkspaceNavigation activeWorkspace;

  public Navigation() {
    double top = (Screen.getPrimary().getVisualBounds().getHeight()/2)-200;
    setPadding(new Insets(top, 0, 0, 0));
    setAlignment(Pos.CENTER);
    setId("root");
    init();
  }

  @Override
  public void rotateLeft() {
    if(index < Workspaces.getWorkspaces().size()-1) {
      index++;
      scroll(-WorkspaceNavigation.WIDTH);
    }
  }

  @Override
  public void rotateRight() {
    if(index > 0) {
      index--;
      scroll(WorkspaceNavigation.WIDTH);
    }
  }

  @Override
  public void push() {
    activeWorkspace.select(true);
    UIControl.getInstance().setRotaryEncoderControl(activeWorkspace);
  }

  // ------------------------------- UI setup -------------------------------------------------------

  private void scroll(int width) {
    List<Transition> transitions = new ArrayList<>();
    transitions.add(TransitionUtil.createScaler(activeWorkspace.getTitle(), 1.0));
    activeWorkspace.select(false);
    activeWorkspace.getStyleClass().remove(CLS_SELECTED);
    activeWorkspace = jobNodes.get(index);
    transitions.add(TransitionUtil.createScaler(activeWorkspace.getTitle(), ZOOM_FACTOR));
    transitions.add(TransitionUtil.createTranslateByXTransition(scroller, 200, width));
    transitionQueue.addTransition(new ParallelTransition(transitions.toArray(new Transition[transitions.size()])));
    transitionQueue.play();
  }

  private void init() {
    VBox verticalRoot = new VBox();
    StackPane rootStack = new StackPane();
    verticalRoot.getChildren().addAll(createTitle(), rootStack);
    rootStack.getChildren().addAll(createBackground());

    scroller = new HBox();
    transitionQueue = new TransitionQueue(scroller);

    HBox spacer = new HBox();
    spacer.setAlignment(Pos.BASELINE_CENTER);
    spacer.getStyleClass().addAll("job-node");
    double offset = (Workspaces.getWorkspaces().size() - 1) * WorkspaceNavigation.WIDTH;
    spacer.setMinWidth(offset);
    scroller.getChildren().add(spacer);


    for(Workspace workspace : Workspaces.getWorkspaces()) {
      jobNodes.add(new WorkspaceNavigation(this, workspace));
    }
    scroller.getChildren().addAll(jobNodes);
    rootStack.getChildren().addAll(scroller);

    getChildren().add(verticalRoot);

    activeWorkspace = jobNodes.get(0);
    TransitionUtil.createScaler(activeWorkspace.getTitle(), ZOOM_FACTOR).play();
  }

  private Node createBackground() {
    VBox background = new VBox();
    background.getChildren().addAll(createLogos(), createProjects());
    return background;
  }

  private Node createTitle() {
    HBox box = new HBox();
    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
    box.setMinWidth(primaryScreenBounds.getWidth());
    box.setAlignment(Pos.BASELINE_CENTER);
    box.getStyleClass().addAll("title-box");
    Label title = new Label("PROJECT CONTROL");
    title.getStyleClass().addAll("font-default", "title-font");
    box.getChildren().add(title);
    return box;
  }

  private Node createLogos() {
    HBox box = new HBox();
    box.setPadding(new Insets(5, 0, 5, 0));
    box.setMinHeight(66);
    box.setAlignment(Pos.BASELINE_CENTER);
    box.getStyleClass().addAll("logo-scroller");
    return box;
  }

  private Node createProjects() {
    HBox box = new HBox();
    box.setMinHeight(68);
    box.setAlignment(Pos.CENTER);
    box.getStyleClass().addAll("project-scroller");
    return box;
  }
}
