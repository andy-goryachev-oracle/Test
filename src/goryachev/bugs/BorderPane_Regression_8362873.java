package goryachev.bugs;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8362873
 */
public class BorderPane_Regression_8362873 extends Application {

    @Override
    public void start(Stage stage) {
        boolean verticalLayout = !true;

        FlowPane flowPane = new FlowPane();
        flowPane.getStyleClass().add("main");
        flowPane.setOrientation(verticalLayout ? Orientation.HORIZONTAL : Orientation.VERTICAL);
        flowPane.getChildren().addAll(
            new Button("Minecraft"),
            new Button("Forge"),
            new Button("NeoForge"),
            new Button("OptiFine"),
            new Button("Fabric"),
            new Button("Fabric API"),
            new Button("Quilt"),
            new Button("SQL/QFAPI"));

        BorderPane borderPane = new BorderPane();

        if (verticalLayout) {
            borderPane.setTop(new Label("Instance Name"));
        } else {
            borderPane.setLeft(new Label("Instance Name"));
        }
        borderPane.setCenter(flowPane);

        if (verticalLayout) {
            borderPane.setBottom(new Button("Install"));
        } else {
            borderPane.setRight(new Button("Install"));
        }

        Scene scene = new Scene(borderPane, 600, 480);
        scene.getStylesheets().add(
            """
            data:text/css,
            .main .button {
              -fx-pref-width: 12em;
              -fx-pref-height: 12em;
            }

            .main {
              -fx-border-width: 2px;
              -fx-border-color: red;
            }
            """);

        stage.setScene(scene);
        stage.setTitle("BorderPane_Regression_8362873");
        stage.show();
    }
}
