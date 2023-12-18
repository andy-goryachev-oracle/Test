# Rich Text Area Control

Andy Goryachev

<andy.goryachev@oracle.com>


## Summary

Introducing a RichTextArea control for displaying and editing of rich text.

![rich text area screenshot](rich-text-area.png)



## Goals

**RichTextArea** control enables support for a number of common use cases:

- read-only presentation of rich text information (help pages, documentation, etc.)
- a simple editor similar to WordPad or TextEdit level, suitable for note taking or message editing.
- a code editor with syntax highlighting
- an editor which combines rich text with interactive content, such as a code notebook
- enable extension and customization using via the input map



## Non-Goals

The following list represents features RichTextArea does not support:

- "wide" models with long (10K+ symbols) paragraphs (for performance reasons)
- applications requiring arbitrary text/graphics positioning (MS Word)
- desktop publishing application that require precise control of text appearance (Adobe InDesign)
- multiple or rectangular selection segments
- embedded tables



## Motivation

For a long time, JavaFX has lacked a dedicated rich text area control, resulting in a functional gap in relation to Swing with its StyledEditorKit/JEditorPane.  

The new RichTextArea control intends to bridge this gap by providing a dedicated control for displaying and editing rich text.

The main design goal is to provide a good enough control to be useful out-of-the box, as well as open to extension by the application developers.

Creating a simple editable control should be as easy as this:

        RichTextArea t = new RichTextArea();

Creating a read-only informational control should also be easy:

        SimpleReadOnlyStyledModel m = new SimpleReadOnlyStyledModel();
        // add text segment using CSS style name (requires a style sheet)
        m.addSegment("RichTextArea ", null, "HEADER");
        // add text segment using direct style
        m.addSegment("Demo", "-fx-font-size:200%;", null);
        // newline
        m.nl();

        RichTextArea t = new RichTextArea(m);






## Description

[Complete Public API javadoc](javadoc/javadoc.zip) is available.



### Design Principles

- paragraph-oriented model, up to ~2 billion rows
- virtualized text cell flow
- supports text styling with an application stylesheet or inline attributes
- supports multiple views connected to the same model
- single selection
- input map allows for control extension



### Properties

The new **RichTextArea** control exposes the following properties:

|Property |Description |Styleable|
|:--------|:-----------|:--------|
|anchorPosition	|provides the anchor position (read-only)	
|caretBlinkPeriod	|determines the caret blink rate	
|caretPosition	|provides the caret position (read-only)	
|contentPadding	|defines the amount of padding in the content area	|Yes
|displayCaret	|indicates whether the caret is displayed	
|editable	|indicates whether the editing is enabled	
|highlightCurrentParagraph	|indicates whether the current paragraph is highlighted	
|model	|document data model	
|wrapText	|indicates whether text should be wrapped	|Yes


### Model

The RichTextArea control separates data model from the view by providing the **model** property.

Conceptually, the model is a sequence of styled text paragraphs, represented by **RichParagraph** class, exposed by these three methods:

- int size()
- String getPlainText(int index)
- RichParagraph getParagraph(int index)

It is important to note that the model does not contain or manages any Nodes, as that will prevent multiple views working off the same model.


#### Standard Models

The base class for any data model is **StyledTextModel**.  This abstract class provides no data storage, however, it takes care of many mundane tasks in order to ease the process of writing custom models, such as dealing with styled segments, keeping track of markers, sending events, and so on.

Other models are also included, as described in this table:

|Class Name	|Description
|:---|:---|
|StyledTextModel	|Base class
|├ EditableRichTextModel	|Default model for RichTextArea
|├ BasePlainTextModel	|Base class for models based on plain text
|│ └ CodeTextModel	|Default model for CodeArea
|└ StyledTextModelReadOnlyBase	|Base class for a read-only model
|   └ SimpleReadOnlyStyledModel	|In-memory read-only styled model

**EditableRichTextModel** stores the data in memory, in the form of text segments styled with attributes defined in **StyleAttrs** class.  This is a default model for RichTextArea.

The abstract **BasePlainTextModel** is a base class for in-memory text models which are based on plain text.  This class provides foundation for the **CodeTextModel**, which styles the text using a pluggable **SyntaxDecorator**.

The abstract **StyledTextModelReadOnlyBase** is a base class for read-only models.  This class is used by **SimpleReadOnlyStyledModel** which simplifies building of in-memory read-only styled documents.


#### Styling

There are two ways of styling text in RichTextArea: either using inline attributes, or relying on style names in the application style sheet.  It is important to understand the limitation of stylesheet approach as it is only suitable for read-only models because editing of styles by the user is nearly impossible given the static nature of the application stylesheet.  (An example provided earlier illustrates how to style a read-only document using SimpleReadOnlyStyledModel and an application stylesheet).

The default model for RichTextArea, EditableRichTextModel, utilizes a number of style attributes (found in StyleAttrs class).  These attributes are applicable either to the whole paragraph (BACKGROUND, BULLET, FIRST_LINE_INDENT, ...) or to the individual text segments (BOLD, FONT_FAMILY, etc.).

This example illustrates how to populate an editable RichTextArea programmatically:

        // create styles
        StyleAttrs heading = StyleAttrs.builder().setBold(true).setFontSize(24).build();
        StyleAttrs plain = StyleAttrs.builder().setFontFamily("Monospaced").build();

        RichTextArea rta = new RichTextArea();
        // build the content
        rta.appendText("Heading\n", heading);
        rta.appendText("Plain monospaced text.\n", plain);



### Export/Import

StyledTextModel provides a common mechanism for importing/exporting styled text into/from the model via the following methods:

