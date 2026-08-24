package goryachev.bugs;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jfx.incubator.scene.control.richtext.RichTextArea;
import jfx.incubator.scene.control.richtext.TextPos;
import jfx.incubator.scene.control.richtext.model.RichTextModel;
import jfx.incubator.scene.control.richtext.model.StyleAttributeMap;

// https://bugs.openjdk.org/browse/JDK-8390913
public class RichTextArea_DownArrow_8390913 extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        RichTextModel model = new RichTextModel();
        StyleAttributeMap headerMap = StyleAttributeMap.builder()
                .setFontSize(20.0).setBold(true)
                .setSpaceAbove(12.0).setSpaceBelow(8.0)
                .build();
        RichTextArea richTextArea = new RichTextArea(model);
        richTextArea.appendText("This is the heading\n");
        richTextArea.appendText("This is some text.");
        richTextArea.applyStyle(TextPos.ofLeading(0, 0), richTextArea.getParagraphEnd(0), headerMap);

        Scene scene = new Scene(richTextArea, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}