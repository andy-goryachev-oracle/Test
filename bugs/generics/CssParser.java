package generics;

// https://github.com/openjdk/jfx/blob/6ddb2b2f76a2facfd9d1345787cfa4a6658b0412/modules/javafx.graphics/src/main/java/javafx/css/CssParser.java#L3086
public class CssParser {
    private ParsedValueImpl<ParsedValue<ParsedValue[], BackgroundPosition>[], BackgroundPosition[]> parseBackgroundPositionLayers() {
        ParsedValueImpl<ParsedValue[], BackgroundPosition>[] layers = new ParsedValueImpl[0];
        
        // Eclipse: reports Java Problem
        // Cannot infer type arguments for ParsedValueImpl<>
        // return new ParsedValueImpl<>(layers, LayeredBackgroundPositionConverter.getInstance());
        
        /*
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
        
        // dummy return value so I can check this example into my repo
        return null;
    }
}
