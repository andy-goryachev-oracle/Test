package goryachev.bugs;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

// https://bugs.openjdk.org/browse/JDK-8090267
public class JapaneseIMETest extends Application {
    static boolean useSwing = true;

    @Override
    public void start(Stage primaryStage) throws Exception {

        GridPane root = new GridPane();
        root.addRow(0, new TextField());
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        if (!useSwing)
            launch(args);
        else {

            final JFXPanel panel = new JFXPanel();
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setContentPane(panel);
            frame.setSize(100, 100);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    GridPane root = new GridPane();
                    root.addRow(0, new TextField());
                    Scene scene = new Scene(root);
                    panel.setScene(scene);
                }
            });
        }
    }
}