package goryachev.bugs;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class EmbeddedFrameBug {
    private static final boolean SET_FRAME_VISIBLE_LATE = true;

    private static void initAndShowGUI() //throws Exception
    {
        for (GraphicsDevice d: GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
            GraphicsConfiguration c = d.getDefaultConfiguration();
            Rectangle r = c.getBounds();

            JFrame frame = new JFrame("Swing and JavaFX");
            final JFXPanel fxPanel = new JFXPanel();
            frame.add(fxPanel);
            frame.setSize(300, 200);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocation((int)r.getCenterX(), (int)r.getCenterY());

            if (!SET_FRAME_VISIBLE_LATE) {
                setFrameVisible(frame);
            }

            Platform.runLater(() -> {
                try {
                    initFX(fxPanel, frame);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            if (SET_FRAME_VISIBLE_LATE) {
                setFrameVisible(frame);
            }
        }
    }

    private static void initFX(JFXPanel fxPanel, JFrame frame) throws Exception {
        // This method is invoked on the JavaFX thread
        Scene scene = createScene();
        fxPanel.setScene(scene);
    }

    private static void setFrameVisible(JFrame frame) {
        frame.setVisible(true);
    }

    private static Scene createScene() {
        VBox root = new VBox();
        root.setPadding(new Insets(5));
        Scene scene = new Scene(root);

        HBox hBox = new HBox();
        hBox.setPrefSize(30, 30);
        hBox.setMaxWidth(Region.USE_PREF_SIZE);
        hBox.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        Label label = new Label();

        scene.windowProperty().addListener((ob, oldWindow, newWindow) -> {
            newWindow.renderScaleXProperty().addListener((obs, oldValue, newValue) -> updateText(label, newValue));
            updateText(label, newWindow.getRenderScaleX());
            newWindow.renderScaleXProperty().addListener((s,p,c) -> {
               System.out.println("w=" + newWindow + " scale=" + c); 
            });
        });

        root.getChildren().addAll(hBox, label);

        return (scene);
    }

    private static void updateText(Label label, Number scaleX) {
        if (scaleX == null) {
            label.setText("Unknown scale x");
        } else {
            label.setText("O" + String.format("%.0f%%", scaleX.doubleValue() * 100));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EmbeddedFrameBug::initAndShowGUI);
    }
}