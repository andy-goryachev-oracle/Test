package goryachev.bugs;

import java.util.Arrays;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Setting split pane dividers.
 * 
 * TODO this code does not reproduce the issue I see with restoring dividers in my other projects...
 */
public class SplitPane_Dividers extends Application {
    private SplitPane split;
    private static final double[] DIVS = { 0.6, 0.8 };
    
    @Override
    public void start(Stage stage) throws Exception {
        split = new SplitPane();
        split.getDividers().addListener((Observable p) -> {
            p("listener=", split.getDividerPositions());
        });
        
        Button add = new Button("Add SplitPane");
        add.setOnAction((ev) -> {
            addSplitPane();
        });
        
        Button setDividersButton = new Button("Set Dividers");
        setDividersButton.setOnAction((ev) -> {
            setDividerPositions(DIVS);  
        });

        HBox bp = new HBox(
            add,
            setDividersButton
        );
        
        BorderPane p = new BorderPane();
        p.setCenter(split);
        p.setBottom(bp);

        Scene scene = new Scene(p);
        stage.setScene(scene);
        stage.setWidth(700);
        stage.setHeight(500);
        stage.setTitle("SplitPane: Dividers");

        stage.show();
    }

    private void addSplitPane() {
        split.getItems().setAll(
            p("this window demonstrates repeated calling of setDividers() on a SplitPane"),
            p("as we have a bug"),
            p("here")
        );
        p("initial=", split.getDividerPositions());
        setDividerPositions(DIVS);
    }
    
    private static Node p(String text) {
        //Label t = new Label(text);
//        t.setMinWidth(10);
//        t.setMaxWidth(Double.MAX_VALUE);
        //return new BorderPane(t);
        return new BorderPane();
    }
    
    private void p(String prefix, double[] divs) {
        System.out.println(prefix + Arrays.stream(divs).
            mapToObj(Double::valueOf).
            map(String::valueOf).
            collect(Collectors.joining(", "))
        );
    }
    
    private void setDividerPositions(double[] divs) {
        p("before setting. divs=", split.getDividerPositions());
        p("setting ", divs);
        split.setDividerPositions(divs);
        p("added. divs=", split.getDividerPositions());
        Platform.runLater(() -> {
            p("RUN.LATER before setting. divs=", split.getDividerPositions());
            split.setDividerPositions(divs);
            p("RUN.LATER added. divs=", split.getDividerPositions());
        });
    }
}