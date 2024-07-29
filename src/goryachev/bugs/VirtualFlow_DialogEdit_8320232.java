package goryachev.bugs;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.DefaultStringConverter;

/**
 * https://bugs.openjdk.org/browse/JDK-8320232
 */
public class VirtualFlow_DialogEdit_8320232 extends Application {
    @Override
    public void start(Stage primaryStage) throws InterruptedException {
        primaryStage.setScene(new Scene(new StackPane()));
        Dialog<Void> dialog = new Dialog<>();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.APPLY);

        TableView<String> content = new TableView<>();
        content.setItems(FXCollections.observableArrayList("1", "2"));
        content.setEditable(true);

        TableColumn<String, String> col = new TableColumn<>("abc");
        col.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue()));
        col.setCellFactory(a -> new TextFieldTableCell<>(new DefaultStringConverter()) {
            @Override
            public void startEdit() {
                super.startEdit();
                System.out.println("startEdit called for index " + getIndex());
            }
        });
        content.getColumns().add(col);

        dialog.getDialogPane().setContent(content);
        dialog.show();
    }
}