/*******************************************************************************
 * Copyright (c) 2017 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package goryachev.apps;
//package docking1;

import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/** Start of a 'Docking' demo
 *
 *  Dragging a tab tries to start the visual feedback
 *  of moving a tab around similar to
 *  http://berry120.blogspot.co.uk/2014/01/draggable-and-detachable-tabs-in-javafx.html
 *  and other docking libs.
 *
 *  It creates a new Stage that represents the dragged tab.
 *  On Linux, however, that Stage takes the focus,
 *  so one then needs to click and drag the original tab AGAIN
 *  to move the dragged item around.
 *
 *  --> Doesn't work, not implemented further
 *
 *  @author Kay Kasemir
 */
public class Demo1 extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

    /** Representation of a 'Tab' that's being dragged */
    static class DraggedTab extends Stage
    {
        public DraggedTab(final DraggableTab tab)
        {
            initModality(Modality.NONE);
            initStyle(StageStyle.UNDECORATED);
            setAlwaysOnTop(true);
            setOpacity(0.5);
            final Bounds bounds = tab.getTabPane().getLayoutBounds();
            setWidth(bounds.getWidth());
            setHeight(bounds.getHeight());
            show();
        }
    }

    /** Representation of tab that's currently being dragged */
    private DraggedTab dragged_tab = null;

    class DraggableTab extends Tab
    {
        private final Label tab_name;

        public DraggableTab(final String label)
        {
            // Create tab with no 'text',
            // instead using a Label for the text
            // because the label can react to drag operations
            tab_name = new Label(label);
            setGraphic(tab_name);


            tab_name.setOnMouseDragged(event ->
            {
                System.out.println("Drag " + tab_name.getText() + " to " + event.getScreenX() + ", " + event.getScreenY());

                if (dragged_tab == null)
                {
                    dragged_tab = new DraggedTab(this);
                    tab_name.requestFocus();
                }
                dragged_tab.setX(event.getScreenX());
                dragged_tab.setY(event.getScreenY());

            });

            tab_name.setOnMouseReleased(event ->
            {
                if (dragged_tab != null)
                {
                    dragged_tab.close();
                    dragged_tab = null;
                    // TODO: Should now 'drop' the dragged tab
                }
            });
        }

        /** Label of the tab */
        public String getLabel()
        {
            return tab_name.getText();
        }
    }

    @Override
    public void start(final Stage stage) throws Exception
    {
        final DraggableTab tab1 = new DraggableTab("Tab 1");
        tab1.setContent(new Rectangle(500, 500, Color.BLACK));

        final DraggableTab tab2 = new DraggableTab("Tab 2");
        tab2.setContent(new Rectangle(500, 500, Color.RED));

        final TabPane tabs = new TabPane();
        tabs.getTabs().addAll(tab1, tab2);

        final StackPane root = new StackPane();
        root.getChildren().add(tabs);

        final Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("ZINC");
        //stage.getIcons().add(new Image(new FileInputStream("icons/zinc.png")));
        stage.show();
    }
}