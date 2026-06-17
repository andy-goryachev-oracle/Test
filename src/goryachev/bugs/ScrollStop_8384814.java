package goryachev.bugs;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

// https://bugs.openjdk.org/browse/JDK-8384814
public class ScrollStop_8384814 extends Application {
    private ListView<DataItem> dataListView;
    private ObservableList<DataItem> dataItems;

    public void initialize() {
        dataListView = new ListView<>();
        dataItems = FXCollections.observableArrayList();

        generateTestData();

        dataListView.setItems(dataItems);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initialize();

        var scene = new Scene(new StackPane(dataListView));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void generateTestData() {
        for (int i = 1; i <= 1000; i++) {
            DataItem item = new DataItem(i, "item " + i, "this is " + i);
            dataItems.add(item);
        }
    }

    public static class DataItem {
        private final IntegerProperty id;
        private final StringProperty name;
        private final StringProperty description;

        public DataItem(int id, String name, String description) {
            this.id = new SimpleIntegerProperty(id);
            this.name = new SimpleStringProperty(name);
            this.description = new SimpleStringProperty(description);
        }

        @Override
        public String toString() {
            return String.format("[ID:%d] %s - %s", id.get(), name.get(), description.get());
        }
    }
}