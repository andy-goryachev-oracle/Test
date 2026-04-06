package goryachev.bugs;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8335541
 * 
 * 1. Launch the application.
 * 2. Click the "Open dialog" button.
 * 3. Quickly click on the "Help" menu in the menu bar (before the dialog shows up).
 *  - Hover with the mouse the Help menuItems, and when the dialog shows up, hover the Help menu: another About menuItem pops up.
 *  - At this point clicking on any menu (File, Help), or menuItem (Exit or About) will close the menus, but the first Help popup won't close
 *  
 *  BUG macOS 26.3.1:
 *  - click on system menu, the pulldown shows Search Field + About
 *  - dialog appears, the expanded menu pulldown hides
 *  - click on Help, hold -> see About, no search field
 *  - release mouse -> entire menu disappears
 */
public class SystemMenu_Popup_8335541 extends Application {

    private Executor primaryExecutor = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void start(Stage stage) {
        Menu fileMenu = new Menu("File");
        MenuItem exitMenuItem = new MenuItem("Exit");
        fileMenu.getItems().add(exitMenuItem);
        Menu help = new Menu("Help");
        MenuItem about = new MenuItem("About");
        help.getItems().add(about);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, help);
        menuBar.setUseSystemMenuBar(true);

        Button button = new Button("Open Dialog");
        button.setOnAction(__ -> {
            primaryExecutor.execute(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> {
                    Dialog<ButtonType> d = new Dialog<>();
                    d.getDialogPane().getButtonTypes().setAll(ButtonType.CANCEL);
                    d.initOwner(stage);
                    d.initModality(Modality.APPLICATION_MODAL);
                    d.show();
                });
            });
        });

        BorderPane borderPane = new BorderPane(button);
        borderPane.setTop(menuBar);

        Scene scene = new Scene(borderPane, 320, 240);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        System.exit(0);
    }
}