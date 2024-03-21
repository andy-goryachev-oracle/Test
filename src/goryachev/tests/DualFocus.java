// Copyright © 2018-2019 Andy Goryachev <andy@goryachev.com>
// https://github.com/andy-goryachev/JavaBugs/blob/master/src/goryachev/bugs/fx/DualFocus.java
package goryachev.tests;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


/**
 * Dual Focus.
 * 
 * Start the application, press SPACE.  Notice that both the text field and the check box
 * have focus.  Pressing SPACE adds a space in the text field as well as toggles the check box.
 *  
 * Only one component is expected to have focus.
 */
public class DualFocus
	extends Application
{
	protected PopupControl popup;
	protected BorderPane popupBox;
	protected TextField textField;
	
	
//	public static void main(String[] args)
//	{
//		launch(args);
//	}
	
	
	public DualFocus()
	{
	}

	@Override
	public void start(Stage stage) throws Exception
	{
		textField = new TextField();
		textField.focusedProperty().addListener((s,p,c) -> handleFocus(c));
		
		TextArea textArea = new TextArea();
		textArea.setEditable(false);
		textArea.setText
		(
			"\n\n\n" +
			"1. Click on the text field.\n" +
			"2. Notice that both the text field and the check box have focus.\n" +
			"3. Press SPACE.  Notice both both text field and check box have the input focus.\n" +
			"\n" +
			"Only one component is expected to have focus."
		);
		
		BorderPane root = new BorderPane();
		root.setPrefSize(700, 300);
		root.setTop(textField);
		root.setCenter(textArea);
		
		Scene sc = new Scene(root);
		stage.setScene(sc);
		stage.setTitle("Dual Focus");
		stage.show();
	}
	
	protected void handleFocus(boolean on)
	{
		if(on)
		{
			showPopup();
		}
		else
		{
			hidePopup();
		}
	}
	
	
	protected void showPopup()
	{
		if(popup == null)
		{
			popupBox = new BorderPane();
			popupBox.setLeft(new CheckBox("why do both popup and text field have the input focus?"));
			popupBox.setStyle("-fx-background-color:red; -fx-background-radius:10; -fx-padding:10px;");
			
			popup = new PopupControl();
			popup.getScene().setRoot(popupBox);
			popup.setConsumeAutoHidingEvents(false);
			popup.setAutoFix(true);
			popup.setAutoHide(false);
			
			popupBox.applyCss();
			
			double dx = textField.getLayoutX();
			double dy = textField.getLayoutY() + textField.getHeight();
			
			Point2D p = textField.localToScreen(0, 0);
			popup.show(textField, p.getX() + dx, p.getY() + dy);
		}
	}
	
	
	protected void hidePopup()
	{
		if(popup != null)
		{
			popup.hide();
			popup = null;
		}
		
		if(popupBox != null)
		{
			popupBox = null;
		}
	}
}