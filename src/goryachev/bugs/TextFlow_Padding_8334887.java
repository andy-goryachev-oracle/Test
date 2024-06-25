package goryachev.bugs;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8334887
 */
public class TextFlow_Padding_8334887 extends Application {

    private StackPane rootPane;

    @Override
    public void start(Stage stage) {
        rootPane = new StackPane();
        rootPane.setId("rootPane");
        rootPane.setPadding(new Insets(40));
        rootPane.getStyleClass().add("root");

        Scene scene = new Scene(rootPane);
        stage.setTitle("TestFlow Padding Test JDK-8334887");
        stage.setScene(scene);
        stage.setWidth(700);
        stage.setHeight(400);
        stage.show();
        stage.toFront();
        stage.centerOnScreen();
        testPaddingContainer();
    }

    public void testPaddingContainer() {
        System.out.println("Testing padding on container...");

        TextFlow flowEN = new TextFlow(
            new Text("We have made changes to RI keyboard shortcuts to ensure they "
                + "are easier to discover, memorize and don't conflict with "
                + "those of other programs, actions within RI, or potential "
                + "new shortcuts added in the future.\n\n"
                + "If you are used to old shortcuts, we suggest reviewing "
                + "the new list, which can be found under "
                + "\"Help -> Keyboard shortcuts\"."));
        flowEN.setStyle("-fx-border-color: red; -fx-border-width: 1px;");

        BorderPane containerEN = new BorderPane(flowEN);
        containerEN.setPadding(new Insets(16));
        initContainer(containerEN);

        Platform.runLater(() -> {
            StackPane.setAlignment(containerEN, Pos.CENTER_LEFT);
            rootPane.getChildren().add(containerEN);
        });

        TextFlow flowES = new TextFlow(
            new Text("Hemos efectuado cambios en los atajos de teclado de RI para garantizar "
                + "que son más sencillos de descubrir y memorizar, y que no entren en "
                + "conflicto con los de otros programas, acciones dentro de RI o nuevos "
                + "atajos potenciales que se añadan en el futuro.\n\n"
                + "Si se ha acostumbrado a los anteriores atajos, le sugerimos que revise "
                + "la nueva lista, la cual podrá encontrar en \"Ayuda -> Atajos de teclado\"."));
        flowES.setStyle("-fx-border-color: red; -fx-border-width: 1px;");

        BorderPane containerES = new BorderPane(flowES);
        containerES.setPadding(new Insets(16));
        initContainer(containerES);

        Platform.runLater(() -> {
            StackPane.setAlignment(containerES, Pos.CENTER_RIGHT);
            rootPane.getChildren().add(containerES);
        });
    }

    private void initContainer(BorderPane container) {
        container.setMinWidth(16 + 80 + 16);
        container.setMaxWidth(16 + 240 + 16);
        container.setPrefHeight(Region.USE_COMPUTED_SIZE);
        container.setMaxHeight(Region.USE_PREF_SIZE);
        container.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
    }
}