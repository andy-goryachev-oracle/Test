package goryachev.bugs;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableStringProperty;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.css.StyleableStringProperty;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;

// https://bugs.openjdk.org/browse/JDK-8389094
public class Css_Font_8389094 extends Application {
    public static class ProbeNode extends Text {
        private final CssMetaData<ProbeNode, String> propertyMetaData;
        private final StyleableStringProperty value;

        public ProbeNode(String cssPropertyName, String styleClass) {
            propertyMetaData = new StyleablePropertyFactory<ProbeNode>(Text.getClassCssMetaData()).createStringCssMetaData(cssPropertyName, n -> n.value, null);
            value = new SimpleStyleableStringProperty(propertyMetaData, this, "value", null);
            getStyleClass().add(styleClass);
        }

        public String getValue() {
            return value.get();
        }

        @Override
        public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
            return List.of(propertyMetaData);
        }
    }

    private static String buildInlineStylesheet(String css) {
        String encoded = Base64.getEncoder().encodeToString(css.getBytes(StandardCharsets.UTF_8));
        return "data:text/css;base64," + encoded;
    }

    @Override
    public void start(Stage stage) {
        Logger.getLogger("javafx.css").setLevel(Level.WARNING);
        ProbeNode fontProbe = new ProbeNode("-fx-foo-font", "font-probe-node");
        ProbeNode familyProbe = new ProbeNode("-fx-foo-family", "family-probe-node");
        Scene scene = new Scene(new Group(fontProbe, familyProbe), 200, 100);
        scene.getStylesheets().add(buildInlineStylesheet(".font-probe-node {\n -fx-foo-font: 'Some Value';\n}\n"));
        scene.getStylesheets().add(buildInlineStylesheet(".family-probe-node {\n -fx-foo-family: 'Some Value';\n}\n"));
        stage.setScene(scene);
        stage.show();
        fontProbe.applyCss();
        familyProbe.applyCss();
        Platform.runLater(() -> {
            System.out.println("-fx-foo-font -> " + fontProbe.getValue());
            System.out.println("-fx-foo-family -> " + familyProbe.getValue());
            Platform.exit();
        });
    }
}
