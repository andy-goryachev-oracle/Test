package goryachev.bugs;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.stage.PopupWindow.AnchorLocation;
import javafx.stage.Stage;

public class ToolTipIssue_8315645 extends Application {

   // boilerplate to build a button, add it to a stage, and show stage
//   public static void main(String[] args) {
//      launch(args);
//   }

   @Override
   public void start(Stage primaryStage) {
      BorderPane root = new BorderPane();
      Button button = new Button();
      BorderPane.setAlignment(button, Pos.CENTER_RIGHT);
      button.textProperty().bind(Bindings.when(button.hoverProperty()).then("HOVERED Button")
            .otherwise("Regular Button"));
      Tooltip tip = new Tooltip("I am a button's tooltip.");
      
      //tip.setAnchorLocation(AnchorLocation.WINDOW_BOTTOM_LEFT);
      tip.setAnchorLocation(AnchorLocation.WINDOW_BOTTOM_RIGHT);
      button.setTooltip(tip);
      root.setRight(button);
      primaryStage.setScene(new Scene(root, 450, 150));
      primaryStage.show();
   }
}