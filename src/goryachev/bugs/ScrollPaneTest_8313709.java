package goryachev.bugs;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// https://bugs.openjdk.org/browse/JDK-8313709
public class ScrollPaneTest_8313709 extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        final FlowPane pane = new FlowPane(10, 10,
                createNode("1"), createNode("2"), createNode("3"), createNode("4"));
        final BorderPane borderPane = new BorderPane(pane);
        final ScrollPane scrollPane = new ScrollPane(borderPane);

        // works
//        borderPane.setPadding(new Insets(1));
        // fails
        borderPane.setPadding(new Insets(2));

        VBox.setVgrow(borderPane, Priority.ALWAYS);

        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        final Scene scene = new Scene(scrollPane, 830, 675);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static Node createNode(String text) {
        final TextArea textArea = new TextArea(text);
        textArea.setPrefSize(400, 400);
        return textArea;
    }

}