package goryachev.bugs;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8352991
 */
public class TitledPane_Resizeable_8352991 extends Application {

    @Override
    public void start(Stage stage) {
        Label n = new Label("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        n.setBackground(Background.fill(Color.SALMON));
        n.setWrapText(true);
        
        TitledPane tp = new TitledPane("This Title Is Expected To Be Truncated, But It Ain't", n);

        tp.setAlignment(Pos.CENTER_RIGHT);
        //tp.setMinWidth(500);
        tp.setPrefWidth(100); // does not work
        tp.setMaxWidth(100); // does not work
        tp.setEllipsisString("...");

        Scene scene = new Scene(new BorderPane(tp), 400, 400);

        stage.setScene(scene);
        stage.setTitle(getClass().getSimpleName());
        stage.show();
    }
}