package goryachev.bugs;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8331319
 */
public class Ime_Popup_8331319 extends Application{

	enum Items {
		A,
		B,
		C
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		ChoiceBox<Items> choiceBox = new ChoiceBox<>();
		ComboBox<Items> comboBox = new ComboBox<>();
		MenuButton menuButton = new MenuButton();
		
		menuButton.getItems().addAll(new MenuItem("1"), new MenuItem("2"));
		choiceBox.getItems().addAll(Items.values());
		comboBox.getItems().addAll(Items.values());
		
		VBox root = new VBox(
			new Label("1. Select any of the following control items."),
			new HBox( 8, choiceBox, comboBox, menuButton),
			new Label("2. Entering text with the IME."),
			new HBox(8, new TextField(), new TextArea()));
		
		Scene scene = new Scene(root, 600, 600);
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}
}