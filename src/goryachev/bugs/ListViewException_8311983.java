package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

// https://bugs.openjdk.org/browse/JDK-8311983
public class ListViewException_8311983 extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Image image1 = im(Color.GRAY);
        Image image2 = im(Color.LIGHTGRAY);

        TextArea prompt = new TextArea("a flower on Mars");
        Button button = new Button("Submit");
        ListView<Image> listView = new ListView<>();

        listView.getItems().addAll(image1, image2);
        listView.setCellFactory(lv -> {
            final ImageView imageView = new ImageView();

            return new ListCell<>() {
                protected void updateItem(Image image, boolean empty) {
                    super.updateItem(image, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        imageView.setImage(image);
                        setGraphic(imageView);
                    }
                }
            };
        });

        HBox hbox = new HBox() {
            {
                getChildren().addAll(
                    new VBox() {
                        {
                            getChildren().addAll(prompt, button);
                        }
                    },
                    listView);
            }
        };

        HBox.setHgrow(listView, Priority.ALWAYS);

        Scene scene = new Scene(hbox);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static Image im(Color color) {
        Canvas c = new Canvas(512, 512);
        GraphicsContext g = c.getGraphicsContext2D();
        g.setFill(color);
        g.fillRect(0, 0, c.getWidth(), c.getHeight());
        return c.snapshot(null, null);
    }
}