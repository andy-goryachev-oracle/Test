package goryachev.bugs;

import java.io.IOException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8336590
 */
public class Button_Mnemonic_8336590 extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Button buttonNoGraphic = new Button("_Hello");
        buttonNoGraphic.setMnemonicParsing(true);
        
        Button buttonGraphic = new Button("_Hello");
        buttonGraphic.setMnemonicParsing(true);
        buttonGraphic.setGraphic(new Label());

        VBox layout = new VBox(
            buttonNoGraphic,
            buttonGraphic);
        
        Scene scene = new Scene(layout, 320, 240);
        stage.setTitle("Button_Mnemonic_8336590");
        stage.setScene(scene);
        stage.show();
    }
}