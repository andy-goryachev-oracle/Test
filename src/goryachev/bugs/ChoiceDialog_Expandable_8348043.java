package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8348043
 */
public class ChoiceDialog_Expandable_8348043 extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Button showDialog = new Button("Show Dialog");

        showDialog.setOnAction(e -> {
            var cd = getChoiceDialog();
            cd.showAndWait();
        });

        Scene scene = new Scene(new FlowPane(showDialog), 200, 100);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private ChoiceDialog<String> getChoiceDialog() {
        ChoiceDialog<String> cd = new ChoiceDialog<String>(null, "One", "Two", "Three");

        // @formatter:off
        StringBuffer buf = new StringBuffer()
        .append("Some new text that wraps around the textarea and demonstrates the expandable bug ")
        .append("Some new text that wraps around the textarea and demonstrates the expandable bug ")
        .append("Some new text that wraps around the textarea and demonstrates the expandable bug ")
        .append("Some new text that wraps around the textarea and demonstrates the expandable bug ")
        .append("Some new text that wraps around the textarea and demonstrates the expandable bug ")
        .append("Some new text that wraps around the textarea and demonstrates the expandable bug ");
        // @formatter:on

        TextArea ta = new TextArea();
        ta.setText(buf.toString());
        ta.setEditable(false);
        ta.setWrapText(true);

        // Initially the dialog works as expected when
        // the expandable content is expanded/hidden.
        // The dialog expands horizontally and vertically
        // when expanded and shrinks in both axis' when hidden.
        // Once the combo box has been clicked, after
        // expanding/hiding the dialog remains at the
        // expanded width.
        cd.getDialogPane().setExpandableContent(ta);

        return cd;
    }
}