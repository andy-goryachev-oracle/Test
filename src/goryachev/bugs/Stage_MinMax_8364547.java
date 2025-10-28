package goryachev.bugs;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Stage_MinMax_8364547 extends Application {
    private Label widthLabel = new Label();
    private Label heightLabel = new Label();
    private Label minWidthLabel = new Label();
    private Label minHeightLabel = new Label();
    private Label maxWidthLabel = new Label();
    private Label maxHeightLabel = new Label();

    @Override
    public void start(Stage stage) {
        stage.initStyle(StageStyle.DECORATED);
        stage.setMinWidth(300);
        stage.setMinHeight(400);
        stage.setMaxWidth(800);
        stage.setMaxHeight(700);

        Button smallerThanMinButton = new Button("Smaller than Minimum");
        smallerThanMinButton.setOnAction((_) -> {
            stage.setWidth(stage.getMinWidth() - 10);
            stage.setHeight(stage.getMinHeight() - 10);
        });

        Button largerThanMaxButton = new Button("Larger than Maximum");
        largerThanMaxButton.setOnAction((_) -> {
            stage.setWidth(stage.getMaxWidth() + 10);
            stage.setHeight(stage.getMaxHeight() + 10);
        });

        Button middleOfTheRoadButton = new Button("Between Min and Max");
        middleOfTheRoadButton.setOnAction((_) -> {
            stage.setWidth((stage.getMinWidth() + stage.getMaxWidth()) / 2.0);
            stage.setHeight((stage.getMinHeight() + stage.getMaxHeight()) / 2.0);
        });

        FlowPane commandPane = new FlowPane(
            smallerThanMinButton,
            largerThanMaxButton,
            middleOfTheRoadButton
        );
        commandPane.setHgap(5);
        commandPane.setVgap(5);

        VBox root = new VBox(
            commandPane,
            new Separator(Orientation.HORIZONTAL),
            new Label("Stage Properties:"),
            widthLabel, heightLabel,
            minWidthLabel, minHeightLabel,
            maxWidthLabel, maxHeightLabel
        );
        root.setSpacing(5);
        root.setFillWidth(true);

        widthLabel.textProperty().bind(Bindings.format("Width: %.2f", stage.widthProperty()));
        heightLabel.textProperty().bind(Bindings.format("Height: %.2f", stage.heightProperty()));
        minWidthLabel.textProperty().bind(Bindings.format("Min Width: %.2f", stage.minWidthProperty()));
        minHeightLabel.textProperty().bind(Bindings.format("Min Height: %.2f", stage.minHeightProperty()));
        maxWidthLabel.textProperty().bind(Bindings.format("Max Width: %.2f", stage.maxWidthProperty()));
        maxHeightLabel.textProperty().bind(Bindings.format("Max Height: %.2f", stage.maxHeightProperty()));

        Scene scene = new Scene(root);
        stage.setTitle("Stage_MinMax_8364547");
        stage.setScene(scene);
        stage.show();
    }
}