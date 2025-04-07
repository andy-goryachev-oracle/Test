package goryachev.bugs;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.event.Event;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8353843
 */
public class TabPane_Repaint_8353843 extends Application {

    TabPane tabPane;

    @Override
    public void start(Stage stage) {
        ResizableCanvas canvas = new ResizableCanvas();
        StackPane pane = new StackPane(canvas);

        tabPane = new TabPane();
        tabPane.getTabs().add(createTab("1"));

        TextArea t = new TextArea("press [=] key to add and [-] to remove");
        t.setEditable(false);
        SplitPane splitButtonsTabPane = new SplitPane(t, tabPane);

        Button btnAdd = new Button("+");
        btnAdd.setOnAction((ev) -> add());

        Button btnRemove = new Button("-");
        btnRemove.disableProperty().bind(Bindings.isEmpty(tabPane.getTabs()));
        btnRemove.setOnAction((ev) -> remove());

        VBox panelBelow = new VBox(splitButtonsTabPane, new HBox(btnAdd, btnRemove));

        SplitPane splitAboveBelow = new SplitPane(pane, panelBelow);
        splitAboveBelow.setOrientation(Orientation.VERTICAL);

        Scene scene = new Scene(splitAboveBelow);
        stage.setScene(scene);
        stage.show();

        new RedrawTimer(() -> redraw(canvas)).start();

        stage.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKey);
    }

    private <T extends Event> void handleKey(KeyEvent ev) {
        switch (ev.getCode()) {
        case MINUS: // -
            remove();
            break;
        case EQUALS: // + is above =
            add();
            break;
        default:
            return;
        }
        ev.consume();
    }

    private void add() {
        tabPane.getTabs().add(createTab(String.format("%d", tabPane.getTabs().size() + 1)));
    }

    private void remove() {
        if (!tabPane.getTabs().isEmpty()) {
            tabPane.getTabs().removeLast();
        }
    }

    private void redraw(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setLineWidth(1);
        gc.setStroke(Color.GREEN);
        gc.strokeRect(10, 10, 50, 50);
        gc.setStroke(Color.RED);
        gc.strokeRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private Tab createTab(String name) {
        Tab result = new Tab(name);
        result.setContent(new Label(name));
        return result;
    }

    // https://stackoverflow.com/questions/24533556/how-to-make-canvas-resizable-in-javafx
    private static class ResizableCanvas extends Canvas {
        @Override
        public boolean isResizable() {
            return true;
        }

        @Override
        public double maxHeight(double width) {
            return Double.POSITIVE_INFINITY;
        }

        @Override
        public double maxWidth(double height) {
            return Double.POSITIVE_INFINITY;
        }

        @Override
        public double minWidth(double height) {
            return 1D;
        }

        @Override
        public double minHeight(double width) {
            return 1D;
        }

        @Override
        public void resize(double width, double height) {
            this.setWidth(width);
            this.setHeight(height);
        }
    }

    private static class RedrawTimer extends AnimationTimer {
        private final Runnable runnable;

        public RedrawTimer(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void handle(long now) {
            runnable.run();
        }
    }
}