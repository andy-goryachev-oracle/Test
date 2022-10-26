package goryachev.apps;
import java.util.Random;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.stage.Stage;

public class LineChartDemo extends Application {
    @Override
    public void start(Stage stage) {
        XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
        series.setName("Random Walk");
        generateData(series);

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Sequence");

        NumberAxis yAxis = new NumberAxis();

        LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);
        lineChart.setTitle("Line Chart Demo");
        lineChart.getData().add(series);
        
        Scene scene = new Scene(lineChart, 1000, 700);

        stage.setScene(scene);
        stage.show();
    }

    private void generateData(Series<Number, Number> series) {
        Random r = new Random();
        int y = 0;
        for(int i=0; i<10_000; i++) {
            y += (r.nextInt(11) - 5);
            series.getData().add(new XYChart.Data<Number, Number>(i, y));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}