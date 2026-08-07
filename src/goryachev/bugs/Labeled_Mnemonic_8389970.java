package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// https://bugs.openjdk.org/browse/JDK-8389970
public class Labeled_Mnemonic_8389970 extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        CheckBox checkBox = new CheckBox("aaaaa_bbbbb");
        IO.println(checkBox.isMnemonicParsing());
        
        Button button = new Button("set \"\"");
        button.setOnAction(e -> checkBox.setText(""));
        stage.setScene(new Scene(new VBox(checkBox, button)));
        stage.show();
    }
}