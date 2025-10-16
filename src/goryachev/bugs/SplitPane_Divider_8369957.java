package goryachev.bugs;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/// [https://bugs.openjdk.org/browse/JDK-8369957]
public class SplitPane_Divider_8369957 extends Application {

    @Override
    public void start(Stage stage) {
        final var contentVBox = new VBox(8);
        contentVBox.widthProperty().addListener((_, _, v) -> {
            System.out.println("new vBox width: " + v);
        });
        contentVBox.getChildren().addAll(IntStream.rangeClosed(1, 6).mapToObj(i -> new CheckBox("Item " + i)).toList());

        final var scrollPane = new ScrollPane(contentVBox);
        scrollPane.setFitToWidth(false);
        scrollPane.setStyle("-fx-background: white;");
        scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        scrollPane.minWidthProperty().bind(contentVBox.widthProperty());
        scrollPane.maxWidthProperty().bind(contentVBox.widthProperty());

        final var splitPane = new SplitPane(new Region(), scrollPane);
        splitPane.setStyle("-fx-background-color: gainsboro;");

        final var scene = new Scene(splitPane, 800, 500);
        stage.setScene(scene);
        stage.show();

        final var scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.schedule(() -> {
            Platform.runLater(() -> {
                contentVBox.getChildren().add(new CheckBox("CheckBoxWithLongItemName"));
                
                // doesn't change anything
                splitPane.requestLayout();
                splitPane.layout();
            });

            scheduledExecutor.shutdown();
        }, 5, TimeUnit.SECONDS);
    }
}