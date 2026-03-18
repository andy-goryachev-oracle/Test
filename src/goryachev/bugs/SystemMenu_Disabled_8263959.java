package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;

public class SystemMenu_Disabled_8263959 extends Application {

    @Override
    public void start(Stage stage) {
        Menu menu = new Menu("Menu");
        MenuItem topLevelMenuItem = new MenuItem("Top Level Menu Item");

        Menu subMenu = new Menu("Sub-menu");
        MenuItem subLevelMenuItem = new MenuItem("Sub Level Menu Item");
        
        MenuItem m2 = new MenuItem("M2");
        Menu sub2 = new Menu("Sub1");
        sub2.getItems().add(m2);

        subMenu.getItems().addAll(subLevelMenuItem, sub2);

        menu.getItems().addAll(subMenu, topLevelMenuItem);

        MenuBar menuBar = new MenuBar();
        menuBar.setUseSystemMenuBar(true);
        menuBar.getMenus().add(menu);

        BorderPane root = new BorderPane();
        root.setTop(menuBar);

        Button alertButton = new Button("Click Me");
        root.setCenter(alertButton);
        alertButton.setOnAction(e -> createAlert(root.getScene().getWindow(), menuBar));

        Scene scene = new Scene(root, 500, 200);
        stage.setScene(scene);

        stage.show();
    }

    private void createAlert(final Window parentWindow, final MenuBar menuBar) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(parentWindow);
        alert.showingProperty().addListener((_, _, isShowing) -> menuBar.getMenus().forEach(m -> m.setDisable(isShowing)));
        alert.showAndWait();
    }
}