package goryachev.bugs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jfx.incubator.scene.control.richtext.CodeArea;
import jfx.incubator.scene.control.richtext.SyntaxDecorator;
import jfx.incubator.scene.control.richtext.TextPos;
import jfx.incubator.scene.control.richtext.model.CodeTextModel;
import jfx.incubator.scene.control.richtext.model.RichParagraph;

public class CodeArea_Slow extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        CodeArea codeArea = new CodeArea();
        codeArea.setLineNumbersEnabled(true);
        final List<Integer> requestedIndexes = new ArrayList<>();
        codeArea.setSyntaxDecorator(new SyntaxDecorator() {
            @Override
            public RichParagraph createRichParagraph(CodeTextModel ctm, int i) {
                requestedIndexes.add(i);
                RichParagraph.Builder b = RichParagraph.builder();
                b.addSegment(ctm.getPlainText(i));
                return b.build();
            }

            @Override
            public void handleChange(CodeTextModel ctm, TextPos tp, TextPos tp1, int i, int i1, int i2) {

            }
        });
        StringBuilder sb = new StringBuilder();
        for (var i = 0; i < 1000; i++) {
            sb.append(i);
            sb.append("\n");
        }
        codeArea.setText(sb.toString());

        var reportButton = new Button("Report & Clear");
        reportButton.setOnAction(e -> {
            Collections.sort(requestedIndexes);
            System.out.println("Created/Updated " + requestedIndexes.size() + " paragraphs: " + requestedIndexes);
            requestedIndexes.clear();
        });
        
        var topButton = new Button("To Top");
        topButton.setOnAction(e -> codeArea.select(new TextPos(0, 0, 0, true)));
        
        var bottomButton = new Button("To Bottom");
        bottomButton.setOnAction(e -> codeArea.select(new TextPos(999, 0, 0, true)));
        
        var eventButton = new Button("Fire Event");
        eventButton.setOnAction(e -> codeArea.getModel().fireStyleChangeEvent(new TextPos(0, 0, 0, true), new TextPos(15, 0, 0, true)));
        HBox buttonBox = new HBox(reportButton, topButton, bottomButton, eventButton);

        VBox.setVgrow(codeArea, Priority.ALWAYS);
        VBox root = new VBox(codeArea, buttonBox);
        Scene scene = new Scene(root, 600, 200);
        stage.setScene(scene);
        stage.show();
    }
}