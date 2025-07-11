# Public API for Text Layout

Andy Goryachev

<andy.goryachev@oracle.com>

June 13, 2025


## Summary

This proposal introduces a new method to the `Text` and `TextFlow` classes, `getLayoutInfo()`,
which returns a view into the text layout for that node.  The new method returns a new class, `LayoutInfo`,
which allows for querying various aspects of the text layout, such as:

- caret information
- overall layout bounds
- text lines: offsets and bounds
- text selection geometry
- strike-through geometry
- underline geometry



## Goals

The goal of this proposal is to provide a public API for querying the text layout and the associated caret geometry
from the internal text layout [1], [2], [3].



## Non-Goals

It is not a goal to:

- modify the existing text layout mechanism



## Motivation

Applications dealing with text-based Nodes, such as those providing rich text editors, need more information
about the layout of text than currently provided via public API.

While partial information about the text layout can be obtained by reverse-engineering undocumented `PathElement[]`
returned by the existing `caretShape()` and `rangeShape()` methods in the **Text** and **TextFlow** classes,
other information, such as strike-through geometry, cannot be obtained via public API at all.

Reverse engineering is currently successful because developers realize that the `PathElement[]` array contains
a sequence of `MoveTo` and `LineTo` elements, and nothing else.  This may or may not be true in the future,
and in general the structure of this array, while suitable for using with the `Path` class, is not documented.

The application and library developers typically resolve to creating custom nodes that extend `TextFlow`,
or use the hacks like drawing mid-height lines for the strike-through decoration.



## Description

The new API provide a way to query additional information about the text layout within the **Text** and the **TextFlow** nodes,
for the applications that require additional introspection into details of the text layout.


### Caret Information

The new API provides the rectangles represeting a single caret:

![single caret](single-caret.png)

or a split caret:

![split caret](split-caret.png)

 
### Layout Bounds

The new API provides the overall layout bounds, including the line spacing:

![layout bounds with the line spacing](bounds-with-line-spacing.png)

or excluding the line spacing:

![layout bounds without the line spacing](bounds-without-line-spacing.png)


### Individual Text Lines

The text layout details such as break up of text into multiple lines, the geometry of said lines,
and the character offsets are provided by the new API.

The caller can request geometry to include the line spacing:

![text lines with the line spacing](lines-with-line-spacing.png)

or exclude the line spacing:

![text lines without the line spacing](lines-without-line-spacing.png)


### Text Selection

The selection shape (range shape) for an arbitrary range of text can be requested by the caller to include the line spacing:

![selection shape with the line spacing](selection-with-line-spacing.png)

or exclude line spacing:

![selection shape without the line spacing](selection-without-line-spacing.png)



### Strike-through and Underline Decoration

The geometry of commonly used decorations can be obtained via the new API.   The supported decorations are strike-through:

![strike-through shapes](strike-through.png)

and underline:

![underline shapes](underline.png)



### javafx.scene.text.Text

```java
    /**
     * Returns a copy of the of the text layout geometry for this node. This copy is a snapshot
     * of the text layout at the time the method is called.
     * <p>
     * While there is no general guarantee that successive invocations of this method return the same instance,
     * it is safe to either cache this object or call this method each time, since the information obtained from
     * this lightweight object remains valid until the next layout cycle.
     *
     * @return a copy of the layout information
     * @since 25
     */
    public final LayoutInfo getLayoutInfo()
```

### javafx.scene.text.TextFlow

```java
    /**
     * Returns a copy of the of the text layout geometry for this node. This copy is a snapshot
     * of the text layout at the time the method is called.
     * <p>
     * While there is no general guarantee that successive invocations of this method return the same instance,
     * it is safe to either cache this object or call this method each time, since the information obtained from
     * this lightweight object remains valid until the next layout cycle.
     *
     * @return a copy of the layout information
     * @since 25
     */
    public final LayoutInfo getLayoutInfo()
```


### javafx.scene.text.LayoutInfo

