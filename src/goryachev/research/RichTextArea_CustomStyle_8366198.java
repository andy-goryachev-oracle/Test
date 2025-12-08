package goryachev.research;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import jfx.incubator.scene.control.richtext.RichTextArea;
import jfx.incubator.scene.control.richtext.StyleHandlerRegistry;
import jfx.incubator.scene.control.richtext.TextPos;
import jfx.incubator.scene.control.richtext.model.StyleAttribute;
import jfx.incubator.scene.control.richtext.model.StyleAttributeMap;

/**
 * https://bugs.openjdk.org/browse/JDK-8366198
 * 
 * The idea to add a custom attribute does not work,
 * because one highlight might be applied to more than one segment,
 * and we do not want (or can't) concatenate some decorations
 * (e.g. the phase of squiggly lines). 
 */
public class RichTextArea_CustomStyle_8366198 extends Application {
    public static final StyleAttribute<Boolean> RED_SQUIGGLY = new StyleAttribute<>("RED_SQUIGGLY", Boolean.class, false);
    public static final StyleAttribute<Boolean> BLUE_SQUIGGLY = new StyleAttribute<>("BLUE_SQUIGGLY", Boolean.class, false);
    
    @Override
    public void start(Stage stage) throws Exception {
        RichTextArea r = new RichTextArea() {
            private static final StyleHandlerRegistry registry = init();

            private static StyleHandlerRegistry init() {
                // brings in the handlers from the base class
                StyleHandlerRegistry.Builder b = StyleHandlerRegistry.builder(RichTextArea.styleHandlerRegistry);
                // adds a handler for the new attribute
                b.setSegHandler(RED_SQUIGGLY, (c, cx, v) -> {
                    if (v) {
                        // FIX and here we have a problem:
                        // attribute handlers work on text segments, but the highlights might be applied to several segments
                        cx.addStyle("-fx-fill: red;");
                    }
                });
                b.setSegHandler(BLUE_SQUIGGLY, (c, cx, v) -> {
                    if (v) {
                        cx.addStyle("-fx-fill: blue;");
                    }
                });
                return b.build();
            }

            @Override
            public StyleHandlerRegistry getStyleHandlerRegistry() {
                return registry;
            }
        };

        r.appendText("""
            red squiggly
            both
            blue squiggly
            none
            """);
        r.select(TextPos.ofLeading(0, 1));
        r.applyStyle(TextPos.ZERO, TextPos.ofLeading(1, 100), StyleAttributeMap.of(RED_SQUIGGLY, true));
        r.applyStyle(TextPos.ofLeading(1, 0), TextPos.ofLeading(2, 100), StyleAttributeMap.of(BLUE_SQUIGGLY, true));

        BorderPane pane = new BorderPane();
        pane.setCenter(r);
        stage.setScene(new Scene(pane));
        stage.show();
    }
}