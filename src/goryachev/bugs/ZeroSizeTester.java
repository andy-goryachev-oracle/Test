package goryachev.bugs;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

public class ZeroSizeTester
{
   private JFrame frame;

   public static void main(String[] args)
   {
      new ZeroSizeTester().init();
   }

   private void init()
   {
      JFXPanel fxPanel = new JFXPanel();
      frame = new JFrame();
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

      JButton button = new JButton("Click");
      button.addActionListener(e ->
                               {
                                  frame.remove(button);
                                  Platform.runLater(() -> initFX(fxPanel));
                               });

      frame.add(button);
      frame.pack();
      frame.setSize(200, 200);
      frame.setVisible(true);
   }

   private void initFX(JFXPanel fxPanel)
   {
      Scene scene = createScene();
      fxPanel.setScene(scene);
      SwingUtilities.invokeLater(() ->
                                 {
                                    frame.add(fxPanel);
                                    frame.revalidate();
                                 });
   }

   private Scene createScene()
   {
      TextArea textArea = new TextArea();
      textArea.setMaxWidth(Integer.MAX_VALUE);
      textArea.widthProperty().addListener((obs, oldValue, newValue) -> textArea.setText(textArea.getText() + "\nnew width: " + newValue));
      textArea.setPrefWidth(250);
      textArea.setPrefHeight(250);
      
      BorderPane bp = new BorderPane(textArea);
      bp.setPadding(new Insets(0, 0, 0, 0));

      return new Scene(bp);
   }
}