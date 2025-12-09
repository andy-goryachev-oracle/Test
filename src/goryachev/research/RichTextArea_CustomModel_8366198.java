package goryachev.research;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import jfx.incubator.scene.control.richtext.RichTextArea;
import jfx.incubator.scene.control.richtext.TextPos;
import jfx.incubator.scene.control.richtext.model.RichParagraph;
import jfx.incubator.scene.control.richtext.model.RichTextModel;
import jfx.incubator.scene.control.richtext.model.StyleAttribute;
import jfx.incubator.scene.control.richtext.model.StyleAttributeMap;
import jfx.incubator.scene.control.richtext.model.StyledSegment;

/**
 * https://bugs.openjdk.org/browse/JDK-8366198
 */
public class RichTextArea_CustomModel_8366198 extends Application {
    private static final StyleAttribute<Boolean> RED_SQUIGGLY = new StyleAttribute<>("RED_SQUIGGLY", Boolean.class, false);
    private static final StyleAttribute<Boolean> BLUE_SQUIGGLY = new StyleAttribute<>("BLUE_SQUIGGLY", Boolean.class, false);
    private static final Color RED = Color.RED.deriveColor(0.0, 1.0, 1.0, 0.5);
    private static final Color BLUE = Color.BLUE.deriveColor(0.0, 1.0, 1.0, 0.3);
    
    @Override
    public void start(Stage stage) throws Exception {
        
        RichTextModel m = new RichTextModel() {
            @Override
            protected RichParagraph.Builder buildParagraph(int index) {
                RichParagraph.Builder b = super.buildParagraph(index);
                applyCustomHighlights(b);
                return b;
            }
            
            // so here we could iterate over text segments looking for the special attributes (expensive!)
            // to determine the start/end positions, then add highlights to the paragraph.
            // the main problem here is that style attributes are specific to segments, and the highlights
            // might span segments or be applied to partial segments (that is, they are conceptually separate
            // from the segments).
            private void applyCustomHighlights(RichParagraph.Builder b) {
                int red = -1;
                int blue = -1;
                int ix = 0;
                int ct = b.getSegmentCount();
                for (int i = 0; i < ct; i++) {
                    // NOTE: requires new APIs in RichParagraph.Builder
                    StyledSegment seg = b.getSegment(i);
                    // style resolver is not needed at this point
                    StyleAttributeMap a = seg.getStyleAttributeMap(null);
                    if (a.contains(RED_SQUIGGLY)) {
                        if (red < 0) {
                            red = ix;
                        }
                    } else {
                        if (red >= 0) {
                            b.addWavyUnderline(red, ix, RED);
                            red = -1;
                        }
                    }
                    if (a.contains(BLUE_SQUIGGLY)) {
                        if (blue < 0) {
                            blue = ix;
                        }
                    } else {
                        if (blue >= 0) {
                            b.addWavyUnderline(blue, ix, BLUE);
                            blue = -1;
                        }
                    }
                    ix += seg.getText().length();
                }
                if (red >= 0) {
                    b.addWavyUnderline(red, ix, RED);
                }
                if (blue >= 0) {
                    b.addWavyUnderline(blue, ix, BLUE);
                }
            }
        };
        
        RichTextArea r = new RichTextArea(m);
        r.setWrapText(true);

        r.appendText("""
            red squiggly
            both
            blue squiggly
            none
            """);
        r.select(TextPos.ofLeading(0, 1));
        r.applyStyle(TextPos.ZERO, TextPos.ofLeading(1, 100), StyleAttributeMap.of(RED_SQUIGGLY, true));
        r.applyStyle(TextPos.ofLeading(0, 6), TextPos.ofLeading(0, 100), StyleAttributeMap.of(StyleAttributeMap.BOLD, true));
        r.applyStyle(TextPos.ofLeading(1, 0), TextPos.ofLeading(2, 100), StyleAttributeMap.of(BLUE_SQUIGGLY, true));

        BorderPane pane = new BorderPane();
        pane.setCenter(r);
        stage.setScene(new Scene(pane));
        stage.setTitle("Better Highlights APIs");
        stage.show();
    }
}