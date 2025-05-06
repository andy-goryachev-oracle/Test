package goryachev.bugs;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jfx.incubator.scene.control.richtext.CodeArea;
import jfx.incubator.scene.control.richtext.SyntaxDecorator;
import jfx.incubator.scene.control.richtext.TextPos;
import jfx.incubator.scene.control.richtext.model.CodeTextModel;
import jfx.incubator.scene.control.richtext.model.RichParagraph;

public class CodeArea_Backgrounds extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        String text = """
            Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt
            ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco
            laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit
            in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
            """;

        String css = """
                .test {
                    -fx-font-weight: bold;
                    -fx-fill: red;
                    -fx-background-color: green;
                }

            """;
        String data = "data:text/css;base64," + Base64.getEncoder().encodeToString(css.getBytes(StandardCharsets.UTF_8));

        CodeArea codeArea = new CodeArea();
        codeArea.getStylesheets().add(data);

        codeArea.setSyntaxDecorator(new SyntaxDecorator() {
            @Override
            public RichParagraph createRichParagraph(CodeTextModel model, int index) {
                var builder = RichParagraph.builder();
                // this will never work as text segments have no background property
                builder.addWithStyleNames(model.getPlainText(index), "test");
                return builder.build();
            }

            @Override
            public void handleChange(CodeTextModel m, TextPos start, TextPos end, int charsTop, int linesAdded,
                int charsBottom) {
            }
        });

        VBox.setVgrow(codeArea, Priority.ALWAYS);
        var button = new Button("Go!");
        button.setOnAction(e -> codeArea.setText(text));
        VBox root = new VBox(codeArea, button);
        Scene scene = new Scene(root, 600, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
