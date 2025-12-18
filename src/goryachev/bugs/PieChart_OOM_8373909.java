package goryachev.bugs;

import java.util.Random;
import java.util.concurrent.Semaphore;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// no problem in PieChart
// https://bugs.openjdk.org/browse/JDK-8373908
public class PieChart_OOM_8373909 extends Application {
    @Override
    public void start(final Stage stage) throws InterruptedException {
        stage.setTitle(getClass().getSimpleName());

        PieChart chart = new PieChart();
        chart.setAnimated(false);

        stage.setScene(new Scene(new VBox(chart), 800, 400));
        stage.show();

        Thread modificationsThread = new Thread(() -> {
            final Semaphore semaphore = new Semaphore(1); // Just make sure to not overwhelm FX thread

            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                System.out.println(i + " Waiting to modify");
                try {
                    semaphore.acquire();
                } catch (final InterruptedException e) {
                    return;
                }

                Platform.runLater(() -> {
                    chart.getData().clear();
                    Random r = new Random();
                    for(int j=0; j<20; j++) {
                        int v = r.nextInt(100);
                        chart.getData().add(new Data(String.valueOf(v), v));
                    }

                    semaphore.release();
                });
            }
        }, "modify_dataset_thread");

        modificationsThread.setDaemon(true);
        modificationsThread.start();
    }
}