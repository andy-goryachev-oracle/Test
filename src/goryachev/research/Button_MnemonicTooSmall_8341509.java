package goryachev.research;

import java.util.Arrays;
import java.util.List;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8341509
 */
public class Button_MnemonicTooSmall_8341509
    extends Application {
    private static boolean fixBug = false;

    @Override
    public void start(Stage stage) throws Exception {
        test1(stage);
    }

    void test1(Stage stage) {
        Scene scene;
        BorderPane pane;

        pane = new BorderPane();
        pane.setTop(getHBox());
        pane.setCenter(new Button("Press me"));
        scene = new Scene(pane);

        stage.setTitle("JDiskUsage");
        stage.setScene(scene);
        stage.show();
        stage.setHeight(300);
        stage.setWidth(400);
    }

    HBox getHBox() {
        HBox box;
        List<String> list;

        box = new HBox();

        list = Arrays.asList("/media/kees/CubeSSD/export/hoorn", "snapshot_001_2024_08_27__11_43_19", "usr", "local",
            "kees", "vault", "ECRYPTFS_FNEK_ENCRYPTED.FWahHL-b4aMey-Zzs7Wn0KzX2iNQZc.bhjjooW.7UlFj5o8ECnCeYAWQ5---",
            "ECRYPTFS_FNEK_ENCRYPTED.FWahHL-b4aMey-Zzs7Wn0KzX2iNQZc.bhjjoKjwSdLYOC3fNqbJM9WGZrk--",
            "ECRYPTFS_FNEK_ENCRYPTED.FWahHL-b4aMey-Zzs7Wn0KzX2iNQZc.bhjjooW.7UlFj5o8ECnCeYAWQ5---");

        list.forEach(s -> {
            box.getChildren().add(new MyButton(s));
        });

        return box;
    }

    private static class MyButton
        extends Button {
        public MyButton(String text) {
            super(text);

            System.out.println("mnemonic=" + isMnemonicParsing());
            if (fixBug) {
                setMnemonicParsing(false);
            }

            if ("snapshot_001_2024_08_27__11_43_19".equals(text)) {
                boundsInLocalProperty().addListener((o, oldValue, newValue) -> {
                    if (newValue.getHeight() > 100.0) {
                        System.out.println("minX=" + newValue.getMinY());
                        System.out.println(newValue + " : bounds of " + text);
                    }
                });
            }
        }
    }
}