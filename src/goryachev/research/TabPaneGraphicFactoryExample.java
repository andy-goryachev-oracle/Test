package goryachev.research;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.skin.TabPaneSkin;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

// https://bugs.openjdk.org/browse/JDK-8353599
public class TabPaneGraphicFactoryExample extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Tab tab1 = new Tab("Tab1");
        tab1.setGraphic(createGraphic(tab1));

        Tab tab2 = new Tab("Tab2");
        tab2.setGraphic(createGraphic(tab2));

        Tab tab3 = new Tab("Tab3");
        tab2.setGraphic(createGraphic(tab2));

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(tab1, tab2, tab3);

        TabPaneSkin skin = new TabPaneSkin(tabPane);
        // set overflow menu factory with the same method as was used to create the tabs
        skin.setMenuGraphicFactory(this::createGraphic);
        tabPane.setSkin(skin);

        stage.setScene(new Scene(tabPane, 100, 300));
        stage.show();
    }

    // creates graphic Nodes for tabs as well as the overflow menu
    private Node createGraphic(Tab tab) {
        switch (tab.getText()) {
        case "Tab1":
            return new Circle(15);
        case "Tab2":
            return new Circle(10);
        case "Tab3":
            return new Circle(5);
        default:
            return null;
        }
    }
}
