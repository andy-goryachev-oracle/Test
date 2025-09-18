package goryachev.bugs;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8367322
 */
public class SplitPane_Dividers_8367322 extends Application {

    private final SplitPane splitPane = new SplitPane();

    @Override
    public void start(Stage primaryStage) {
        splitPane.getItems().add(createItem(0));
        splitPane.setOrientation(Orientation.VERTICAL);
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        var fooPane = new Pane(new Label("Foo pane"));
        VBox.setVgrow(fooPane, Priority.ALWAYS);

        var button = new Button("Test");
        button.setOnAction(e -> {
            splitPane.getItems().add(createItem(1));
            splitPane.setDividerPositions(new double[] { 0.5 });
            printDividerPositions("Current pulse: ");
            //Platform.runLater(() -> printDividerPositions("2nd pulse: "));
            //Platform.runLater(() -> Platform.runLater(() -> printDividerPositions("3rd pulse: ")));
            
            class Delayed implements Runnable {
                private final AtomicInteger count = new AtomicInteger(1);
                private final int max;
                
                public Delayed(int max) {
                    this.max = max;
                }
                
                @Override
                public void run() {
                    int ct = count.getAndIncrement();
                    if(ct < max) {
                        printDividerPositions("Pulse " + ct + ": ");
                        Platform.runLater(this);
                    }
                }
            }
            
            AtomicInteger count = new AtomicInteger(1);
            Platform.runLater(new Delayed(10));
        });

        var root = new VBox(splitPane, fooPane, button);
        Scene scene = new Scene(root, 500, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void printDividerPositions(String pulseInfo) {
        System.out.println(pulseInfo + Arrays.toString(splitPane.getDividerPositions()));
    }

    private Pane createItem(int index) {
        var pane = new Pane(new Label("Item : " + index));
        if (index == 0) {
            SplitPane.setResizableWithParent(pane, true);
        } else {
            SplitPane.setResizableWithParent(pane, false);
        }
        return pane;
    }
}