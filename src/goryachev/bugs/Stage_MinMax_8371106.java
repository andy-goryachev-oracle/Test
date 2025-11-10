package goryachev.bugs;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HeaderBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Stage_MinMax_8371106 extends Application {
    private Stage testStage = new Stage();
    private Label widthLabel = new Label();
    private Label heightLabel = new Label();
    private Label minWidthLabel = new Label();
    private Label minHeightLabel = new Label();
    private Label maxWidthLabel = new Label();
    private Label maxHeightLabel = new Label();

    @Override
    public void start(Stage stage) {
        stage.initStyle(StageStyle.EXTENDED);
        stage.setMinWidth(600);
        stage.setMinHeight(300);
        stage.setMaxWidth(900);
        stage.setMaxHeight(700);

        Button smallerThanMinButton = new Button("Smaller than Minimum");
        smallerThanMinButton.setOnAction((_) -> {
            stage.setWidth(stage.getMinWidth() - 20);
            stage.setHeight(stage.getMinHeight() - 20);
        });

        Button largerThanMaxButton = new Button("Larger than Maximum");
        largerThanMaxButton.setOnAction((_) -> {
            stage.setWidth(stage.getMaxWidth() + 20);
            stage.setHeight(stage.getMaxHeight() + 20);
        });

        Button middleOfTheRoadButton = new Button("Between Min and Max");
        middleOfTheRoadButton.setOnAction((_) -> {
            stage.setWidth((stage.getMinWidth() + stage.getMaxWidth()) / 2.0);
            stage.setHeight((stage.getMinHeight() + stage.getMaxHeight()) / 2.0);
        });

        HBox buttons = new HBox(smallerThanMinButton, largerThanMaxButton, middleOfTheRoadButton);
        buttons.setSpacing(5);

        HeaderBar headerBar = new HeaderBar();
        headerBar.setCenter(buttons);

        var root = new BorderPane();
        root.setTop(headerBar);

        VBox vbox = new VBox(
            new Separator(Orientation.HORIZONTAL),
            new Label("Stage Properties:"),
            widthLabel,
            heightLabel,
            minWidthLabel,
            minHeightLabel,
            maxWidthLabel,
            maxHeightLabel);
        vbox.setSpacing(5);
        vbox.setFillWidth(true);
        root.setCenter(vbox);

        widthLabel.textProperty().bind(Bindings.format("Width: %.2f", stage.widthProperty()));
        heightLabel.textProperty().bind(Bindings.format("Height: %.2f", stage.heightProperty()));
        minWidthLabel.textProperty().bind(Bindings.format("Min Width: %.2f", stage.minWidthProperty()));
        minHeightLabel.textProperty().bind(Bindings.format("Min Height: %.2f", stage.minHeightProperty()));
        maxWidthLabel.textProperty().bind(Bindings.format("Max Width: %.2f", stage.maxWidthProperty()));
        maxHeightLabel.textProperty().bind(Bindings.format("Max Height: %.2f", stage.maxHeightProperty()));

        Scene scene = new Scene(root, 700, 500);
        stage.setTitle("Command Stage");
        stage.setScene(scene);
        stage.show();
    }
}