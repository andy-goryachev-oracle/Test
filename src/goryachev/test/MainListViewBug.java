package goryachev.test;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainListViewBug extends Application {

    @Override
    public void start(final Stage primaryStage) {

        ObservableList<Parent> testArrayList = FXCollections.observableArrayList();
        for (int i = 0; i < 300; i++) {

            HBox h = new HBox();
            Label l = new Label("Element " + i);
            h.getChildren().setAll(l);
            testArrayList.add(h);
            
            l.boundsInParentProperty().addListener((s,p,c) -> {
                System.out.println("x=" + c + " this=" + l.hashCode());
            });
        }
        ListView listView = new ListView(testArrayList);

        listView.setCellFactory(param -> new ListCell<HBox>() {
            @Override
            protected void updateItem(final HBox item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setGraphic(item);
                }
            }
        });

        VBox root = new VBox();
        root.getChildren().addAll(listView);

        Scene scene = new Scene(root, 450, 250);

        primaryStage.setTitle("Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

