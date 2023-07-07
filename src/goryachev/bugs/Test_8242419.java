package goryachev.bugs;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Test_8242419 {
    public static void main(String[] args) { SwingUtilities.invokeLater(Test_8242419::initAndShowGUI); }

    private static void initAndShowGUI() {
        // Setup the JFXPanel inside a JPanel
        JFrame frame = new JFrame();
        final JFXPanel fxPanel = new JFXPanel();
        frame.add(fxPanel);
        frame.setSize(300, 200);
        frame.setVisible(true);

        Platform.runLater(() -> initFX(fxPanel));
    }

    private static void initFX(JFXPanel fxPanel) {
        Group root = new Group();
        Scene scene = new Scene(root);

        // The event handler that incorrectly returns the button held instead of the one clicked
        scene.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> System.out.println("Button clicked: " + event.getButton().name()));
        scene.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> System.out.println("Button pressed: " + event.getButton().name()));
        scene.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> System.out.println("Button released: " + event.getButton().name()));
        
        fxPanel.setScene(scene);
    }
}