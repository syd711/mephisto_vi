package com.mavenbox.ui.monitoring;

import com.mavenbox.ui.UIControl;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Node that shows all pipelines
 */
public class PipelinesNode extends VBox {

  public PipelinesNode() {
    super(5);

    List<Pipeline> pipelines = UIControl.getInstance().getMonitoringService().getPipelines();
    for(Pipeline pipeline : pipelines) {
      getChildren().add(new PipelineNode(pipeline));
    }
  }
}
