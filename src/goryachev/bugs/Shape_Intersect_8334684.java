package goryachev.bugs;
import java.util.function.Supplier;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8334684
 */
public class Shape_Intersect_8334684 extends Application {
    protected StackPane stack;

    @Override
    public void start(Stage stage) {
        ComboBox<Entry> choices = new ComboBox<>();
        choices.getItems().setAll(
            new Entry("Filled", this::intersectSolid),
            new Entry("Outline", this::intersectOutlines),
            new Entry("Complex", this::intersectComplex)
        );
        choices.getSelectionModel().selectedItemProperty().addListener((s,p,v) -> {
            if(v != null) {
                Node[] nodes = v.gen.get();
                stack.getChildren().setAll(nodes);
            }
        });

        stack = new StackPane();

        BorderPane bp = new BorderPane();
        bp.setBackground(Background.fill(Color.WHITE));
        bp.setTop(choices);
        bp.setCenter(stack);

        Scene scene = new Scene(bp);
        stage.setScene(scene);
        stage.setTitle("Shape");
        stage.setWidth(400);
        stage.setHeight(300);
        stage.show();

        choices.getSelectionModel().selectFirst();
    }

    static Color tran(Color c) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), 0.5);
    }

    void configFill(Path p, Color c) {
        p.setStroke(null);
        p.setFill(tran(c));
        p.setSmooth(true);
        p.setStrokeWidth(0);
        p.setStrokeType(StrokeType.INSIDE);
    }

    void configOutline(Path p, Color c) {
        p.setStroke(tran(c));
        p.setSmooth(true);
        p.setStrokeWidth(10);
        p.setStrokeType(StrokeType.CENTERED);
    }

    Node[] intersectSolid() {
        Path p1 = new Path();
        configFill(p1, Color.GREEN);
        p1.getElements().addAll(
            new MoveTo(0.0, 0),
            new LineTo(100, 10),
            new LineTo(110, 45),
            new LineTo(10, 50),
            new ClosePath()
        );
        
        Path p2 = new Path();
        configFill(p2, Color.RED);
        p2.getElements().addAll(
            new MoveTo(50, 0),
            new LineTo(75, 5),
            new LineTo(80, 120),
            new LineTo(45, 125),
            new ClosePath()
        );

        Shape ch = Shape.intersect(p1, p2);
        ch.setSmooth(true);
        ch.setFill(tran(Color.BLACK));
        ch.setStrokeWidth(0);
        
        return new Node[] {
            p1,
            p2,
            ch
        };
    }

    Node[] intersectOutlines() {
        Path p1 = new Path();
        configOutline(p1, Color.GREEN);
        p1.getElements().addAll(
            new MoveTo(0.0, 0),
            new LineTo(100, 10),
            new LineTo(110, 45),
            new LineTo(10, 50),
            new ClosePath()
        );
        
        Path p2 = new Path();
        configOutline(p2, Color.RED);
        p2.getElements().addAll(
            new MoveTo(50, 0),
            new LineTo(75, 5),
            new LineTo(80, 120),
            new LineTo(45, 125),
            new ClosePath()
        );

        Shape ch = Shape.intersect(p1, p2);
        ch.setSmooth(true);
        ch.setStrokeWidth(0);
        ch.setFill(tran(Color.BLACK));
        
        return new Node[] {
            p1,
            p2,
            ch
        };
    }

    Node[] intersectComplex() {
        Rectangle r = new Rectangle(200, 30);
        r.setX(0);
        r.setY(40);
        r.setFill(tran(Color.RED));

        Path p = new Path();
        p.setStroke(null);
        p.setFill(tran(Color.GREEN));
        p.getElements().setAll(
            new MoveTo(0,0),
            new ArcTo(60, 60, 0, 30, 30, false, true),
            new CubicCurveTo(70, 70, 80, 80, 90, 0),
            new QuadCurveTo(50, 50, 20, 20),
            new ClosePath()
        );

        Shape ch = Shape.intersect(r, p);
        ch.setSmooth(true);
        ch.setStrokeWidth(0);
        ch.setFill(tran(Color.BLACK));

        return new Node[] { r, p, ch };
    }

    static class Entry {
        private final String name;
        public final Supplier<Node[]> gen;

        public Entry(String name, Supplier<Node[]> gen) {
            this.name = name;
            this.gen = gen;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}