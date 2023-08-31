package goryachev.bugs;

import java.io.File;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Bug: scroll pane should update its preferred size when a scroll bar appears.
 * https://bugs.openjdk.org/browse/JDK-8315419
 */
public class ScrollPane_Pref extends Application {

//    public static void main(String[] args) {
//        launch(args);
//    }

    private Label status;

    @Override
    public void start(Stage stage) throws Exception {
        Label t = new Label("400 x 400");
        t.setAlignment(Pos.CENTER);
        t.setBorder(Border.stroke(Color.RED));
        t.setPrefSize(400, 400);

        ScrollPane sp = new ScrollPane(t);
        sp.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        sp.setHbarPolicy(ScrollBarPolicy.NEVER);

        status = new Label();

        BorderPane bp = new BorderPane();
        bp.setRight(sp);
        bp.setBottom(status);

        Scene scene = new Scene(bp);
        stage.setScene(scene);
        stage.setWidth(700);
        stage.setHeight(500);
        stage.setTitle("ScrollPane: Preferred Size");
        stage.renderScaleXProperty().addListener((x) -> updateStatus(stage));
        stage.renderScaleYProperty().addListener((x) -> updateStatus(stage));
        updateStatus(stage);

        stage.show();
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