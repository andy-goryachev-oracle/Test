package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

// https://bugs.openjdk.org/browse/JDK-8377153
public class FlowPane_MaxWidth_8377153 extends Application {
    @Override
    public void start(Stage stage) {
        FlowPane flowPane = new FlowPane();
        TextFlow tf1 = createTextFlow();
        TextFlow tf2 = createTextFlow();
        TextFlow tf3 = createTextFlow();
        flowPane.getChildren().addAll(tf1, tf2, tf3);
        Scene scene = new Scene(flowPane, 320, 240);
        stage.setScene(scene);
        stage.show();
    }

    private static TextFlow createTextFlow() {
        TextFlow textFlow = new TextFlow(
            new Text("this is a long text that will be wrapped"),
            new Text("this is a long text that will be wrapped"),
            new Text("this is a long text that will be wrapped")
        );
        textFlow.setStyle("-fx-background-color: red; -fx-max-width: 10em; -fx-wrap-text: true");
        return textFlow;
    }
}