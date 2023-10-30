package goryachev.bugs;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8187314
 */
public class TreeTableView_8187314 extends Application {

    TreeTablePosition<Dummy, String> editPosition;
    private Object editValue;

    @Override
    public void start(Stage primaryStage) {
        TreeTableView<Dummy> table = new TreeTableView<>();
        table.setRoot(new TreeItem<>());
        for (Dummy dummy: Dummy.dummies()) {
            TreeItem<Dummy> dummyItem = new TreeItem<>(dummy);
            table.getRoot().getChildren().add(dummyItem);
        }

        table.setEditable(true);

        {
            TreeTableColumn<Dummy, String> c = new TreeTableColumn<>("C1");
            table.getColumns().add(c);
            c.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
            c.setCellValueFactory(new TreeItemPropertyValueFactory<>("dummy1"));
            c.setOnEditStart(t -> editPosition = t.getTreeTablePosition());
            c.addEventHandler(TreeTableColumn.editCommitEvent(), t -> {
                editValue = t.getNewValue();
                System.out.println("doing nothing 1");
            });
        }
        {
            TreeTableColumn<Dummy, String> c = new TreeTableColumn<>("C2");
            table.getColumns().add(c);
            c.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
            c.setCellValueFactory(new TreeItemPropertyValueFactory<>("dummy2"));
            c.setOnEditStart(t -> editPosition = t.getTreeTablePosition());
            c.addEventHandler(TreeTableColumn.editCommitEvent(), t -> {
                editValue = t.getNewValue();
                System.out.println("doing nothing 2");
            });
        }
        {
            TreeTableColumn<Dummy, String> c = new TreeTableColumn<>("C3");
            table.getColumns().add(c);
            c.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
            c.setCellValueFactory(new TreeItemPropertyValueFactory<>("dummy3"));
            c.setOnEditStart(t -> editPosition = t.getTreeTablePosition());
            c.addEventHandler(TreeTableColumn.editCommitEvent(), t -> {
                editValue = t.getNewValue();
                System.out.println("doing nothing 3");
            });
        }

        Button button = new Button("Check value");
        button.setOnAction(e -> {
            if (editPosition == null) {
                return;
            }
            String value = editPosition.getTableColumn().getCellObservableValue(editPosition.getRow()).getValue();
            System.out.println("value in edited cell must represent backing data: " + value + " not the edited " + editValue);
        });
        BorderPane root = new BorderPane(table);
        root.setBottom(button);
        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static class Dummy {
        private String dummy1;
        private String dummy2;
        private String dummy3;

        public Dummy(String dummy) {
            this.dummy1 = dummy;
            this.dummy2 = dummy + ".";
            this.dummy3 = dummy + ";";
        }

        public String getDummy1() {
            return dummy1;
        }
        
        public String getDummy2() {
            return dummy2;
        }
        
        public String getDummy3() {
            return dummy3;
        }

        public static ObservableList<Dummy> dummies() {
            return FXCollections.observableArrayList(
                new Dummy("1"), new Dummy("2"), new Dummy("3"));
        }
    }
}