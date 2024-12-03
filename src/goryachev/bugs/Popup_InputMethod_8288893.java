package goryachev.bugs;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Popup_InputMethod_8288893 extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("Popup Test");

        Button popupButton = new Button("Popup");
        Button popupControlButton = new Button("PopupControl");
        Button stageButton = new Button("Stage (Workaround)");
        CheckBox focusTraversable = new CheckBox("focusTraversable");

        focusTraversable.setSelected(true);

        popupButton.focusTraversableProperty().bind(focusTraversable.selectedProperty());
        popupControlButton.focusTraversableProperty().bind(focusTraversable.selectedProperty());
        stageButton.focusTraversableProperty().bind(focusTraversable.selectedProperty());

        Popup popup = createPopup();
        PopupControl popupControl = createPopupControl();
        Stage stage2 = createStage();
        stage2.initOwner(stage);

        popupButton.setOnAction(e -> popup.show(stage));
        popupControlButton.setOnAction(e -> popupControl.show(stage));
        stageButton.setOnAction(e -> stage2.show());

        ToolBar toolBar = new ToolBar(popupButton, popupControlButton, stageButton, focusTraversable);
        BorderPane root = new BorderPane(createContent());
        root.setTop(toolBar);
        Scene scene = new Scene(root, 450, 360);
        stage.setScene(scene);

        stage.show();
    }

    private Popup createPopup() {
        Popup popup = new Popup();
        popup.setAutoHide(true);
        VBox content = createContent();
        popup.getContent().add(content);
        return popup;
    }

    private PopupControl createPopupControl() {
        PopupControl popup = new PopupControl();
        popup.setAutoHide(true);
        VBox content = createContent();
        popup.getScene().setRoot(content);
        return popup;
    }

    private Stage createStage() {
        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.focusedProperty().addListener((ob, oldValue, newValue) -> {
            if (!newValue.booleanValue()) {
                stage.hide();
            }
        });
        stage.addEventFilter(KeyEvent.KEY_PRESSED, (e) -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                stage.hide();
            }
        });
        VBox content = createContent();
        BorderPane root = new BorderPane(content);
        root.setBackground(Background.EMPTY);
        root.setPadding(new Insets(6, 10, 10, 10));
        Scene scene = new Scene(root, Color.TRANSPARENT);
        stage.setScene(scene);

        // To stabilize the state of the IME
        // we need to takeaway the focus of the text input control.
        stage.setOnHidden(e -> root.requestFocus());
        stage.setOnShown((e) -> content.lookup(".text-field").requestFocus());

        return stage;
    }

    private VBox createContent() {
        TextField textField = new TextField();
        TextArea textArea = new TextArea();

        textField.addEventFilter(InputMethodEvent.ANY, e -> {
            System.out.println(e);
        });
        textArea.addEventFilter(InputMethodEvent.ANY, e -> {
            System.out.println(e);
        });

        VBox content = new VBox(16, new Label("Enter text using InputMethod"), textField, textArea);
        content.setPrefSize(300, 200);
        content.setPadding(new Insets(10));
        content.setStyle("-fx-background-color:#fefefe; -fx-effect:dropshadow(gaussian,rgba(0,0,0,0.2),12,0,0,4)");
        return content;
    }
}