package generics;

// https://github.com/openjdk/jfx/blob/master/modules/javafx.graphics/src/main/java/javafx/css/ParsedValue.java
public class ParsedValue<V, T> {
    protected ParsedValue(V value, StyleConverter<V, T> converter) {
    }
}