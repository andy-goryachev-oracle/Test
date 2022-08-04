package goryachev.apps;

import java.util.Locale;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Issue TableView throws NPE on sorting column if selectionModel property value is null.
 * 
 * https://bugs.openjdk.org/browse/JDK-8090060
 * 
 * To reproduce:

- run
- click header
- expected: data sorted
- actual: NPE thrown
 *
 * @author Jeanette Winzenburg, Berlin
 */
public class TreeTableSortNPEIfNullSelectionModel extends Application {
    private Parent getContent() {
        TreeItem<Locale> root = new TreeItem(null);
        
        // instantiate the table with null items
        TreeTableView<Locale> view = new TreeTableView<Locale>(root);
        
        {
            TreeTableColumn<Locale, String> c = new TreeTableColumn<>("Language");
            c.setCellValueFactory(new TreeItemPropertyValueFactory<>("displayLanguage"));
            view.getColumns().add(c);
        }
        {
            TreeTableColumn<Locale, String> c = new TreeTableColumn<>("ISO3Language");
            c.setCellValueFactory(new TreeItemPropertyValueFactory<>("ISO3Language"));
            view.getColumns().add(c);
        }

        view.setSelectionModel(null);
        // or add column to sort order immediately
        //view.getSortOrder().add(column);
        BorderPane parent = new BorderPane();
        parent.setCenter(view);
        parent.setPadding(new Insets(20));
        return parent;
    }
    
    @Override
    public void start(Stage s) throws Exception {
        Scene scene = new Scene(getContent());
        s.setTitle(getClass().getSimpleName() + " " + System.getProperty("java.version"));
        s.setWidth(600);
        s.setScene(scene);
        s.show();
    }

    public static void main(String[] args) {
        launch();
    }
}