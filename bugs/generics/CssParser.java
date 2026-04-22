package generics;

// https://github.com/openjdk/jfx/blob/master/modules/javafx.graphics/src/main/java/javafx/css/CssParser.java
final public class CssParser {
    private ParsedValueImpl<ParsedValue<ParsedValue[], BackgroundPosition>[], BackgroundPosition[]> parseBackgroundPositionLayers() {
        ParsedValueImpl<ParsedValue[], BackgroundPosition>[] layers = new ParsedValueImpl[0];

        // hmmm, adding constructors to ParsedValue/ParsedValueImpl solves the issue in both Eclipse and javac
        // but not in the real code, where constructors already exist!
        
        // Eclipse: reports Java Problem
        // Cannot infer type arguments for ParsedValueImpl<>
        /*
         * Javac reports an error as well, with a simple reproducer.
         * However, it DOES NOT report an error in opendjk/jfx build
         * https://github.com/openjdk/jfx/blob/master/modules/javafx.graphics/src/main/java/javafx/css/CssParser.java
         *
        
        javac -version
        javac 25.0.2

        javac *.java
        CssParser.java:9: error: cannot infer type arguments for ParsedValueImpl<>
                return new ParsedValueImpl<>(layers, LayeredBackgroundPositionConverter.getInstance());
                       ^
          reason: cannot infer type-variable(s) V,T
            (actual and formal argument lists differ in length)
          where V,T are type-variables:
            V extends Object declared in class ParsedValueImpl
            T extends Object declared in class ParsedValueImpl
        Note: CssParser.java uses unchecked or unsafe operations.
        Note: Recompile with -Xlint:unchecked for details.
        1 error
        */
        // TODO uncomment to show the error
        return new ParsedValueImpl<>(layers, (StyleConverter<ParsedValue<ParsedValue[], BackgroundPosition>[], BackgroundPosition[]>)LayeredBackgroundPositionConverter.getInstance());
        // dummy return value so I can check this example into my repo
        //return null;
    }
}
