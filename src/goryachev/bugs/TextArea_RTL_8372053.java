package goryachev.bugs;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TextArea_RTL_8372053 extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create UI components
        Label titleLabel = new Label("RTL Text Navigation Demo");

        // Create RTL TextArea (simulating RichTextArea)
        TextArea rtlTextArea = new TextArea();
        rtlTextArea.setPromptText("RTL Text Area - Type Hebrew/Arabic text here...");
        rtlTextArea.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        rtlTextArea.setWrapText(true);

        // Set some sample RTL text (Hebrew)
        String hebrewText = "זוהי דוגמה לטקסט בעברית. הטקסט נכתב מימין לשמאל";
        rtlTextArea.setText(hebrewText);

        // Create labels for each text area
        Label rtlLabel = new Label("RTL Text Area (Hebrew/Arabic):");

        // Create output area to show cursor position and key presses
        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPromptText("Navigation events will be displayed here...");

        // Add event listeners to track navigation
        setupNavigationTracking(rtlTextArea, outputArea, "RTL");

        // Create layout
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.getChildren().addAll(titleLabel, new Separator(), rtlLabel, rtlTextArea,
            new Label("Navigation Events:"), outputArea);

        // Create scene and stage
        Scene scene = new Scene(root, 600, 700);
        primaryStage.setTitle("RTL Text Navigation Demo - JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Set focus to RTL area initially
        rtlTextArea.requestFocus();
    }

    private void setupNavigationTracking(TextArea textArea, TextArea outputArea, String type) {
        textArea.setOnKeyPressed(event -> {
            switch (event.getCode()) {
            case LEFT:
                logEvent(outputArea, type + ": LEFT arrow pressed");
                break;
            case RIGHT:
                logEvent(outputArea, type + ": RIGHT arrow pressed");
                break;
            case HOME:
                logEvent(outputArea, type + ": HOME pressed");
                break;
            case END:
                logEvent(outputArea, type + ": END pressed");
                break;
            }
        });

    }

    private void logEvent(TextArea outputArea, String message) {
        String timestamp = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        outputArea.appendText("[" + timestamp + "] " + message + "\n");

        // Auto-scroll to bottom
        outputArea.setScrollTop(Double.MAX_VALUE);
    }
}