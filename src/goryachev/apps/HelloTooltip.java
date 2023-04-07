package goryachev.apps;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * JDK-8296387 [Tooltip, CSS] -fx-show-delay is only applied to the first tooltip that is shown before it is displayed
 */
public class HelloTooltip extends Application {

    @Override
    public void start(Stage primaryStage) {
        Button btn1 = new Button("Button 1");
        btn1.setOnMouseEntered(e -> System.out.println("Tooltip 1 delay: " + btn1.getTooltip().getShowDelay()));
        Tooltip tooltip1 = new Tooltip("Tooltip 1");
        tooltip1.setOnShown(e -> System.out.println("Tooltip 1 shown"));
        btn1.setTooltip(tooltip1);

        Button btn2 = new Button("Button 2");
        btn2.setOnMouseEntered(e -> System.out.println("Tooltip 2 delay: " + btn2.getTooltip().getShowDelay()));
        Tooltip tooltip2 = new Tooltip("Tooltip 2");
        tooltip2.setOnShowing(e -> System.out.println("Tooltip 2 shown"));
        btn2.setTooltip(tooltip2);

        Scene scene = new Scene(new HBox(10, btn1, btn2), 300, 250);
        scene.getStylesheets().add(HelloTooltip.class.getResource("HelloTooltip.css").toExternalForm());
        primaryStage.setScene(scene);

        primaryStage.show();
    }
}
