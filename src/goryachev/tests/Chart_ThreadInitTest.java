package goryachev.tests;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Chart_ThreadInitTest extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        var ch = createChart();
        BorderPane bp = new BorderPane(ch);
        stage.setTitle("Chart Init in Background Thread");
        stage.setScene(new Scene(bp, 500, 300));
        stage.show();

        new Thread(() -> {
            var chart = createChart();
            Platform.runLater(() -> {
                bp.setCenter(chart);
            });
        }).start();
    }

    private LineChart<Number, Number> createChart() {
        LineChart<Number, Number> chart = new LineChart<>(new NumberAxis(), new NumberAxis());
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        ObservableList<XYChart.Data<Number, Number>> data = FXCollections.observableArrayList((d) -> {
            return new Observable[] { d.XValueProperty() };
        });
        series.setData(data);
        chart.getData().add(series);

        XYChart.Data<Number, Number> left = new XYChart.Data<>(1, 3);
        XYChart.Data<Number, Number> middle = new XYChart.Data<>(10, 5);
        XYChart.Data<Number, Number> right = new XYChart.Data<>(19, 3);
        data.addAll(left, middle, right);
        return chart;
    }
}