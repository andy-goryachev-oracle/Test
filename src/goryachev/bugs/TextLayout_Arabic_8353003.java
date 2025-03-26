package goryachev.bugs;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8353003
 */
public class TextLayout_Arabic_8353003 extends Application {
    
    private CheckBox rtl;
    private ComboBox<String> font;
    private Label label;
    private TextArea textArea;
    private TextField textField;

    @Override
    public void start(Stage stage) {
        
        String text = "السَّلَامُ عَلَيْكُمْ";
        
        rtl = new CheckBox("right to left");
        
        label = new Label(text);
        
        textArea = new TextArea(text);
        
        textField = new TextField(text);
        
        font = new ComboBox<>();
        font.getItems().setAll(Font.getFontNames());
        font.getSelectionModel().selectedItemProperty().addListener((p) -> {
            updateFont();
        });
        font.getSelectionModel().select("Noto Sans Arabic Regular");
        
        VBox p = new VBox(2);
        p.setPadding(new Insets(20));
        p.getChildren().setAll(
            font,
            label,
            textField,
            textArea,
            rtl);
        VBox.setVgrow(textArea, Priority.ALWAYS);
        
        updateFont();

        Scene scene = new Scene(p, 500, 500);
        rtl.selectedProperty().addListener((s,prev,on) -> {
            scene.setNodeOrientation(on ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
        });

        stage.setScene(scene);
        stage.setTitle(getClass().getSimpleName());
        stage.show();
    }
    
    private void updateFont() {
        String name = font.getSelectionModel().getSelectedItem();
        Font f = new Font(name, 36);
        label.setFont(f);
        textArea.setFont(f);
        textField.setFont(f);
    }
}