package goryachev.bugs;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class BrokenFocusAndKeyboardInput_8319317 extends Application {
    @Override
    public void start(final Stage stage) {
        final VBox textFieldBox1 = createInputBox();
        final VBox textFieldBox2 = createInputBox();

        final Button openNewDialogButton = new Button("Open NEW dialog (broken)");
        openNewDialogButton.setOnAction(evt -> {
            // Hide/destroy previous dialog
            if (openNewDialogButton.getUserData() instanceof Stage oldDialogStage) {
                oldDialogStage.hide();
                openNewDialogButton.setUserData(null);
            }

            final Stage newDialogStage = createDemoDialogStage();
            setContentAndShow(newDialogStage, textFieldBox1);
            openNewDialogButton.setUserData(newDialogStage);
        });

        final Button openExistingDialogButton = new Button("(Re-)Open EXISTING dialog (ok)");
        final Stage reusableDialog = createDemoDialogStage();
        openExistingDialogButton.setOnAction(evt -> {
            setContentAndShow(reusableDialog, textFieldBox2);
        });

        final HBox root = new HBox(openNewDialogButton, openExistingDialogButton);
        final Scene scene = new Scene(root, 500, 200);
        stage.setScene(scene);

        Platform.setImplicitExit(true);
        stage.setTitle("BrokenFocusAndKeyboardInputWhenTextFieldsReusedInNewDialogsDemoApp");
        stage.show();
    }

    private static void setContentAndShow(final Stage dialogStage, final Node content) {
        if (dialogStage.getScene().getRoot() instanceof Pane rootPane) {
            rootPane.getChildren().setAll(content);
        }
        dialogStage.show();
    }

    private static VBox createInputBox() {
        final ToggleGroup toggleGroup = new ToggleGroup();
        final VBox textFieldBox = new VBox(new TextField("a"), new TextField("b"), new TextField("c"),
            createRadio("d", toggleGroup), createRadio("e", toggleGroup), createRadio("f", toggleGroup));
        textFieldBox.getChildren()
            .forEach(n -> n.addEventHandler(Event.ANY, event -> System.out.println("Event: " + event)));
        return textFieldBox;
    }

    private static RadioButton createRadio(final String text, final ToggleGroup toggleGroup) {
        final RadioButton rb = new RadioButton(text);
        rb.setToggleGroup(toggleGroup);
        return rb;
    }

    private static Stage createDemoDialogStage() {
        final Stage dialogWindow = new Stage(StageStyle.UTILITY);
        dialogWindow.setTitle("EventFilterCreepDemoApp - Dialog");
        final Pane dialogRoot = new StackPane();
        dialogWindow.setScene(new Scene(dialogRoot));

        // Uncomment for WORKAROUND:
        // dialogWindow.showingProperty().addListener((obs, oldIsShowing, newIsShowing) -> {
        // // Request focus on hiding the Stage (no idea WHY it helps, but it does??)
        // if (!newIsShowing) {
        // dialogRoot.requestFocus();
        // }
        // });
        // END WORKAROUND

        return dialogWindow;
    }
}