package goryachev.bugs;

import javafx.application.Application;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8317747
 */
public class ChartBugDemo_8317747 extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        LineChart<Number, Number> chart = new LineChart<>(new NumberAxis(), new NumberAxis());
        chart.setAnimated(false);
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        ObservableList<XYChart.Data<Number, Number>> dataList = FXCollections
            .observableArrayList(data -> new Observable[] { data.XValueProperty() });
        series.setData(dataList);
        chart.getData().add(series);

        XYChart.Data<Number, Number> left = new XYChart.Data<>(1, 3);
        XYChart.Data<Number, Number> middle = new XYChart.Data<>(10, 5);
        XYChart.Data<Number, Number> right = new XYChart.Data<>(19, 3);
        dataList.addAll(left, middle, right);

        Button moveRight = new Button("Move Right");
        moveRight.setOnAction(e -> middle.setXValue(middle.getXValue().doubleValue() + 1));
        BorderPane root = new BorderPane();
        root.setCenter(chart);
        root.setTop(moveRight);
        BorderPane.setAlignment(moveRight, Pos.CENTER);
        BorderPane.setMargin(moveRight, new Insets(5));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}