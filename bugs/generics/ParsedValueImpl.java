package generics;

// https://github.com/openjdk/jfx/blob/master/modules/javafx.graphics/src/main/java/com/sun/javafx/css/ParsedValueImpl.java
public class ParsedValueImpl<V, T> extends ParsedValue<V,T> {
    public ParsedValueImpl(V value, StyleConverter<V, T> converter, boolean lookup) {
        super(value, converter);
    }

    public ParsedValueImpl(V value, StyleConverter<V, T> type) {
        this(value, type, false);
    }
}