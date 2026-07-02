package goryachev.bugs;

import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javafx.beans.NamedArg;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;

public class FXMLLoader_Map_8203870 {

    private ObservableList<String> listProp = FXCollections.observableArrayList();

    public FXMLLoader_Map_8203870() {
    }

    // The problem is solved after the constructor is commented
    public FXMLLoader_Map_8203870(@NamedArg("stringProp") String stringProp) {
    }
    //

    public ObservableList<String> getListProp() {
        return listProp;
    }

    public static void start() throws Exception {
        URL url = FXMLLoader_Map_8203870.class.getResource("FXMLLoader_Map_8203870.fxml");
        FXMLLoader_Map_8203870 region = new FXMLLoader(url).load();

        System.out.println("listProp: " + region.listProp);

        if (region.listProp.size() == 2) {
            System.out.println("No problem.");
        } else {
            System.err.println("Problem appeared");
        }
    }
}