package goryachev.research;

import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.BorderPane;
import javafx.stage.Popup;
import javafx.stage.Stage;

/**
 * A Demo App for https://bugs.openjdk.org/browse/JDK-8287712
 */
public class CustomPopupResearch extends Application {
    @Override
    public void start(Stage stage) {
        Button button = new Button();
        BorderPane.setAlignment(button, Pos.CENTER_RIGHT);
        new CustomPopup().install(button);
        
        BorderPane root = new BorderPane();
        root.setRight(button);

        stage.setTitle("Custom Popup Example");
        stage.setScene(new Scene(root, 450, 150));
        stage.show();
    }

    /**
     * Custom popup can serve as a tooltip with interaction.
     * Make sure to style it similar to .tooltip in modena.css
     * You may want to add more code for delaying the show and hide,
     * position the popup where you want it (making sure to handle various corner cases)
     */
    public static class CustomPopup extends Popup {
        private final TextArea field;
        private final BorderPane pane;

        public CustomPopup() {
            field = new TextArea();
            field.setText("Feel free to copy from this\nTextArea.");

            pane = new BorderPane(field);
            pane.addEventHandler(MouseEvent.MOUSE_EXITED, this::handleMouseExited2);
            pane.setCenter(field);
            pane.setPrefSize(200, 150);
            pane.setStyle("-fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.5) , 10, 0.0 , 0 , 3 );");

            getContent().add(pane);
        }

        public void install(Node node) {
            node.addEventHandler(MouseEvent.MOUSE_ENTERED, this::handleMouseEntered);
            node.addEventHandler(MouseEvent.MOUSE_EXITED, this::handleMouseExited);
            node.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
        }

        void handleMouseEntered(MouseEvent ev) {
            System.out.println("entered");
            if (!isShowing()) {
                Node owner = (Node)ev.getSource();
                Bounds local = owner.getBoundsInLocal();
                Bounds screen = owner.localToScreen(local);
                double x = screen.getMinX();
                double y = screen.getMinY();
                show(owner, x, y);
            }
        }

        void handleMouseExited(MouseEvent ev) {
            System.out.println("exited");
            if (isShowing()) {
                PickResult pick = ev.getPickResult();
                Node p = pick.getIntersectedNode();
                Node owner = (Node)ev.getSource();
                if (isNotParent(p, owner)) {
                    hide();
                }
            }
        }
        
        void handleMouseExited2(MouseEvent ev) {
            System.out.println("exited2");
            if (isShowing()) {
                hide();
            }
        }

        void handleMousePressed(MouseEvent ev) {
            System.out.println("pressed");
        }

        boolean isNotParent(Node n, Node owner) {
            while (n != null) {
                if ((n == pane) || (n == owner)){
                    return false;
                }
                n = n.getParent();
            }
            return true;
        }
    }
}