package goryachev.research;

import java.time.Duration;
import java.time.Instant;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * https://bugs.openjdk.org/browse/JDK-8355990
 */
public class Stage_Playground_8355990 extends Application {

    private StageStyle testStageStyle = StageStyle.DECORATED;
    private Stage testStage;
    private final TextArea logArea = new TextArea();
    private boolean expectingChanges;
    private Instant startTime;
    private final Label statusLabel = new Label();

    private void addToLog(String line) {
        logArea.appendText(line);
        logArea.appendText("\n");
    }

    private void beginChange(String label) {
        addToLog(label);
        expectingChanges = true;
        startTime = Instant.now();
    }

    private void endChange() {
        addToLog("Done in " + Duration.between(startTime, Instant.now()).toMillis() + "ms");
        expectingChanges = false;
        updateStatusLabel();
    }

    private void reportChange(String change) {
        if (expectingChanges) {
            addToLog("    " + change);
        } else {
            addToLog("!! Unexpected change: " + change);
            updateStatusLabel();
        }
    }

    private void updateStatusLabel() {
        StringBuilder sb = new StringBuilder();
        if (testStage == null) {
            sb.append("No test stage");
        } else if (!testStage.isShowing()) {
            sb.append("Test stage not visible");
        } else {
            if (testStage.isIconified()) {
                sb.append("iconified, ");
            }
            if (testStage.isMaximized()) {
                sb.append("maximized, ");
            }
            if (testStage.isFullScreen()) {
                sb.append("fullscreen, ");
            }
            sb.append("width is ").append(testStage.getWidth());
            sb.append(", x is ").append(testStage.getX());
        }
        statusLabel.setText(sb.toString());
    }

    private void buildTestStage(boolean doShow) {
        if (testStage != null) {
            return;
        }
        testStage = new Stage();
        testStage.initStyle(testStageStyle);
        var testScene = new Scene(new StackPane(new Label("Test Stage")), 600, 600);
        testStage.setScene(testScene);
        testStage.setTitle("Test Stage");
        testStage.setOnCloseRequest((ev) -> {
            testStage = null;
            updateStatusLabel();
        });

        testStage.widthProperty().addListener((_, _, v) -> {
            reportChange("Width is " + v);
        });
        testStage.xProperty().addListener((_, _, v) -> {
            reportChange("X is " + v);
        });
        testStage.fullScreenProperty().addListener((_, _, v) -> {
            reportChange("Fullscreen is " + v);
        });
        testStage.maximizedProperty().addListener((_, _, v) -> {
            reportChange("Maximized is " + v);
        });
        testStage.iconifiedProperty().addListener((_, _, v) -> {
            reportChange("Iconified is " + v);
        });

        if (doShow) {
            beginChange("Showing");
            testStage.show();
            endChange();
        }
    }

    private void buildTestStage() {
        buildTestStage(true);
    }

    private void destroyTestStage() {
        if (testStage == null) {
            return;
        }
        testStage.setFullScreen(false);
        testStage.hide();
        testStage = null;
    }

    private enum AllowedStyle {
        DECORATED(StageStyle.DECORATED, "Decorated"),
        UNDECORATED(StageStyle.UNDECORATED, "Undecorated"),
        TRANSPARENT(StageStyle.TRANSPARENT, "Transparent");

        private final StageStyle style;
        private final String title;

        private AllowedStyle(StageStyle s, String title) {
            this.style = s;
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }

        public StageStyle getStyle() {
            return style;
        }
    }

    @Override
    public void start(Stage stage) {
        buildTestStage(true);

        logArea.setEditable(false);

        Button maximizeButton = new Button("Maximize");
        maximizeButton.setOnAction((ev) -> {
            buildTestStage();
            String logEntry = "Turning maximized " + (testStage.isMaximized() ? "off" : "on");
            beginChange(logEntry);
            testStage.setMaximized(!testStage.isMaximized());
            endChange();
        });

        Button fullScreenButton = new Button("FullScreen");
        fullScreenButton.setOnAction((ev) -> {
            buildTestStage();
            String logEntry = "Turning fullscreen " + (testStage.isFullScreen() ? "off" : "on");
            beginChange(logEntry);
            testStage.setFullScreen(!testStage.isFullScreen());
            endChange();
        });

        Button iconifyButton = new Button("Iconify");
        iconifyButton.setOnAction((ev) -> {
            buildTestStage();
            String logEntry = "Turning iconified " + (testStage.isIconified() ? "off" : "on");
            beginChange(logEntry);
            testStage.setIconified(!testStage.isIconified());
            endChange();
        });

        Button smallerButton = new Button("Smaller");
        smallerButton.setOnAction((ev) -> {
            buildTestStage();
            beginChange("Making smaller");
            testStage.setWidth(testStage.getWidth() - 10);
            testStage.setHeight(testStage.getHeight() - 10);
            endChange();
        });

        Button shiftButton = new Button("Shift");
        shiftButton.setOnAction((ev) -> {
            buildTestStage();
            beginChange("Shifting");
            testStage.setX(testStage.getX() + 10);
            testStage.setY(testStage.getY() + 10);
            endChange();
        });

        ChoiceBox<AllowedStyle> styleChoiceBox = new ChoiceBox<>();
        styleChoiceBox.getItems().setAll(AllowedStyle.values());
        styleChoiceBox.setValue(AllowedStyle.DECORATED);
        styleChoiceBox.setOnAction((ev) -> {
            destroyTestStage();
            testStageStyle = styleChoiceBox.getValue().getStyle();
            buildTestStage();
        });

        Button clearButton = new Button("Clear");
        clearButton.setOnAction((ev) -> {
            logArea.setText("");
        });

        HBox testControls = new HBox(maximizeButton, fullScreenButton, iconifyButton, smallerButton, shiftButton);
        testControls.setSpacing(5);
        HBox additionalControls = new HBox(styleChoiceBox, clearButton);
        additionalControls.setAlignment(Pos.CENTER_LEFT);
        additionalControls.setSpacing(5);

        VBox root = new VBox(testControls, additionalControls, statusLabel, logArea);
        root.setPadding(new Insets(5));
        root.setSpacing(5);
        VBox.setVgrow(logArea, Priority.ALWAYS);

        Scene scene = new Scene(root, 400, 400);
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        stage.setTitle("Stage Playground");
        stage.setOnCloseRequest((ev) -> {
            destroyTestStage();
        });
        stage.show();

        Platform.runLater(maximizeButton::requestFocus);
    }
}