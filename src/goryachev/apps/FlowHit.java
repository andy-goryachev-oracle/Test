package goryachev.apps;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.HitInfo;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

/**
 * This app tests the HitTest method for Text and TextFlow. Both methods return the same HitInfo.
 * 
 * Is this a bug in TextFlow?
 * https://bugs.openjdk.org/browse/JDK-8194704
 */
public class FlowHit extends Application {

    private Font mainFont = Font.font("DejaVu Serif", FontWeight.NORMAL, 20);
    private BorderPane bp = new BorderPane();
    private StackPane sp = new StackPane();
    private VBox vbox = new VBox();
    private TextFlow flow = new TextFlow();
    private String s1 = 
        "Electromagnetic waves cover a wide spectrum from radio waves to gamma rays. Waves can interfere. " +
        "Constructive interference occurs when two waves are in phase. ";

    public void test() {
        try {
            init();
            addText(flow, s1);
            vbox.getChildren().add(flow);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {
        try {
            //insets: top, right, bottom, left
            bp.setPadding(new Insets(5, 5, 5, 5));
            bp.setStyle(" -fx-border-color: green;-fx-border-width: 2px;-fx-border-style: solid;");
            bp.setPrefSize(500, 400);
            //
            sp.setPadding(new Insets(10, 5, 10, 5));
            sp.setStyle(" -fx-border-color: blue;-fx-border-width: 1px;-fx-border-style: solid;");
            //
            vbox.setSpacing(8);
            vbox.setPadding(new Insets(15, 5, 15, 5));
            vbox.setStyle(" -fx-border-color: red;-fx-border-width: 1px;-fx-border-style: solid;");
            //
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addText(TextFlow flow, String s) {
        try {
            //LOG.log(Level.INFO, "\n\nText s: " + s);
            //split string into words
            String[] ss = s.split(" ");
            int len = ss.length;
            for (int i = 0; i < len; i++) {
                //create a word
                Text tx = new Text(ss[i] + " ");
                //set font on each word
                tx.setFont(mainFont);
                //add mouse click handler
                tx.setOnMouseReleased(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            event.consume();
                            double mx = event.getX();
                            double my = event.getY();
                            int index = flow.getChildren().indexOf(tx);
                            //check baseline offset
                            checkHit(flow, index, mx, my);
                        }
                    }
                });

                //add one word to flow
                flow.getChildren().add(tx);
            }
            flow.setStyle(" -fx-border-color: orange;-fx-border-width: 1px;-fx-border-style: solid;");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** check hit info of selected node */
    public void checkHit(TextFlow flow, int index, double mx, double my) {
        try {
            //
            Node n = flow.getChildren().get(index);
            Text t = (Text)n;
            //create rectangle around selected node (may be commented out)
            addBox(n);
            //find mouse point
            Point2D pt = new Point2D(mx, my);
            //
            //get Text HitInfo
            HitInfo hitText = t.hitTest(pt);
            //
            //get TextFlow HitInfo
            HitInfo hitFlow = flow.hitTest(pt);

            System.out.println(
                "index=" + index +
                ", text=" + t.getText() +
                ", pt=" + pt +
                "\n  Text.hit=" + hitText +
                "\n  TextFlow.hit=:" + hitFlow
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addBox(Node n) {
        //add box around of selected node
        try {
            //get text bounds within flow (parent)
            Bounds parentB = n.getBoundsInParent();
            // LOG.log(Level.INFO, "\n\n node bounds in parent: " + parentB);

            //convert to scene coords
            Bounds sceneB = flow.localToScene(parentB);
            // LOG.log(Level.INFO, "\n\n scene bounds: " + sceneB);

            //create rectangle in scene coords
            Rectangle r = new Rectangle(sceneB.getMinX(), sceneB.getMinY(), sceneB.getWidth(), sceneB.getHeight());
            r.setStroke(Color.GREEN);
            r.setFill(Color.TRANSPARENT);
            //add rectangle to border pane
            bp.getChildren().add(r);
            //
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        //add textflow to vbox
        test();
        // add stack pane to outer pane
        sp.setAlignment(Pos.CENTER);
        sp.getChildren().add(vbox);
        bp.setCenter(sp);
        //
        // setup scene and stage
        Scene scene = new Scene(bp, 600, 500);

        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
        //check baselines of selected nodes
    }
}