package goryachev.bugs;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Button_MultipleDefault_8385779 extends Application {

    private VBox root;
    private int rowCounter = 0;

    @Override
    public void start(Stage stage) {
        root = new VBox(8);
        root.setPadding(new Insets(16));

        addRow(); // initial row

        Scene scene = new Scene(root, 480, 400);
        stage.setTitle("Default Button Bug — JavaFX Reproducer");
        stage.setScene(scene);
        stage.show();
    }

    private void addRow() {
        rowCounter++;
        int id = rowCounter;

        HBox row = new HBox(8);
        row.setPadding(new Insets(4));

        TextField textField = new TextField();
        textField.setPromptText("Row " + id + " — press Enter here");
        textField.setPrefWidth(240);

        Button addBtn = new Button("Add [" + id + "]");
        addBtn.setDefaultButton(true);
        addBtn.setOnAction(e -> {
            System.out.println("Add fired from row " + id);
            addRow();
        });

        Button removeBtn = new Button("Remove [" + id + "]");
        removeBtn.setCancelButton(false);
        removeBtn.setOnAction(e -> {
            System.out.println("Remove fired from row " + id);
            root.getChildren().remove(row);
        });

        row.getChildren().addAll(textField, addBtn, removeBtn);
        root.getChildren().add(row);
        textField.requestFocus();
    }
}