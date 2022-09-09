package goryachev.apps;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class HorizontalConstrainedTableScrollingMin extends Application { 
    @Override 
    public void start(final Stage primaryStage) throws Exception { 
        // left table 
        final TableView<Object> left = new TableView<>(); 
        final TableColumn<Object, String> leftColumn = new TableColumn<>(); 
        left.getColumns().addAll(leftColumn 
                // comment to not see the on/off 
                , new TableColumn<>() 
                ); 
        left.getItems().add(new Object()); 
        left.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); 

        // scene /stage 
        primaryStage.setScene(new Scene(new SplitPane(left, new BorderPane()))); 
        primaryStage.show(); 
    } 

    public static void main(String[] args) { 
        launch(args); 
    } 
} 
