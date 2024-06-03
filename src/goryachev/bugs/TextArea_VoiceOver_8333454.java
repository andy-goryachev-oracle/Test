package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8333454
 */
public class TextArea_VoiceOver_8333454 extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setFont(Font.getDefault().font(24.0));
        textArea.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Pellentesque habitant morbi tristique senectus et netus et malesuada. Nunc eget lorem dolor sed viverra ipsum. Vulputate enim nulla aliquet porttitor lacus. Platea dictumst quisque sagittis purus sit amet volutpat consequat. Vestibulum rhoncus est pellentesque elit ullamcorper dignissim cras tincidunt. Turpis egestas pretium aenean pharetra magna. Ultricies mi quis hendrerit dolor magna eget est lorem ipsum. Vitae et leo duis ut diam quam. Tincidunt nunc pulvinar sapien et. Laoreet sit amet cursus sit amet dictum sit amet.");

        BorderPane bp = new BorderPane();
        bp.setCenter(textArea);

        Scene scene = new Scene(bp);

        stage.setScene(scene);
        stage.setTitle("JDK-8333454");
        stage.show();
    }
}