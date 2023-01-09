package goryachev.apps;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * https://bugs.openjdk.org/browse/JDK-8283551
 */
public class TwoStagesMenuBarLeakSample {
    public static void main(String[] args) {
        System.err.println(Runtime.version().toString());
        Application.launch(TwoStagesMenuBarLeakSample.MainFx.class, args);
    }

    public static class MainFx extends Application {
        @Override
        public void start(final Stage primaryStage) throws Exception {
            final BorderPane borderPane = new BorderPane(new VBox(new Label("sample")));
            final Menu one = new Menu("_One");
            one.getItems().add(new MenuItem("Sample 1"));
            one.getItems().add(new MenuItem("Sample 2"));
            one.getItems().add(new MenuItem("Sample 3"));
            one.getItems().add(new MenuItem("Sample 4"));
            final var menuBar = new MenuBar(one);
            borderPane.setTop(menuBar);
            borderPane.setCenter(new Label("Press ` to switch MenuBar to other window."));
            Scene scene = new Scene(borderPane, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.show();
            final var stage2 = new Stage(StageStyle.DECORATED);
            final BorderPane stage2Container = new BorderPane();
            stage2Container.setCenter(new Label("Press ` to switch MenuBar to other window."));
            stage2.setScene(new Scene(stage2Container, 800, 600));
            primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, aKeyEvent -> {
                if (KeyCombination.keyCombination("`").match(aKeyEvent)) {
                    stage2Container.setTop(menuBar);
                    stage2.requestFocus();
                }
            });
            stage2.addEventHandler(KeyEvent.KEY_PRESSED, aKeyEvent -> {
                if (KeyCombination.keyCombination("`").match(aKeyEvent)) {
                    borderPane.setTop(menuBar);
                    primaryStage.requestFocus();
                }
            });
            stage2.show();
        }
    }
}