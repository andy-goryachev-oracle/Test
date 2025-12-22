package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/// https://bugs.openjdk.org/browse/JDK-8374214
public class ToolBar_OverflowButton_8374214 extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        ComboBox<String> cbox = new ComboBox<>();
        cbox.getItems().add("Lalalalalalalalalalalalalalalalalalalalalalalalalalala");
        
        // BUG: messes up the toolbar overflow button logic 
        cbox.setMaxWidth(100);
        // this code works correctly
        //cbox.setPrefWidth(100);

        ToolBar tb = new ToolBar();
        tb.getItems().addAll(
            cbox,
            new Button("1")
        );
        
        BorderPane bp = new BorderPane();
        bp.setTop(tb);

        stage.setScene(new Scene(bp, 200, 200));
        stage.show();
    }
}