package goryachev.bugs;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8283449
 */
public class TabPane_MemoryLeak_8283449 extends Application {

    private static final int LARGE_MEM_BYTES = 50_000_000;

    @Override
    public void start(Stage primaryStage) {
        TabPane tabPane = new TabPane();

        Scene scene = new Scene(tabPane);
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(300);
        primaryStage.setMinWidth(300);

        primaryStage.show();

        new Thread(() -> {
            for (int i = 0; i < 4; i++) {
                runGc();
                printMem("without tab");

                Platform.runLater(() -> {
                    addTab(tabPane);
                });

                runGc();
                printMem("with tab, after gc");

                Platform.runLater(() -> {
                    tabPane.getTabs().remove(0);
                    System.out.println("Tab removed");
                });

                // wait for tab removal
                while (!isEmpty(tabPane)) {
                    sleep(200);
                }
                runGc();
                printMem("removed tab, after gc");
            }
            //Platform.exit();
        }).start();
    }

    boolean isEmpty(TabPane t) {
        AtomicBoolean rv = new AtomicBoolean();
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            rv.set(t.getTabs().isEmpty());
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return rv.get();
    }

    public void addTab(TabPane tabPane) {
        Tab tab = new Tab("HeavyTab");
        tab.setUserData(new byte[LARGE_MEM_BYTES]);
        tab.setContextMenu(new ContextMenu());

        // reference to tab in MenuItem (onAction)
        MenuItem menuItemWithReferenceToTab = new MenuItem("RenameTabMenuItem");
        
        // FIX this line causes memory leak
        menuItemWithReferenceToTab.setOnAction(e -> tab.setText("tab renamed"));
        
        tab.getContextMenu().getItems().add(menuItemWithReferenceToTab);

        tabPane.getTabs().add(tab);
    }

    public static void runGc() {
        for (int i = 0; i < 10; i++) {
            System.gc();
            sleep(200);
        }
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            e.printStackTrace(); // ignore
        }
    }

    public static void printMem(String message) {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory() / 1024 / 1024;
        long freeMemory = runtime.freeMemory() / 1024 / 1024;
        System.out.println(message + "\t - used mem: " + (totalMemory - freeMemory) + " MB");
    }
}