- exportText(TextPos start, TextPos end, StyledOutput out)
- TextPos replace(StyleResolver, TextPos start, TextPos end, String text, boolean createUndo)
- TextPos replace(StyleResolver, TextPos start, TextPos end, StyledInput in, boolean createUndo)

The I/O classes **StyledInput** and **StyledOutput** provide the transport of individual **StyledSegment**s.

At the control level, save() and load() methods allow for data transfer using any of the data formats supported by the underlying model.



### Skin

The default skin, implemented by the **RichTextAreaSkin** class, provides the visual representation of RichTextArea control.

The main feature of the default skin is a virtualized text flow, where only a small number of paragraphs is laid out in a sliding window, enabling visualization and even editing of large models.

The size of the sliding window slightly exceeds the visible area, resulting in improved scrolling experience when paragraph heights differ.



### Behavior

RichTextArea control utilizes the new capabilities offered by the new **InputMap** feature.  In this design, the control exposes a number of function tags identifying the public methods that convey the behavior.  There is one public method that corresponds to each function tag, allowing for customization of the behavior when required.

The table below lists the available function tags:

|Function Tag|Description|
|:-----------|:----------|
|BACKSPACE	|Deletes the previous symbol
|COPY	|Copies selected text to the clipboard
|CUT	|Cuts selected text and places it to the clipboard
|DELETE	|Deletes symbol at the caret
|DELETE_PARAGRAPH	|Deletes paragraph at the caret, or selected paragraphs
|INSERT_LINE_BREAK	|Inserts a single line break
|INSERT_TAB	|Inserts a TAB symbol
|MOVE_DOCUMENT_END	|Moves the caret to end of the document
|MOVE_DOCUMENT_START	|Moves the caret to beginning of the document
|MOVE_DOWN	|Moves the caret one visual text line down
|MOVE_LEFT	|Moves the caret one symbol to the left
|MOVE_PARAGRAPH_END	|Moves the caret to the end of the current paragraph
|MOVE_PARAGRAPH_START	|Moves the caret to the beginning of the current paragraph
|MOVE_RIGHT	|Moves the caret one symbol to the right
|MOVE_UP	|Moves the caret one visual text line up
|MOVE_WORD_LEFT	|Moves the caret one word left (previous word if LTR, next word if RTL)
|MOVE_WORD_NEXT	|Moves the caret to the next word
|MOVE_WORD_NEXT_END	|Moves the caret to the end of next word
|MOVE_WORD_PREVIOUS	|Moves the caret to the previous word
|MOVE_WORD_RIGHT	|Moves the caret one word right (next word if LTR, previous word if RTL)
|PAGE_DOWN	|Moves the caret one page down
|PAGE_UP	|Moves the caret one page up
|PASTE	|Inserts rich text from the clipboard
|PASTE_PLAIN_TEXT	|Inserts plain text from the clipboard
|REDO	|Reverts the last undo operation
|SELECT_ALL	|Selects all text in the document
|SELECT_DOCUMENT_END	|Selects text (or extends selection) from the current caret position to the end of document
|SELECT_DOCUMENT_START	|Selects text (or extends selection) from the current caret position to the start of document
|SELECT_DOWN	|Selects text (or extends selection) from the current caret position one visual text line down
|SELECT_LEFT	|Selects text (or extends selection) from the current position to one symbol to the left
|SELECT_PAGE_DOWN	|Selects text (or extends selection) from the current position to one page down
|SELECT_PAGE_UP	|Selects text (or extends selection) from the current position to one page up
|SELECT_PARAGRAPH	|Selects text (or extends selection) of the current paragraph
|SELECT_RIGHT	|Selects text (or extends selection) from the current position to one symbol to the right
|SELECT_UP	|Selects text (or extends selection) from the current caret position one visual text line up
|SELECT_WORD	|Selects word at the caret position
|SELECT_WORD_LEFT	|Extends selection to the previous word (LTR) or next word (RTL)
|SELECT_WORD_NEXT	|Extends selection to the next word
|SELECT_WORD_NEXT_END	|Extends selection to the end of next word
|SELECT_WORD_PREVIOUS	|Extends selection to the previous word
|SELECT_WORD_RIGHT	|Extends selection to the next word (LTR) or previous word (RTL)
|UNDO	|Undoes the last edit operation

Additionally, the InputMap allows for redefining of the key mappings.

The following example illustrates how the basic navigation can be altered to support custom navigation (for example, allowing to jump to the next CamelCase word):

        richTextArea.getInputMap().registerFunction(RichTextArea.MOVE_WORD_NEXT, () -> {
            // refers to custom logic
            TextPos p = getCustomNextWordPosition(richTextArea);
            richTextArea.setCaret(p);
        });



### Extensibility

RichTextArea is designed with extensibility in mind.  A number of mechanisms are provided for the application developer to customize the control behavior:

- extending the model by adding new attributes
- adding new functions with new key bindings
- redefining the existing key bindings
- by adding left and right side paragraph decorators
- providing custom scroll bars via **ConfigurationParameters**



## Alternatives

A number of open source projects do exist:

- https://github.com/FXMisc/RichTextFX
- https://github.com/gluonhq/rich-text-area
- https://github.com/andy-goryachev/FxEditor


## Testing

Two manual test applications are provided - one for RichTextArea (**RichTextAreaDemoApp**)
and one for the CodeArea (**CodeAreaDemoApp**).

In addition to these two testers, a small example provides a standalone rich text editor, see **RichEditorDemoApp**.




## Risks and Assumptions

TBD



## Dependencies

This enhancement depends on the following RFEs:

- Input map: https://github.com/openjdk/jfx/pull/1254
- Tab stop policy: [JDK-8314482](https://bugs.openjdk.org/browse/JDK-8314482)

