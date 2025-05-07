package goryachev.bugs;

import java.lang.reflect.Method;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Yep, -add-opens opens up even private methods.
 *
 * --add-exports javafx.graphics/com.sun.javafx.application.preferences=andy_test
 * --add-opens javafx.graphics/com.sun.javafx.application.preferences=andy_test
 */
public class PlatformPreferences_Private extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Label t = new Label("100");
        t.setOpacity(1.0);
        t.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
        
        Scene scene = new Scene(t, 300, 250);
        stage.setScene(scene);
        stage.show();

        var platformPreferences = Platform.getPreferences();
        var colorSchemeProperty = platformPreferences.colorSchemeProperty();
        Method m = colorSchemeProperty.getClass().getMethod("updateEffectiveValue");
        m.invoke(colorSchemeProperty);
    }
}
