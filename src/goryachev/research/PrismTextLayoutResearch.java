package goryachev.research;

import javafx.application.Application;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Investigating PrismTextLayout using TextArea. 
 */
public class PrismTextLayoutResearch extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setFont(Font.getDefault().font(24.0));
        textArea.setText("1\t2\n3");

        CheckBox rightToLeft = new CheckBox(); //"Right-to-Left");
        
        ToolBar tb = new ToolBar(rightToLeft);

        BorderPane bp = new BorderPane();
        bp.setTop(tb);
        bp.setCenter(textArea);
        
        Scene scene = new Scene(bp, 500, 300);
        
        rightToLeft.selectedProperty().addListener((s, p, c) -> {
            NodeOrientation ori = c ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT;
            scene.setNodeOrientation(ori);
        });

        stage.setScene(scene);
        stage.setTitle("PrismTextLayout Research");
        stage.show();
    }
}