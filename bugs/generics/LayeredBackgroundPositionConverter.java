package generics;

// https://github.com/openjdk/jfx/blob/master/modules/javafx.graphics/src/main/java/com/sun/javafx/scene/layout/region/LayeredBackgroundPositionConverter.java
public final class LayeredBackgroundPositionConverter extends StyleConverter<ParsedValue<ParsedValue[], BackgroundPosition>[], BackgroundPosition[]> {
    private static final LayeredBackgroundPositionConverter LAYERED_BACKGROUND_POSITION_CONVERTER =
            new LayeredBackgroundPositionConverter();

    public static LayeredBackgroundPositionConverter getInstance() {
        return LAYERED_BACKGROUND_POSITION_CONVERTER;
    }

    private LayeredBackgroundPositionConverter() {
    }
}