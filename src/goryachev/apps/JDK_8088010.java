package goryachev.apps;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class JDK_8088010 extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override 
	public void start(final Stage stage) throws Exception {
		final VBox root = new VBox();
		root.setPadding(new Insets(10));
		teste(root);
		teste(root);
		teste(root);
		teste(root);
		teste(root);
		
		Scene scene = new Scene(root, 850, 500);
		//scene.getStylesheets().add(CssUtils.getCss());
		stage.setScene(scene);
		stage.setTitle("JDK-8088010 " + System.getProperty("java.version"));
		stage.show();
	}
	
	public void teste(VBox root) {
		TableView<Object> t = new TableView<>();
		t.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		t.setPrefHeight(200);
		t.setPrefWidth(800);		
		t.getColumns().addAll(createColumn("Column1", 100), createColumn("Column2", 400), createColumn("Column3", 50));
		//t.getItems().addAll("", "");
			
		root.getChildren().addAll(t);
	}
	
	public TableColumn<Object, Object> createColumn(String text, int w) {
		TableColumn<Object, Object> c = new TableColumn<>();
		c.setText(text);
		c.setPrefWidth(w);
		//c.setMaxWidth(w * 10000);

		c.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Object,Object>, ObservableValue<Object>>() {
			@Override
			public ObservableValue<Object> call(CellDataFeatures<Object, Object> cell) {
				try {
					return new ReadOnlyObjectWrapper<Object>("X");
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		});

		c.setCellFactory(new Callback<TableColumn<Object,Object>, TableCell<Object,Object>>() {					
			@Override
			public TableCell<Object, Object> call(TableColumn<Object, Object> column) {
				return new TableCell<>();
			}
		});
		
		return c;
	}
}