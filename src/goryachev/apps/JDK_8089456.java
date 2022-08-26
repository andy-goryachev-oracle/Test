package goryachev.apps;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class JDK_8089456 extends Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        ObservableList<Room> roomsList = FXCollections.observableArrayList();

        final TableView rooms = new TableView();
        TableColumn icons = new TableColumn();
        TableColumn name = new TableColumn("Name");
        TableColumn topic = new TableColumn("Topic");
        TableColumn users = new TableColumn("Users");

        rooms.getColumns().addAll(icons, name, topic, users);

        name.setCellValueFactory(new PropertyValueFactory<Room,String>("name"));
        topic.setCellValueFactory(new PropertyValueFactory<Room,String>("topic"));
        users.setCellValueFactory(new PropertyValueFactory<Room,String>("users"));
        icons.setCellValueFactory( new PropertyValueFactory<Room,String>("icon"));

        name.setMinWidth(50);
        name.setMaxWidth(450);
        topic.setMinWidth(10);
        users.setMinWidth(100);
        users.setMaxWidth(150);

        icons.setMaxWidth(35);
        icons.setMinWidth(35);
        // Room resize policy, this is almost perfect
        rooms.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        for (int i = 1; i < 50; i++)
            roomsList.add(new Room("Sample Room " + i));
        rooms.setItems(roomsList);
        
//        BorderPane root = new BorderPane();
//        root.setCenter(rooms);
        
        SplitPane root = new SplitPane(rooms, new BorderPane());
        
        stage.setTitle("JDK-8089456 " + System.getProperty("java.version"));
        stage.setScene(new Scene(root, 800, 460));
        stage.show();

    }

    public class Room {

        public Room(String name) {

            this.name = new SimpleStringProperty(name);
            this.topic = new SimpleStringProperty("This is a sample description text");
            this.icon = new SimpleStringProperty("");
            nUsers = (int)(Math.random() * 1000);
            this.users = new SimpleStringProperty(nUsers.toString());
        }

        Integer nUsers;

        private SimpleStringProperty name;
        private SimpleStringProperty topic;
        private SimpleStringProperty users;
        private SimpleStringProperty icon;

        public String getName() {
            return name.get();
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public String getTopic() {
            return topic.get();
        }

        public void setTopic(String topic) {
            this.topic.set(topic);

        }

        public String getUsers() {
            return nUsers.toString();
        }

        public void setUsers(String users) {
            this.users.set(users);
        }

        public String getIcon() {
            return icon.get();
        }

        public void setIcon(String icon) {
            this.icon.set(icon);
        }
    }
}