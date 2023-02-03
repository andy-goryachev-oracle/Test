package goryachev.apps;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

// https://bugs.openjdk.org/browse/JDK-8301742
public class PaginationDisappear extends Application {

    private boolean disappear = !false;

    @Override
    public void start(Stage stage) throws Exception {
        Pagination pagination = new Pagination(15);
        pagination.setMaxPageIndicatorCount(5);
        Label label = new Label();
        pagination.setPageFactory(index -> {
            label.setText("" + (index + 1));
            try {
                if (index != 0)
                    start(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return label;
        });
        Parent parent = pagination;
        if (!disappear)
            parent = new HBox(pagination);
        stage.setScene(new Scene(parent));
        stage.show();
        stage.setMinHeight(300);
        stage.setMinWidth(300);
    }

    public static void main(String[] args) {
        Application.launch(PaginationDisappear.class);
    }
}