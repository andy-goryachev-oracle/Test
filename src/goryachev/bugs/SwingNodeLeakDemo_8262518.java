package goryachev.bugs;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8262518
 */
public class SwingNodeLeakDemo_8262518 extends Application {

    private Collection<WeakReference<JPanel>> panels = new CopyOnWriteArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception {

        SwingNode node = new SwingNode();

        Pane root = new Pane();
        root.getChildren().add(node);

        //Kick off a thread that repeatedly creates new JPanels and resets the swing node's content
        new Thread(() -> {
            while (true) {
                //Lets throw in a little sleep so we can read the output
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                SwingUtilities.invokeLater(() -> {
                    JPanel panel = new JPanel();
                    panels.add(new WeakReference<>(panel));
                    node.setContent(panel);
                });

                System.out.println("Panels in memory: " + panels.stream().filter(ref -> ref.get() != null).count());

                //I know this doesn't guarantee anything, but prompting a GC gives me more confidence that this
                //truly is a bug.
                System.gc();
            }

        }).start();

        primaryStage.setScene(new Scene(root, 100, 100));

        primaryStage.show();
    }
}