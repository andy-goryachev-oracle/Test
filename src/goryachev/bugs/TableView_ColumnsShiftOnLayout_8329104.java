package goryachev.bugs;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Table columns momentarily shift when the vertical scroll bar appears on pressing  [Pane 2] button.
 *
 * https://bugs.openjdk.org/browse/JDK-8329104
 * https://stackoverflow.com/questions/77369768/javafx-tableview-text-in-the-cells-of-the-columns-seems-to-jump
 */
public class TableView_ColumnsShiftOnLayout_8329104 extends Application {

    TableView<TestTable> tableViev;
    ObservableList<TestTable> observableListWithTests;

//    public static void main(String[] args) {
//        launch();
//    }

    @Override
    public void start(Stage stage) {
        this.tableViev = new TableView<>();
        this.observableListWithTests = FXCollections.observableArrayList();

        this.tableViev = new TableView<>();
        this.tableViev.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        //tableViev.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);

        TableColumn<TestTable, Integer> test1 = new TableColumn<>("Test 1");
        TableColumn<TestTable, String> test2 = new TableColumn<>("Test 2");
        TableColumn<TestTable, String> test3 = new TableColumn<>("Test 3");
        TableColumn<TestTable, String> test4 = new TableColumn<>("Test 4");
        test4.widthProperty().addListener((s,p,c) -> {
            System.out.println(c);
        });

        test1.setStyle("-fx-text-alignment: center; -fx-alignment: center;");
        test2.setStyle("-fx-text-alignment: center; -fx-alignment: center;");
        test3.setStyle("-fx-text-alignment: center; -fx-alignment: center;");
        test4.setStyle("-fx-text-alignment: center; -fx-alignment: center;");

        test1.setCellValueFactory(new PropertyValueFactory<>("test1"));
        test2.setCellValueFactory(new PropertyValueFactory<>("test2"));
        test3.setCellValueFactory(new PropertyValueFactory<>("test3"));
        test4.setCellValueFactory(new PropertyValueFactory<>("test3"));

        for (int x = 0; x < 50; x++) {
            observableListWithTests.add(new TestTable("Test " + x, "Test " + x, "Test " + x, "Test " + x));
        }

        tableViev.getColumns().addAll(test1, test2, test3, test4);
        tableViev.setItems(observableListWithTests);

        VBox centerPane1 = new VBox();
        centerPane1.setPadding(new Insets(20, 20, 20, 10));
        Text text1 = new Text("CenterPane 1");
        centerPane1.getChildren().add(text1);

        VBox centerPane2 = new VBox();
        centerPane2.setPadding(new Insets(20, 20, 20, 10));
        centerPane2.getChildren().add(tableViev);

        Button buttonPane1 = new Button("Pane 1");
        Button buttonPane2 = new Button("Pane 2");

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(centerPane1);

        buttonPane1.setOnAction(event -> borderPane.setCenter(centerPane1));
        buttonPane2.setOnAction(event -> borderPane.setCenter(centerPane2));

        VBox vBoxWithButtons = new VBox();
        vBoxWithButtons.setPadding(new Insets(20));
        vBoxWithButtons.setSpacing(10);
        vBoxWithButtons.getChildren().addAll(buttonPane1, buttonPane2);

        borderPane.setLeft(vBoxWithButtons);
        var scene = new Scene(borderPane, 640, 480);

        stage.setScene(scene);
        stage.show();
    }

    public static class TestTable {
        private String test1;
        private String test2;
        private String test3;
        private String test4;

        public TestTable(String test1, String test2, String test3, String test4) {
            this.test1 = test1;
            this.test2 = test2;
            this.test4 = test3;
            this.test3 = test4;
        }

        public String getTest1() {
            return this.test1;
        }

        public void setTest1(String test) {
            this.test1 = test;
        }

        public String getTest2() {
            return this.test2;
        }

        public void setTest2(String test) {
            this.test2 = test;
        }

        public String getTest4() {
            return this.test4;
        }

        public void setTest4(String test) {
            this.test4 = test;
        }

        public void setTest3(String test) {
            this.test3 = test;
        }

        public String getTest3() {
            return this.test3;
        }
    }
}