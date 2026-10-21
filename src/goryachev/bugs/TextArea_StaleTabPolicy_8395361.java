package goryachev.bugs;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TextArea_StaleTabPolicy_8395361 extends Application {

    @Override
    public void start(Stage stage) {
        String text =
            """
            Tab-----|
            \tT
                    8 spaces
            --------|
            """;
        TextArea textArea = new TextArea();
        textArea.setFont(Font.font("Monospaced"));
        textArea.setText(text);
        textArea.setWrapText(true);
        stage.setScene(new Scene(textArea, 400, 200));
        stage.show();

        Text content = (Text) textArea.lookup(".text");
        String s = content.getText();
        int tabIndex = s.indexOf('\t');
        int spacesIndex = s.indexOf("        8");
        double tab = caretX(content, tabIndex + 1) - caretX(content, tabIndex);
        double space = (caretX(content, spacesIndex + 8) - caretX(content, spacesIndex)) / 8;
        System.out.printf("tab = %.2f px, space = %.2f px -> tab = %.2f spaces%n", tab, space, tab / space);
    }

    private static double caretX(Text text, int index) {
        return text.getLayoutInfo().caretInfoAt(index, true).getSegmentAt(0).getMinX();
    }
}