This class provides a view into the text layout used by the **Text** and **TextFlow** nodes,
with the purpose of querying the details of the layout such as break up of the text into lines,
as well as geometry of other shapes derived from the layout (selection, underline, etc.).

The information obtained via this object may change after the next layout cycle, which may come as a result
of actions such as resizing of the container, or modification of certain properties.
For example updating the text or the font might change the layout, but a change of color would not.

The **LayoutInfo** class provides the following methods:

- `public CaretInfo caretInfo(int charIndex, boolean leading)` - returns the caret geometry for the given character index and the character bias
- `public Rectangle2D getLogicalBounds(boolean includeLineSpacing)` - returns the logical bounds of the layout
- `public int getTextLineCount()` - returns the number of text lines in the layout
- `public List<TextLineInfo> getTextLines(boolean includeLineSpacing)` - returns information about text lines in the layout
- `public TextLineInfo getTextLine(int index, boolean includeLineSpacing)` - returns the information about the text line at the given index
- `public List<Rectangle2D> getSelectionGeometry(int start, int end, boolean includeLineSpacing)` - returns the geometry of the text selection for the given start and end offsets
- `public List<Rectangle2D> getStrikeThroughGeometry(int start, int end)` - returns the geometry of the strike-through shape for the given start and end offsets
- `public List<Rectangle2D> getUnderlineGeometry(int start, int end)` - returns the geometry of the underline shape for the given start and end offsets


### javafx.scene.text.TextLineInfo

Provides the information about a text line in a text layout:

- `int start` the start offset for the line
- `int end` the end offset for the line (index of the last character + 1)
- `Rectangle2D bounds` the bounds of the text line, in local coordinates


### javafx.scene.text.CaretInfo

Provides the information associated with the caret:

- `public int getSegmentCount()` - returns the number of segments representing the caret
- `public Rectangle2D getSegmentAt(int index)` - returns the geometry of the segment at the specified index




## Future Additions

In the future, these APIs may be extended to provide additional information about
the text runs for the purposes of better handling of bidirectional use cases.



## Alternatives

Do nothing.



## Risks and Assumptions

1. The proposed APIs pose some compatibility risk since the application developers can extends `Text` and `TextFlow`
classes adding methods with similar signatures.

2. In light of the existing bug [4], there will be discrepancy between the results provided by the new API and
the `PathElement`s returned by the existing APIs in the `TextFlow` class when non-empty padding and/or borders
are set.  The new API should have this issue addressed (i.e. provide the correct results)
separately from any solution that might eventually be adopted for [4].

3. A similar, though less impactful, concern exists in regards to the existing bugs [5] and [6],
since the new API provides dedicated flags to control whether lineSpacing property should be used to generate the result.

4. The proposed APIs are also applicable to the Right-to-Left (RTL) orientation, though a number of existing bugs
exist that block the implementation, most notably [7].



## Dependencies

None.



## References

1. [JDK-8341670](https://bugs.openjdk.org/browse/JDK-8341670) [Text,TextFlow] Public API for Text Layout Info (Enhancement - P4)
2. [JDK-8341672](https://bugs.openjdk.org/browse/JDK-8341672) [Text/TextFlow] getRangeInfo (Enhancement - P4)
3. [JDK-8341671](https://bugs.openjdk.org/browse/JDK-8341671) [Text/TextFlow] getCaretInfo (Enhancement - P4)
4. [JDK-8341438](https://bugs.openjdk.org/browse/JDK-8341438) TextFlow: incorrect caretShape(), hitTest(), rangeShape() with non-empty padding/border
5. [JDK-8317120](https://bugs.openjdk.org/browse/JDK-8317120) RFE: TextFlow.rangeShape() ignores lineSpacing
6. [JDK-8317122](https://bugs.openjdk.org/browse/JDK-8317122) RFE: TextFlow.preferredHeight ignores lineSpacing
7. [JDK-8318095](https://bugs.openjdk.org/browse/JDK-8318095) TextArea/TextFlow: wrong layout in RTL mode
