package goryachev.bugs;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class ComboBox_Size_8348782 extends Application {

    enum Test {
        FOO, BAR
    }

    @Override
    public void start(Stage stage) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));

        textFieldRow(gridPane, 0);
        textFieldRow(gridPane, 1);

        Label label1 = new Label("Label AAAAA BBBBB:");
        label1.setMinWidth(Region.USE_PREF_SIZE);
        var comboBox = new ComboBox<Test>();
        comboBox.setItems(FXCollections.observableArrayList(Test.values()));

        gridPane.add(label1, 0, 2);
        gridPane.add(comboBox, 1, 2);
        comboBox.setMaxWidth(Double.MAX_VALUE);

        Scene scene = new Scene(gridPane, 600, 200);
        stage.setScene(scene);
        stage.show();
    }

    private void textFieldRow(GridPane gridPane, int index) {
        Label label1 = new Label("Label AAAAA BBBBB:");
        label1.setMinWidth(Region.USE_PREF_SIZE);
        TextField textField1 = new TextField();
        GridPane.setHgrow(textField1, Priority.ALWAYS);
        textField1.setMinWidth(10);
        Label label2 = new Label("Label CCCCCC DDDDDD:");
        label2.setMinWidth(Region.USE_PREF_SIZE);
        TextField textField2 = new TextField();
        GridPane.setHgrow(textField2, Priority.ALWAYS);
        textField2.setMinWidth(10);

        gridPane.add(label1, 0, index);
        gridPane.add(textField1, 1, index);
        gridPane.add(label2, 2, index);
        gridPane.add(textField2, 3, index);
    }
}