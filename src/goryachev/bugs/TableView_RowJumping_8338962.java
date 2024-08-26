package goryachev.bugs;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8338962
 */
public class TableView_RowJumping_8338962 extends Application {

      private static class Foo {
        private final String a;
        private final int b;
        private final int c;
        private final int d;
        private final int e;
        private final int f;
        private final int g;
        private final int h;

        public Foo(String a, int b, int c, int d, int e, int f, int g, int h) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            this.e = e;
            this.f = f;
            this.g = g;
            this.h = h;
        }
    }

    private TableView<Foo> table = new TableView<>(FXCollections.observableList(
            List.of(new Foo("A", 1, 2, 3, 4, 5, 6, 7),
                    new Foo("AA", 10, 20, 30, 40, 50, 60, 70),
                    new Foo("AAA", 100, 200, 300, 400, 500, 600, 700))));

    @Override
    public void start(Stage primaryStage) {
        Application.setUserAgentStylesheet(toDataURI(CSS));
        var aColumn = new TableColumn<Foo, String>("A");
        aColumn.setCellValueFactory((data) -> new ReadOnlyObjectWrapper<>(data.getValue().a));
        aColumn.setResizable(true);

        var bColumn = new TableColumn<Foo, Integer>("B");
        bColumn.setCellValueFactory((data) -> new ReadOnlyObjectWrapper<>(data.getValue().b));
        bColumn.setResizable(true);

        var cColumn = new TableColumn<Foo, Integer>("C");
        cColumn.setCellValueFactory((data) -> new ReadOnlyObjectWrapper<>(data.getValue().c));
        cColumn.setResizable(true);

        var dColumn = new TableColumn<Foo, Integer>("D");
        dColumn.setCellValueFactory((data) -> new ReadOnlyObjectWrapper<>(data.getValue().d));
        dColumn.setResizable(true);

        var eColumn = new TableColumn<Foo, Integer>("E");
        eColumn.setCellValueFactory((data) -> new ReadOnlyObjectWrapper<>(data.getValue().e));
        eColumn.setResizable(true);

        var fColumn = new TableColumn<Foo, Integer>("F");
        fColumn.setCellValueFactory((data) -> new ReadOnlyObjectWrapper<>(data.getValue().f));
        fColumn.setResizable(true);

        var gColumn = new TableColumn<Foo, Integer>("G");
        gColumn.setCellValueFactory((data) -> new ReadOnlyObjectWrapper<>(data.getValue().g));
        gColumn.setResizable(true);

        var hColumn = new TableColumn<Foo, Integer>("H");
        hColumn.setCellValueFactory((data) -> new ReadOnlyObjectWrapper<>(data.getValue().h));
        hColumn.setResizable(true);

        table.getColumns().addAll(aColumn, bColumn, cColumn, dColumn, eColumn, fColumn, gColumn, hColumn);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);//LINE X

        VBox.setVgrow(table, Priority.ALWAYS);

        VBox root = new VBox(new TextField(), table);
        var scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static String toDataURI(String css) {
        Charset utf8 = Charset.forName("utf-8");
        byte[] b = css.getBytes(utf8);
        return "data:text/css;base64," + Base64.getEncoder().encodeToString(b);
    }

    private static final String CSS =
        """
        .root {
          -color-dark: #0d0e12;
          -color-light: #f8f8f2;
          -color-fg-default: #f8f8f2;
          -color-fg-muted: #bfbfc3;
          -color-fg-subtle: #7e7f86;
          -color-fg-emphasis: #0d0e12;
          -color-bg-default: #282a36;
          -color-bg-overlay: #282a36;
          -color-bg-subtle: #3d3f4a;
          -color-bg-inset: #181920;
          -color-border-default: #685ab3;
          -color-border-muted: #52468c;
          -color-border-subtle: #3c3366;
          -color-shadow-default: #0d0e12;
          -color-neutral-emphasis-plus: #94959b;
          -color-neutral-emphasis: #7e7f86;
          -color-neutral-muted: rgba(126, 127, 134, 0.4);
          -color-neutral-subtle: rgba(148, 149, 155, 0.1);
          -color-accent-fg: #9580ff;
          -color-accent-emphasis: #9580ff;
          -color-accent-muted: rgba(149, 128, 255, 0.4);
          -color-accent-subtle: rgba(149, 128, 255, 0.1);
          -fx-background-color: -color-bg-default;
          -fx-font-size: 14px;
          -fx-background-radius: inherit;
          -fx-background-insets: inherit;
          -fx-padding: inherit;
        }
        
        
        .table-view > .virtual-flow > .clipped-container > .sheet > .table-row-cell:filled:selected {
          -color-cell-fg: -color-cell-fg-selected;
          -fx-background-color: -color-cell-border, -color-cell-bg-selected;
        }
        
        .table-view:focused > .virtual-flow > .clipped-container > .sheet > .table-row-cell:filled:selected {
          -color-cell-fg: -color-cell-fg-selected-focused;
          -fx-background-color: -color-cell-border, -color-cell-bg-selected-focused;
        }
        
        .table-view > .virtual-flow > .clipped-container > .sheet > .table-row-cell .table-cell:selected {
          -fx-background-color: -color-cell-bg-selected;
          -fx-background-insets: 0 0 2 0;
        }
        
        .table-view:focused > .virtual-flow > .clipped-container > .sheet > .table-row-cell .table-cell:selected {
          -fx-background-color: -color-cell-bg-selected-focused;
        }
        
        .table-view > .virtual-flow > .clipped-container > .sheet > .table-row-cell .text-field-table-cell:focus-within {
          -fx-background-insets: 0, 1, 2;
          -fx-background-color: -color-bg-default, -color-accent-emphasis, -color-bg-default;
        }
        
        .table-view {
          -color-cell-bg: -color-bg-default;
          -color-cell-fg: -color-fg-default;
          -color-cell-bg-selected: -color-accent-subtle;
          -color-cell-fg-selected: -color-fg-default;
          -color-cell-bg-selected-focused: -color-accent-subtle;
          -color-cell-fg-selected-focused: -color-fg-default;
          -color-cell-bg-odd: -color-bg-subtle;
          -color-cell-border: -color-border-default;
          -color-disclosure: -color-fg-muted;
          -fx-border-color: -color-cell-border;
          -fx-border-width: 1px;
          -fx-border-radius: 0;
          -color-header-bg: -color-bg-subtle;
          -color-header-fg: -color-fg-default;
        }
        .table-view > .virtual-flow > .corner {
          -fx-background-color: -color-cell-border;
          -fx-opacity: 0.4;
        }
        .table-view > .virtual-flow:disabled {
          -fx-opacity: 0.4;
        }
        .table-view.edge-to-edge {
          -fx-border-width: 0;
        }
        .table-view.bordered > .column-header-background .column-header {
          -fx-background-color: -color-cell-border, -color-header-bg;
          -fx-background-insets: 0, 0 1 0 0;
        }
        .table-view > .column-header-background {
          -fx-background-color: -color-cell-border, -color-header-bg;
          -fx-background-insets: 0, 0 0 1 0;
        }
        .table-view > .column-header-background .column-header {
          -fx-background-color: transparent;
          -fx-background-insets: 0;
          -fx-size: 40px;
          -fx-padding: 0;
          -fx-font-weight: bold;
          -fx-border-color: -color-cell-border;
          -fx-border-width: 0 1 1 0;
        }
        .table-view > .column-header-background .column-header .label {
          -fx-text-fill: -color-header-fg;
          -fx-alignment: CENTER_LEFT;
          -fx-padding: 0 0.5em 0 0.5em;
        }
        .table-view > .column-header-background .column-header GridPane {
          -fx-padding: 0 4px 0 0;
        }
        .table-view > .column-header-background .column-header .arrow {
          -fx-background-color: -color-header-fg;
          -fx-padding: 3px 4px 3px 4px;
          -fx-shape: "M 0 0 h 7 l -3.5 4 z";
        }
        .table-view > .column-header-background .column-header .sort-order-dots-container {
          -fx-padding: 2px 0 2px 0;
        }
        .table-view > .column-header-background .column-header .sort-order-dots-container > .sort-order-dot {
          -fx-background-color: -color-header-fg;
          -fx-padding: 0.115em;
          -fx-background-radius: 0.115em;
        }
        .table-view > .column-header-background .column-header .sort-order {
          -fx-padding: 0 0 0 2px;
        }
        .table-view > .column-header-background > .filler {
          -fx-background-color: transparent;
          -fx-border-color: -color-cell-border;
          -fx-border-width: 0 0 1 0;
        }
        .table-view > .column-header-background > .show-hide-columns-button {
          -fx-border-color: -color-cell-border;
          -fx-border-width: 0 0 1 0;
          -fx-cursor: hand;
        }
        .table-view > .column-header-background > .show-hide-columns-button > .show-hide-column-image {
          -fx-background-color: -color-header-fg;
          -fx-shape: "M12 8c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm0 2c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm0 6c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z";
          -fx-scale-shape: true;
          -fx-padding: 0.4em 0.115em 0.4em 0.115em;
        }
        .table-view .column-resize-line {
          -fx-background-color: -color-accent-emphasis;
          -fx-padding: 0 1 0 1;
        }
        .table-view .column-drag-header {
          -fx-background-color: -color-accent-muted;
        }
        .table-view .column-overlay {
          -fx-background-color: -color-accent-muted;
        }
        .table-view .placeholder > .label {
          -fx-font-size: 1.25em;
        }
        .table-view.bordered .table-row-cell > .table-cell {
          -fx-border-color: transparent -color-cell-border transparent transparent;
        }
        .table-view.bordered .table-row-cell > .table-cell:empty {
          -fx-border-color: transparent;
        }
        .table-view.dense > .column-header-background .column-header {
          -fx-size: 34px;
        }
        .table-view.dense .table-row-cell {
          -fx-cell-size: 2em;
        }
        .table-view.striped .table-row-cell {
          -fx-background-insets: 0;
        }
        .table-view.striped.bordered .table-row-cell {
          -fx-background-insets: 0, 0 0 1 0;
        }
        .table-view.striped .table-row-cell:filled:odd {
          -fx-background-color: -color-cell-border, -color-cell-bg-odd;
        }
        .table-view.no-header > .column-header-background {
          -fx-max-height: 0;
          -fx-pref-height: 0;
          -fx-min-height: 0;
        }
        .table-view .table-row-cell {
          -fx-background-color: -color-cell-border, -color-cell-bg;
          -fx-background-insets: 0, 0 0 1 0;
          -fx-padding: 0;
          -fx-cell-size: 2.8em;
        }
        .table-view .table-row-cell:empty {
          -fx-background-color: transparent;
          -fx-background-insets: 0;
        }
        .table-view .table-row-cell:empty > .table-cell {
          -fx-border-color: transparent;
        }
        .table-view .table-row-cell > .table-cell {
          -fx-padding: 0 0.5em 0 0.5em;
          -fx-text-fill: -color-cell-fg;
          -fx-alignment: BASELINE_LEFT;
        }
        .table-view .table-row-cell > .table-cell.table-column.align-left {
          -fx-alignment: BASELINE_LEFT;
        }
        .table-view .table-row-cell > .table-cell.table-column.align-center {
          -fx-alignment: BASELINE_CENTER;
        }
        .table-view .table-row-cell > .table-cell.table-column.align-right {
          -fx-alignment: BASELINE_RIGHT;
        }
        
        .table-view:constrained-resize > .virtual-flow > .clipped-container > .sheet > .table-row-cell > .table-cell:last-visible {
          -fx-border-color: transparent;
        }
        
        .table-view .table-row-cell > .table-cell.check-box-table-cell,
        .table-view .table-row-cell > .table-cell.font-icon-table-cell {
          -fx-alignment: BASELINE_LEFT;
        }
        """;
}

