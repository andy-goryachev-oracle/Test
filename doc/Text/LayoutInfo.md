# Public API for Text Layout

Andy Goryachev

<andy.goryachev@oracle.com>


## Summary

This proposal introduces a new method to the `Text` and `TextFlow` classes, `getLayoutInfo()`,
which returns a view into the text layout for that node.  The new method returns a new class, `LayoutInfo`,
which allows for querying various aspects of the text layout, such as:
- text lines: offsets and bounds
- overall layout bounds
- text selection geometry
- strike-through geometry
- underline geometry
- caret information



## Goals

The goal of this proposal is to provide a public API for querying the text layout geometry and extended caret
information from the text layout.



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

### javafx.scene.text.Text

```java
    /**
     * Returns the object which provides a view into the text layout for this node, which allows for querying
     * the details of the layout.
     * <p>
     * While there is no general guarantee that successive invocations of this method return the same instance,
     * it is safe to either cache this object or call this method each time, since the information obtained from
     * this lightweight object remains valid until the next layout cycle.
     * <p>
     * The information obtained after the next layout cycle might be different as a result
     * of actions such as resizing of the container, or modification of certain properties.
     * For example updating the text or the font might change the layout, but a change of color would not.
     *
     * @return the layout information
     * @since 24
     */
    public final LayoutInfo getLayoutInfo()
```

### javafx.scene.text.TextFlow

```java
    /**
     * Returns the object which provides a view into the text layout for this node, which allows for querying
     * the details of the layout.
     * <p>
     * While there is no general guarantee that successive invocations of this method return the same instance,
     * it is safe to either cache this object or call this method each time, since the information obtained from
     * this lightweight object remains valid until the next layout cycle.
     * <p>
     * The information obtained after the next layout cycle might be different as a result
     * of actions such as resizing of the container, or modification of certain properties.
     * For example updating the text or the font might change the layout, but a change of color would not.
     *
     * @return the layout information
     * @since 24
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

- public Rectangle2D **getBounds**(boolean includeLineSpacing) - returns the logical bounds of the layout
- public int **getTextLineCount**() - returns the number of text lines in the layout
- public List<TextLineInfo> **getTextLines**(boolean includeLineSpacing) - returns the immutable list of the `TextLineInfo` objects describing the layout
- public TextLineInfo **getTextLine**(int index, boolean includeLineSpacing) - returns the `TextLineInfo` object which contains information about the text line at the given index
- public List<Rectangle2D> **selectionShape**(int start, int end, boolean includeLineSpacing) - returns the geometry of the text selection, as an immutable list of `Rectangle2D` objects, for the given start and end offsets
- public List<Rectangle2D> **strikeThroughShape**(int start, int end) - returns the geometry of the strike-through shape, as an immutable list of `Rectangle2D` objects, for the given start and end offsets
- public List<Rectangle2D> **underlineShape**(int start, int end) - returns the geometry of the underline shape, as an immutable list of `Rectangle2D` objects, for the given start and end offsets
- `public CaretInfo caretInfo(int charIndex, boolean leading)` - returns the caret geometry for the given character index and the character bias


### javafx.scene.text.TextLineInfo

Provides the information about a text line in a text layout:

- `start` the start offset for the line
- `end` the end offset for the line (index of the last character + 1)
- `bounds` the bounds of the text line, in local coordinates


### javafx.scene.text.CaretInfo

Provides the information associated with the caret:

- **public int getPartCount()** - returns the number of parts representing the caret
- **public Rectangle2D **getPartAt(int index)** - returns the geometry of the part at the specified index



## Alternatives

Do nothing.



## Risks and Assumptions

The proposed APIs pose some compatibility risk since the application developers can extends `Text` and `TextFlow`
classes adding methods with similar signatures.



## Dependencies

None.


## To Be Determined

A number of issues require further discussion:

- possible discrepancy should the chosen solution for bugs [2] and [3] be to leave the existing functionality as is
- how to deal with possible discrepancy and/or backward compatibility due to bugs in the existing APIs related to
padding, borders, and line spacing [5], [6], [7].



## References

1. [JDK-8341670](https://bugs.openjdk.org/browse/JDK-8341670) [Text,TextFlow] Public API for Text Layout Info (Enhancement - P4)
2. [JDK-8341672](https://bugs.openjdk.org/browse/JDK-8341672): [Text/TextFlow] getRangeInfo (Enhancement - P4)
3. [JDK-8341671](https://bugs.openjdk.org/browse/JDK-8341671): [Text/TextFlow] getCaretInfo (Enhancement - P4)
4. [JDK-8092278](https://bugs.openjdk.org/browse/JDK-8092278) Text should have API for selecting group of characters based on their position similar to the DOM's Range.
5. [JDK-8317120](https://bugs.openjdk.org/browse/JDK-8317120) RFE: TextFlow.rangeShape() ignores lineSpacing
6. [JDK-8317122](https://bugs.openjdk.org/browse/JDK-8317122) RFE: TextFlow.preferredHeight ignores lineSpacing
7. [JDK-8341438](https://bugs.openjdk.org/browse/JDK-8341438) TextFlow: incorrect caretShape(), hitTest(), rangeShape() with non-empty padding/border
