package goryachev.examples;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.skin.TabPaneSkin;
import javafx.scene.shape.Circle;

// https://bugs.openjdk.org/browse/JDK-8353599
public class TabPaneGraphicFactoryExample {
    public void example() {
        Tab tab1 = new Tab("Tab1");
        tab1.setGraphic(createGraphic(tab1));

        Tab tab2 = new Tab("Tab2");
        tab2.setGraphic(createGraphic(tab2));

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(tab1, tab2);

        TabPaneSkin skin = new TabPaneSkin(tabPane);
        // set overflow menu factory with the same method as was used to create the tabs
        //skin.setMenuGraphicFactory(this::createGraphic);
        tabPane.setSkin(skin);
    }

    // creates graphic Nodes for tabs as well as the overflow menu
    private Node createGraphic(Tab tab) {
        switch (tab.getText()) {
        case "Tab1":
            return new Circle(10);
        case "Tab2":
            return new Canvas(10, 10);
        default:
            return null;
        }
    }
}
