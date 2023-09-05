package goryachev.bugs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// https://bugs.openjdk.org/browse/JDK-8283675
public class LineChartClearIssue_8283675 extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        LineChart<Number, Number> lineChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        lineChart.setAnimated(false);
        Series<Number, Number> series = new Series<>();
        series.setName("Dave");
        lineChart.getData().add(series);

        Button add = new Button("Add");
        add.setOnAction((ev) -> {
            var d = series.getData();
            d.add(new Data<>(d.size(), 20 * new Random().nextInt(20)));
        });

        Button remove = new Button("Remove");
        remove.setOnAction((ev) -> {
            var d = series.getData();
            int sz = d.size();
            if (sz > 0) {
                d.remove(sz - 1);
            }
        });

        Button clear = new Button("Clear");
        clear.setOnAction((ev) -> {
            series.getData().clear();
        });

        Button workaround = new Button("Workaround");
        workaround.setOnAction((ev) -> {
            // copy data and add again
            List<Series<Number, Number>> rigby = new ArrayList<>(lineChart.getData());
            lineChart.getData().setAll(rigby);
        });

        stage.setScene(new Scene(new VBox(
            add,
            remove,
            clear,
            workaround,
            lineChart
        )));
        stage.show();
    }
}