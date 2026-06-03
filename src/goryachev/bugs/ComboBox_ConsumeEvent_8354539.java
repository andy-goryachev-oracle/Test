package goryachev.bugs;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// https://bugs.openjdk.org/browse/JDK-8354539
public class ComboBox_ConsumeEvent_8354539 extends Application {

    @Override
    public void start(Stage stage) {
        Label label = new Label("Use Cmd+W to toggle");
        CheckBox checkBox = new CheckBox("Debug toggle");
        checkBox.setDisable(true);
        VBox feedback = new VBox(label, checkBox);
        feedback.setSpacing(10);

        Menu menu = new Menu("Test");
        MenuItem mi = new MenuItem("Toggle CheckBox");
        mi.setOnAction(e -> {
            checkBox.setSelected(!checkBox.isSelected());
        });
        mi.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN));
        menu.getItems().add(mi);

        MenuBar menuBar = new MenuBar(menu);
        menuBar.setUseSystemMenuBar(true);
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10, 10, 10, 10));
        borderPane.setTop(feedback);
        borderPane.setCenter(createControls());
        borderPane.setBottom(menuBar);

        Scene mainScene = new Scene(borderPane, 200, 200);
        stage.setScene(mainScene);
        stage.show();
    }

    public VBox createControls() {
        TextField textField = new TextField();
        textField.setEditable(true);
        ObservableList<String> list = FXCollections.observableArrayList();
        list.addAll("word", "sentence", "paragraph");
        ComboBox<String> comboBox = new ComboBox<>(list);
        comboBox.setEditable(true);
        DatePicker datePicker = new DatePicker();
        Spinner spinner = new Spinner<Integer>(0, 100, 5);
        spinner.setEditable(true);
        VBox vbox = new VBox(textField, comboBox, datePicker, spinner);
        return vbox;
    }
}