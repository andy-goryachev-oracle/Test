package generics;

public final class LayeredBackgroundPositionConverter extends StyleConverter<ParsedValue<ParsedValue[], BackgroundPosition>[], BackgroundPosition[]> {
    private static final LayeredBackgroundPositionConverter LAYERED_BACKGROUND_POSITION_CONVERTER =
            new LayeredBackgroundPositionConverter();

    public static LayeredBackgroundPositionConverter getInstance() {
        return LAYERED_BACKGROUND_POSITION_CONVERTER;
    }

    private LayeredBackgroundPositionConverter() {
    }
}