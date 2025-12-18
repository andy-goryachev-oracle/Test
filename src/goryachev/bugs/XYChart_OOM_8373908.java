package goryachev.bugs;

import java.util.concurrent.Semaphore;
import java.util.stream.IntStream;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * <pre>
 * Run with: java -Xmx200m -XX:+HeapDumpOnOutOfMemoryError
 *
 * Accumulates com.sun.javafx.binding.FlatMappedBinding objects.
 *
 * Tested JDK 17, JDK 21
 *
 * 20.0.2 ok
 * 21.0.7 fail
 * 21.0.9 fail
 * 23.0.2 fail
 *
 * Supposedly relevant commit
 * 8298382: JavaFX ChartArea Accessibility Reader
 * https://github.com/openjdk/jfx/commit/33f1f629c5df9f8e03e81e360730536cde0a8f53
 * </pre>
 *
 * @author Benjamin Peter
 */
// https://bugs.openjdk.org/browse/JDK-8373908
public class XYChart_OOM_8373908 extends Application {

    // Set to true to implement a (bad) workaround
    private static final boolean WITH_WORKAROUND = false;

    // Exact values below are NOT relevant for the leak
    private static final int SLEEP_BEFORE_START_MS = 100;
    private static final int BATCH_SIZE = 20;
    private static final int Y_UPPER_BOUND = 10;

    @Override
    public void start(final Stage primaryStage) throws InterruptedException {
        System.out.println("Initializing");

        primaryStage.setTitle("XY Chart OOM - bpeter");

        // RELEVANT: Using a chart type that creates a node per data item, like ScatterChart!
        final ScatterChart<Number, Number> chart = new ScatterChart<>(
            new NumberAxis(),
            new NumberAxis(0, Y_UPPER_BOUND, 1));
        chart.setAnimated(false);
        chart.getData().add(createNewSeries());

        primaryStage.setScene(new Scene(new VBox(chart), 800, 400));

        primaryStage.show();

        System.out.println("Initialized UI");

        final Thread modificationsThread = new Thread(() -> {
            final Semaphore semaphore = new Semaphore(1); // Just make sure to not overwhelm FX thread

            System.out.println(
                "Starting to modify data set in " + SLEEP_BEFORE_START_MS + " ms. Please prepare vm monitoring.");
            try {
                Thread.sleep(SLEEP_BEFORE_START_MS);
            } catch (final InterruptedException e) {
                System.err.println("Interrupted");
                return;
            }

            System.out.println("Starting");

            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                final int ii = i; // Must be final for use in lambda
                System.out.println(ii + " Waiting to modify");
                try {
                    semaphore.acquire();
                } catch (final InterruptedException e) {
                    System.err.println(ii + "Interrupted");
                    return;
                }

                System.out.println(ii + " Going to modifying");

                Platform.runLater(() -> {
                    final var data = chart.getData().get(0).getData();

                    /* RELEVANT: Removed data items do not cause listeners from XYChart (ScatterChart) to be removed. See
                     *
                     * https://github.com/openjdk/jfx/blob/7e2c0d435a46cd4df9fa8d215f2a943d21cab7d9/modules/javafx.controls/src/main/java/javafx/scene/chart/XYChart.java#L1370
                     */
                    data.clear();

                    IntStream.range(0, BATCH_SIZE) //
                        .mapToObj(di -> new Data<Number, Number>(di, di / (double)BATCH_SIZE * Y_UPPER_BOUND)) //
                        .forEach(d -> data.add(d));

                    System.out.println(ii + " Modification done, data set size: " + data.size());

                    /* WORKAROUND: replace series to free listeners that are still bound despite all data being removed */
                    if (WITH_WORKAROUND) {
                        final var series = chart.getData().get(0);
                        final var seriesNew = createNewSeries();
                        seriesNew.setData(series.getData());
                        chart.getData().set(0, seriesNew);
                    }

                    semaphore.release();
                });
            }
        }, "modify_dataset_thread");

        modificationsThread.setDaemon(true);
        modificationsThread.start();
    }

    private final static XYChart.Series<Number, Number> createNewSeries() {
        final XYChart.Series<Number, Number> chainStateSeries = new XYChart.Series<>();
        chainStateSeries.setName("Data series");
        return chainStateSeries;
    }
}