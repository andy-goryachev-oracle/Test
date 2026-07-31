# RichTextModel - Image Import

Andy Goryachev

<andy.goryachev@oracle.com>


## Summary

Adds an import-only `ImageFormatHandler` to the `RichTextModel` to support pasting of images from the clipboard.



## Problem

After the `RichTextArea` gained support for embedded images with [JDK-8366198](https://bugs.openjdk.org/browse/JDK-8366198),
the images can be added to the model using drag-and-drop but not by pasting from the clipboard.
It's an essential functionality that must be supported.



## Solution

The solution is to add a format handler to the `RichTextModel` to handle `DataFormat.IMAGE` mime type.

When importing, the new handler converts the `Image` obtained from the system clipboard to a PNG-encoded
byte sequence as required by the `EmbeddedImage` class, avoiding loss of image quality. 



## Specification

The `jfx.incubator.scene.control.richtext.model.ImageFormatHandler`
class supports pasting of images from the clipboard.
A global instance of this class is added to the `RichTextModel` to handle import of the `DataFormat.IMAGE` mime type.

```java
/**
 * Facilitates importing of images into the RichTextModel.
 * The image is imported via lossless compression (PNG).
 *
 * @since 28
 */
public class ImageFormatHandler extends DataFormatHandler {

    /**
     * Returns the singleton instance of {@code ImageFormatHandler}.
     * @return the singleton instance of {@code ImageFormatHandler}
     */
    public static final ImageFormatHandler getInstance()

    /**
     * {@inheritDoc}
     *
     * <p>The type of {@code input} must be {@code Image}.
     */
    @Override
    public StyledInput createStyledInput(Object input, StyleAttributeMap attr) throws IOException

    @Override
    public Object copy(StyledTextModel m, StyleResolver r, TextPos start, TextPos end) throws IOException

    @Override
    public void save(StyledTextModel m, StyleResolver r, TextPos start, TextPos end, OutputStream out) throws IOException
```

