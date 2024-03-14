package goryachev.bugs;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8328167
 */
public class ListView_ScrollTo_8328167 extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        ListView listView = new ListView();

        Scene scene = new Scene(listView, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();

        listView.getItems().clear();
        listView.getItems().add("Item 1\nItem 2\nItem 3\nItem 4");
        listView.getItems().add("Item 1\nItem 2\nItem 3\nItem 4");
        listView.getItems().add("Item 1\nItem 2\nItem 3\nItem 4");
        listView.getItems().add("Item 1\nItem 2\nItem 3\nItem 4");
        listView.getItems().add("Item 1\nItem 2\nItem 3\nItem 4");
        listView.getItems().add("Item 1\nItem 2\nItem 3\nItem 4");
        listView.getItems().add("Item 1\nItem 2\nItem 3\nItem 4");
        listView.getItems().add("Item 1\nItem 2\nItem 3\nItem 4");
        listView.getItems().add("Item 1\nItem 2\nItem 3\nItem 4");

        listView.scrollTo(listView.getItems().size() - 1);
    }

//    public static void main(String[] args) {
//        Application.launch(args);
//    }
}