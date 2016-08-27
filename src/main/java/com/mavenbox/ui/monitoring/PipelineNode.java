package com.mavenbox.ui.monitoring;

import com.mavenbox.ui.ResourceLoader;
import com.mavenbox.ui.UIControl;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Panel for displaying one pipeline row
 */
public class PipelineNode extends HBox {

  public PipelineNode(Pipeline pipeline) {
    super(10);
    setAlignment(Pos.TOP_CENTER);
    setPadding(new Insets(6, 6, 6, 12));

    ImageView img = new ImageView(new Image(ResourceLoader.getResource(pipeline.getIndex() + ".png"), 35, 35, false, true));
    getChildren().add(img);

    VBox textbox = new VBox();
    Label pipelineName = new Label(pipeline.getName());
    pipelineName.setPadding(new Insets(0, 0, 0, 3));
    pipelineName.getStyleClass().add("pipeline-name");
    textbox.getChildren().add(pipelineName);
    pipelineName.setMinWidth(340);

    Hyperlink pipelineLink = new Hyperlink();
    pipelineLink.setText(pipeline.getLink());
    pipelineLink.getStyleClass().add("pipeline-link");
    textbox.getChildren().add(pipelineLink);
    pipelineLink.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        UIControl.getInstance().open(pipeline.getLink());
      }
    });

    Label pipelineHost = new Label(pipeline.getHost());
    pipelineHost.getStyleClass().add("pipeline-host");
    pipelineHost.setPadding(new Insets(0, 0, 0, 4));
    textbox.getChildren().add(pipelineHost);

    getChildren().add(textbox);

    if(pipeline.isStatus()) {
      img = new ImageView(new Image(ResourceLoader.getResource("verification24.png"), 35, 35, false, true));
      getChildren().add(img);
    }
    else {
      img = new ImageView(new Image(ResourceLoader.getResource("clear5.png"), 35, 35, false, true));
      getChildren().add(img);
    }
  }
}
