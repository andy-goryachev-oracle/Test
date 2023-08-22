package goryachev.bugs;

import java.io.File;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Bug: setFont() breaks selection fill color.
 * https://bugs.openjdk.org/browse/JDK-8314827
 */
public class TextArea_SelectionColor extends Application {

    private Label status;

    @Override
    public void start(Stage stage) throws Exception {
        TextArea textArea = new TextArea("Selection Text Color is Wrong if Scale != 100%");
        textArea.setWrapText(true);
        textArea.selectAll();

        // comment this out and it works just fine
        textArea.setFont(Font.getDefault().font(48));

        status = new Label();

        BorderPane bp = new BorderPane(textArea);
        bp.setBottom(status);

        Scene scene = new Scene(bp);
        stage.setScene(scene);
        stage.setWidth(700);
        stage.setHeight(400);
        stage.setTitle("TextArea: Selection Fill Color");
        stage.renderScaleXProperty().addListener((x) -> updateStatus(stage));
        stage.renderScaleYProperty().addListener((x) -> updateStatus(stage));
        updateStatus(stage);

        stage.show();

        textArea.requestFocus();
    }

    private void updateStatus(Stage s) {
        StringBuilder sb = new StringBuilder();
        sb.append("   FX:");
        sb.append(System.getProperty("javafx.runtime.version"));
        sb.append("  JDK:");
        sb.append(System.getProperty("java.version"));

        if (s.getRenderScaleX() == s.getRenderScaleY()) {
            sb.append("  scale=");
            sb.append(s.getRenderScaleX());
        } else {
            sb.append("  scaleX=");
            sb.append(s.getRenderScaleX());
            sb.append("  scaleY=");
            sb.append(s.getRenderScaleY());
        }

        sb.append("  LOC:");
        sb.append(new File("").getAbsolutePath());
        status.setText(sb.toString());
    }
}