package goryachev.research;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import jfx.incubator.scene.control.richtext.RichTextArea;
import jfx.incubator.scene.control.richtext.TextPos;
import jfx.incubator.scene.control.richtext.model.RichParagraph;
import jfx.incubator.scene.control.richtext.model.RichTextModel;
import jfx.incubator.scene.control.richtext.model.StyleAttribute;
import jfx.incubator.scene.control.richtext.model.StyleAttributeMap;

/**
 * https://bugs.openjdk.org/browse/JDK-8366198
 */
public class RichTextArea_CustomModel_8366198 extends Application {
    public static final StyleAttribute<Boolean> RED_SQUIGGLY = new StyleAttribute<>("RED_SQUIGGLY", Boolean.class, false);
    public static final StyleAttribute<Boolean> BLUE_SQUIGGLY = new StyleAttribute<>("BLUE_SQUIGGLY", Boolean.class, false);
    
    @Override
    public void start(Stage stage) throws Exception {
        
        RichTextModel m = new RichTextModel() {
            @Override
            public RichParagraph getParagraph(int index) {
                RichParagraph p = super.getParagraph(index);
                applyCustomHighlights(p);
                 return p;
            }
            
            // so here we could iterate over text segments looking for the special attributes (expensive!)
            // to determine the start/end positions, then add highlights to the paragraph.
            private void applyCustomHighlights(RichParagraph p) {
                // TODO needs JDK-8371070
            }
        };
        
        RichTextArea r = new RichTextArea(m);

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