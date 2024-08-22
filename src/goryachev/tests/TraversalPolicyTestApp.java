package goryachev.tests;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
//import javafx.scene.traversal.TraversalDirection;
//import javafx.scene.traversal.TraversalPolicy;
import javafx.stage.Stage;

/**
 * Tests the new custom focus traversal policy APIs.
 */
public class TraversalPolicyTestApp extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle(" Traversal Policy");

        Button b00 = b("b.0.0");
        Button b01 = b("b.0.1");
        Button b02 = b("b.0.2");
        Button b10 = b("b.1.0");
        Button b11 = b("b.1.1");
        Button b12 = b("b.1.2");
        Button b20 = b("b.2.0");
        Button b21 = b("b.2.1");
        Button b22 = b("b.2.2");
        
        GridPane p = new GridPane();
        p.add(b00, 0, 0);
        p.add(b01, 0, 1);
        p.add(b02, 0, 2);
        p.add(b10, 1, 0);
        p.add(b11, 1, 1);
        p.add(b12, 1, 2);
        p.add(b20, 2, 0);
        p.add(b21, 2, 1);
        p.add(b22, 2, 2);

        // TODO
        // uncomment once focus traversal is integrated
        //p.setTraversalPolicy(customTraversalPolicy(b00, b10, b20, b01, b11, b21, b02, b12, b22));
        
        BorderPane bp = new BorderPane(p);
        bp.setTop(new HBox(
            b("T.0"),
            b("T.1"),
            b("T.2"),
            b("T.3")
        ));

        Scene scene = new Scene(bp, 500, 400);
        stage.setScene(scene);
        stage.show();
    }

    private Button b(String text) {
        Button b = new Button(text) {
            @Override
            public String toString() {
                return text;
            }
        };
        b.setMinWidth(70);
        return b;
    }

    // TODO uncomment once focus traversal is integrated
//    private TraversalPolicy customTraversalPolicy(Node... nodes) {
//        return new TraversalPolicy() {
//            @Override
//            public Node select(Parent root, Node owner, TraversalDirection dir) {
//                System.out.println("select root=" + root + " owner=" + owner + " dir=" + dir);
//                int ix = indexOf(owner);
//                if (ix < 0) {
//                    return null;
//                }
//
//                switch (dir) {
//                case NEXT:
//                case NEXT_IN_LINE:
//                    if (ix >= (nodes.length - 1)) {
//                        return findNextFocusableNode(root, owner, dir);
//                    }
//                    ix++;
//                    break;
//                case PREVIOUS:
//                    if (ix <= 0) {
//                        return findPreviousFocusableNode(root, owner);
//                    }
//                    ix--;
//                    break;
//                case LEFT:
//                case UP:
//                    ix--;
//                    break;
//                case DOWN:
//                case RIGHT:
//                default:
//                    ix++;
//                }
//
//                if (ix < 0) {
//                    return selectLast(root);
//                } else if (ix >= nodes.length) {
//                    return selectFirst(root);
//                }
//                return nodes[ix];
//            }
//
//            @Override
//            public Node selectFirst(Parent root) {
//                System.out.println("selectFirst root=" + root);
//                return nodes[0];
//            }
//
//            @Override
//            public Node selectLast(Parent root) {
//                System.out.println("selectLast root=" + root);
//                int ix = nodes.length - 1;
//                if (ix < 0) {
//                    return null;
//                }
//                return nodes[ix];
//            }
//
//            private int indexOf(Node n) {
//                for (int i = nodes.length - 1; i >= 0; --i) {
//                    if (nodes[i] == n) {
//                        return i;
//                    }
//                }
//                return -1;
//            }
//        };
//    }
}