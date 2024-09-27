package goryachev.research;
import java.util.List;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
//import javafx.scene.traversal.TraversalDirection;
//import javafx.scene.traversal.TraversalPolicy;
import javafx.stage.Stage;

/**
 */
public class FocusPolicyResearch extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Focus Policy Research");
        stage.setWidth(450);
        stage.setHeight(550);
        
        ToggleButton t1 = new ToggleButton("1");
        ToggleButton t2 = new ToggleButton("2");
        ToggleButton t3 = new ToggleButton("3");

        EventHandler<KeyEvent> f = new EventHandler<>() {
            @Override
            public void handle(KeyEvent ev) {
                switch(ev.getCode()) {
                case UP:
                case DOWN:
                    // TODO here we could force the focus traversal
                    // by using FocusTraversal API or requestFocus()
                    System.out.println(ev);
//                    ev.consume();
                    break;
                }
            }
        };
//        t1.addEventFilter(KeyEvent.ANY, f);
//        t2.addEventFilter(KeyEvent.ANY, f);
//        t3.addEventFilter(KeyEvent.ANY, f);
        
//        ToggleGroup g = new ToggleGroup();
//        g.getToggles().addAll(
//            t1,
//            t2,
//            t3
//        );
        
        Button b = new Button("Button");

        VBox vb = new VBox(
            t1,
            t2,
            t3,
            b
        );
        vb.addEventFilter(KeyEvent.ANY, f);
        
        /*
        vb.setTraversalPolicy(new TraversalPolicy() {
            @Override
            public Node select(Parent root, Node node, TraversalDirection dir) {
                switch(dir) {
                case UP:
                case PREVIOUS:
                    return sel(node, -1);
                case DOWN:
                case NEXT:
                    return sel(node, 1);
                default:
                    return getDefault().select(root, node, dir);
                }
            }
            
            private Node sel(Node n, int delta) {
                List<Node> cs = vb.getChildren();
                int ix = cs.indexOf(n);
                if(ix < 0) {
                    return null;
                }
                ix += delta;
                if(ix < 0) {
                    ix = cs.size() - 1;
                } else if(ix >= cs.size()) {
                    ix = 0;
                }
                return cs.get(ix);
            }

            @Override
            public Node selectFirst(Parent root) {
                return b;
            }

            @Override
            public Node selectLast(Parent root) {
                return t2;
            }
        });
        */

        Scene scene = new Scene(vb);
        stage.setScene(scene);
        stage.show();
    }
}