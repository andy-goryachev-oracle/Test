package goryachev.bugs;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.control.skin.TextAreaSkin;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Utils_ThreadUnsafe_8347392 extends Application {
    private static final int THREAD_COUNT = 100;
    private static final int DURATION = 5000;
    private static final AtomicLong seq = new AtomicLong();
    
    static record Param<T extends Node>(Supplier<T> generator, Consumer<T> operation) { }

    private Param[] params() {
        return new Param[] {
            new Param<DatePicker>(() -> {
                DatePicker c = new DatePicker();
                c.setSkin(new DatePickerSkin(c));
                return c;
            }, (c) -> {
                c.show(); // fails here
                c.setValue(LocalDate.now());
                c.prefHeight(-1);
                c.setValue(LocalDate.EPOCH);
                c.prefWidth(-1);
            }),

            new Param<TextArea>(() -> {
                TextArea c = new TextArea();
                c.setSkin(new TextAreaSkin(c));
                return c;
            }, (c) -> {
                c.setText(nextString());
                c.prefHeight(-1);
                c.setText(null);
                c.prefWidth(-1);
            }),

            new Param<TextField>(() -> {
                TextField c = new TextField();
                c.setSkin(new TextFieldSkin(c));
                return c;
            }, (c) -> {
                // TODO could not get it to fail
                c.setPrefWidth(20);
                c.setPromptText("yo");
                c.setText(nextString());
                c.prefHeight(-1);
            }),

            new Param<PasswordField>(() -> {
                PasswordField c = new PasswordField();
                c.setSkin(new TextFieldSkin(c));
                return c;
            }, (c) -> {
                // TODO could not get it to fail
                c.setPrefWidth(20);
                c.setPromptText("yo");
                c.setText(nextString());
                c.prefHeight(-1);
            })
        };
    }

    @Override
    public void start(Stage stage) {
        BorderPane bp = new BorderPane();
        Scene scene = new Scene(bp, 300, 250);
        stage.setScene(scene);
        stage.show();
        
        new Thread(() -> {
            cycle(stage, bp);
            Platform.exit();
            System.exit(0);
        }).start();
    }
    
    private void cycle(Stage stage, BorderPane bp) {
        for(Param<Node> p: params()) {
            Node n = p.generator().get();
            
            Platform.runLater(() -> {
                bp.setCenter(n);
                stage.setTitle(n.getClass().getSimpleName());
            });

            exercise(p, n);
        }
    }
    
    private void exercise(Param<Node> p, Node inScene) {
        AtomicBoolean running = new AtomicBoolean(true);

        try {
            for (int i = 0; i < THREAD_COUNT; i++) {
                new Thread() {
                    @Override
                    public void run() {
                        Node n = p.generator().get();
                        int count = 0;
                        while (running.get()) {
                            p.operation().accept(n);

                            count++;
                            if ((count % 100) == 0) {
                                Platform.runLater(() -> {
                                    p.operation().accept(inScene);
                                });
                            }
                        }
                    }
                }.start();
            }

            Thread.sleep(DURATION);

        } catch (InterruptedException ignore) {
        } finally {
            running.set(false);
        }

        // allow them to finish
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
        }
    }

    private static void access(TextArea t) {
        String s = t.getText();
        if (s == null || s.length() == 0) {
            s = "_A" + seq.incrementAndGet();
        } else {
            s = null;
        }
        t.setText(s);
        t.setPromptText("p" + seq.incrementAndGet());
        t.getBaselineOffset();
        t.prefHeight(-1);
        t.prefWidth(-1);
    }

//    private static void access(DatePicker p) {
//        LocalDate v;
//        if (p.getValue() == LocalDate.EPOCH) {
//            v = LocalDate.now();
//        } else {
//            v = LocalDate.EPOCH;
//        }
//        p.setValue(v);
//    }

//    private void update(TextArea t) {
//        Platform.runLater(() -> {
//            access(t);
//            update(t);
//        });
//    }

    private String nextString() {
        return "_a" + seq.incrementAndGet();
    }
}
