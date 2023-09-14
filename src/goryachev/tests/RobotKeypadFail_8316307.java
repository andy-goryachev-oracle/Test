package goryachev.tests;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.KP_DOWN;
import static javafx.scene.input.KeyCode.KP_LEFT;
import static javafx.scene.input.KeyCode.KP_RIGHT;
import static javafx.scene.input.KeyCode.KP_UP;
import static javafx.scene.input.KeyCode.LEFT;
import static javafx.scene.input.KeyCode.RIGHT;
import static javafx.scene.input.KeyCode.UP;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;

/**
 * Robot fails to send keypad keys (at least on macOS).
 * https://bugs.openjdk.org/browse/JDK-8316307
 */
public class RobotKeypadFail_8316307 extends Application {

    Robot robot;
    TextField control;

    @Override
    public void start(Stage stage) {
        robot = new Robot();
        control = new TextField();
        
        Scene scene = new Scene(control, 300, 200);

        stage.setScene(scene);
        stage.show();
        
        control.requestFocus();
        
        new Thread() {
            @Override
            public void run() {
                t(LEFT);
                t(KP_LEFT); // FIX fails
                t(KP_RIGHT); // FIX fails
                t(KP_DOWN); // FIX fails
                t(KP_UP); // FIX fails
                t(RIGHT);
                t(DOWN);
                t(UP);
                t(null);
            }
        }.start();
    }

    private void t(KeyCode k) {
        if (k == null) {
            System.exit(0);
        }

        AtomicReference<KeyCode> ref = new AtomicReference<>();
        EventHandler<KeyEvent> li = (ev) -> {
            KeyCode v = ev.getCode();
            ref.set(v);
        };

        Platform.runLater(() -> {
            control.addEventFilter(KeyEvent.KEY_PRESSED, li);

            robot.keyPress(k);
            robot.keyRelease(k);
        });

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        Platform.runLater(() -> {
            control.removeEventFilter(KeyEvent.KEY_PRESSED, li);
            Object received = ref.get();
            System.err.println("keycode=" + k + " key.press=" + received);
        });
    }

//    public static void main(String[] args) throws Throwable {
//        Application.launch(RobotKeypadFail_8316307.class, args);
//    }
}