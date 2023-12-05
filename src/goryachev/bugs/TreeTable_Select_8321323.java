package goryachev.bugs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javafx.application.Application;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8321323
 */
public class TreeTable_Select_8321323 extends Application {
    private final TreeTableView<String> tree = new TreeTableView<>();
    private final ObjectProperty<Predicate<String>> filterPredicate = new SimpleObjectProperty<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        final VBox outer = new VBox();

        tree.setShowRoot(false);
        tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tree.setRoot(createTree());
        addColumn();

        // Print selection changes: there should only be two (initial selection, then final selection to "null" when nodes are filtered), but there is an extra on in the middle.
        tree.getSelectionModel().selectedItemProperty()
            .addListener((observable, oldValue, newValue) -> System.out.println(
                "Selected item (as per listener): " + (tree.getSelectionModel().getSelectedItem() == null ? "null"
                    : tree.getSelectionModel().getSelectedItem().getValue())));
        tree.getSelectionModel().getSelectedItems().addListener((Observable x) -> {
            System.out.println("-selected.items: " + tree.getSelectionModel().getSelectedItems());
        });

        final Button filterButton = new Button("Filter on \"ggg\"");

        outer.getChildren().addAll(filterButton, tree);
        final Scene scene = new Scene(outer, 640, 480);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Select a lead node: aaa3 -> aaa3.2 (as an example)
        final TreeItem<String> aaa32 = tree.getRoot().getChildren().get(2).getChildren().get(1);
        System.out.println("Value of aaa3.2 from tree (for verification): " + aaa32.getValue());

        // Expand it -- without expanding it, the bug won't occur
        aaa32.getParent().setExpanded(true);

        System.out.println("Selecting item: " + aaa32.getValue());
        // Select an item, note it is printed. Same as a user clicking the row.
        tree.getSelectionModel().select(aaa32);

        filterButton.setOnAction(event -> {
            System.out.println("About to filter on \"ggg\": " + aaa32.getValue());

            // Filter based on "ggg" (the top parent node)
            filterPredicate.set(string -> string.toLowerCase().trim().contains("ggg"));

            // BUG: The output is the below. Note that "bbb2" gets selected along the way, for some reason. This is the bug.
            //
            // Output:
            // aaa32 value from tree: TreeItem [ value: aaa3.2 ]
            // Selected item: aaa3.2
            // Selected item: bbb2
            // Selected item: null
        });
    }

    private SimpleTreeItem<String> createTree() {

        // So, we have a tree like this:
        // ggg1
        // | ggg.1.1
        // | xxx.1.2
        // | ggg.1.3
        // bbb2
        // | bbb.2.1
        // | bbb.2.2
        // | bbb.2.3
        // aaa3
        // | children
        // | aaa.3.1
        // | aaa.3.2
        // | aaa.3.3

        final List<SimpleTreeItem<String>> gggChildren = new ArrayList<>();
        gggChildren.add(new SimpleTreeItem<>("ggg1.1", null, filterPredicate));
        gggChildren.add(new SimpleTreeItem<>("xxx1.2", null, filterPredicate));
        gggChildren.add(new SimpleTreeItem<>("ggg1.3", null, filterPredicate));
        final SimpleTreeItem<String> gggTree = new SimpleTreeItem<>("ggg1", gggChildren, filterPredicate);

        final List<SimpleTreeItem<String>> bbbChildren = new ArrayList<>();
        bbbChildren.add(new SimpleTreeItem<>("bbb2.1", null, filterPredicate));
        bbbChildren.add(new SimpleTreeItem<>("bbb2.2", null, filterPredicate));
        bbbChildren.add(new SimpleTreeItem<>("bbb2.3", null, filterPredicate));
        final SimpleTreeItem<String> bbbTree = new SimpleTreeItem<>("bbb2", bbbChildren, filterPredicate);

        final List<SimpleTreeItem<String>> aaaChildren = new ArrayList<>();
        aaaChildren.add(new SimpleTreeItem<>("aaa3.1", null, filterPredicate));
        aaaChildren.add(new SimpleTreeItem<>("aaa3.2", null, filterPredicate));
        aaaChildren.add(new SimpleTreeItem<>("aaa3.3", null, filterPredicate));
        final SimpleTreeItem<String> aaaTree = new SimpleTreeItem<>("aaa3", aaaChildren, filterPredicate);

        final List<SimpleTreeItem<String>> rootChildren = new ArrayList<>();
        rootChildren.add(gggTree);
        rootChildren.add(bbbTree);
        rootChildren.add(aaaTree);

        return new SimpleTreeItem<>("root",
            rootChildren,
            filterPredicate);
    }

    static class SimpleTreeItem<T> extends TreeItem<T> {

        private final ObjectProperty<Predicate<T>> filter = new SimpleObjectProperty<>();
        private FilteredList<SimpleTreeItem<T>> children;

        public SimpleTreeItem(final T value, List<SimpleTreeItem<T>> children, ObservableValue<Predicate<T>> filter) {
            super(value, null);

            if (filter != null) {
                this.filter.bind(filter);
            }

            if (children != null) {
                addChildren(children);
            }
        }

        private void addChildren(List<SimpleTreeItem<T>> childrenParam) {
            children = new FilteredList<>(FXCollections.observableArrayList(childrenParam));
            children.predicateProperty()
                .bind(Bindings.createObjectBinding(() -> SimpleTreeItem.this::showNode, filter));

            Bindings.bindContent(getChildren(), children);
        }

        private boolean showNode(SimpleTreeItem<T> node) {
            if (filter.get() == null) {
                return true;
            }

            if (filter.get().test(node.getValue())) {
                // Node is directly matched -> so show it
                return true;
            }

            if (node.children != null) {
                // Are there children (or children of children...) that are matched? If yes we also need to show this node
                return node.children.getSource().stream().anyMatch(this::showNode);

            }
            return false;
        }
    }

    protected void addColumn() {
        TreeTableColumn<String, String> column = new TreeTableColumn<>("Some column");
        column.setPrefWidth(150);

        column.setCellFactory(param -> new TreeTableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setOnMouseClicked(null);
                } else {
                    setText(item);
                }
            }
        });

        column.setCellValueFactory(
            param -> param.getValue().valueProperty());
        tree.getColumns().add(column);
    }

//    public static void main(String[] args) {
//        launch(args);
//    }
}