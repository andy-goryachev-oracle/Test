package goryachev.apps;

import javafx.beans.property.SimpleStringProperty;

class Entry {
    public final SimpleStringProperty name = new SimpleStringProperty();
    public final SimpleStringProperty text = new SimpleStringProperty();
    
    public Entry(String name, String text) {
        this.name.set(name);
        this.text.set(text);
    }
}