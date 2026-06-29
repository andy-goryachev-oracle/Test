package goryachev.bugs;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/// https://bugs.openjdk.org/browse/JDK-8386590
public class GridPane_Gap_8386590 extends Application {

    private static final String ITEM_TEXT_PREFIX = "Sample Item ";
    private final Spinner<Integer> hGapSpinner = new Spinner<>(0, 64, 32);
    private final Label bugExpectedLbl = new Label();

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));

        root.getChildren().addAll(
            new Label("If (hGap > 0), then the height of the FlowPane" + System.lineSeparator()
                + "will grow too soon, when reducing the width of the window;" + System.lineSeparator()
                + "A variant of this was fixed in JDK-8092379." + System.lineSeparator()
                + "But when Span has content on every cell - it's still not working."),
            createToolBox(),
            new Label("FlowPane (dotted blue) inside GridPane (dashed red):"),
            createGridPane(createFlowPane()));

        Scene scene = new Scene(root, 600, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Node createToolBox() {

        hGapSpinner.setMinWidth(64);
        hGapSpinner.setMaxWidth(64);

        HBox toolsBox = new HBox(20);
        toolsBox.setAlignment(Pos.CENTER_LEFT);
        toolsBox.getChildren().addAll(new VBox(new Label("hGap"), hGapSpinner), bugExpectedLbl);
        return toolsBox;
    }

    private Node createGridPane(Node innerContent) {
        GridPane result = new GridPane();
        //result.getChildren().add( innerContent );
        result.setStyle("-fx-border-width: 1px; -fx-border-color: red; -fx-border-style: segments(0.5em, 0.5em);");

        result.add(new Label("a"), 0, 0);
        result.add(new Label("b"), 1, 0);
        result.add(innerContent, 2, 1);

        result.setHgap(16);
        result.hgapProperty().bind(hGapSpinner.valueProperty());

        GridPane.setConstraints(innerContent, 0, 0, 2, 1);
        //colSpanSpinner.valueProperty().addListener( ( obs, ol, ne ) -> {
        //    GridPane.setConstraints( innerContent, 0, 0, ne, 1 );
        //} );
        return result;
    }

    private static Node createFlowPane() {
        FlowPane fc = new FlowPane();
        fc.getChildren().addAll(
            new Label(ITEM_TEXT_PREFIX + "1"),
            new Label(ITEM_TEXT_PREFIX + "2"),
            new Label(ITEM_TEXT_PREFIX + "3"));
        fc.setStyle("-fx-border-color: blue; -fx-border-style: dotted; -fx-hgap: 0; -fx-vgap: 0;");
        return fc;
    }
}