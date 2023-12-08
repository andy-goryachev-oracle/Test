# Rich Text Area Control

Andy Goryachev

<andy.goryachev@oracle.com>


## Summary

For a long time, JavaFX has lacked a dedicated rich text area control, resulting in a functional gap in relation to Swing with its StyledEditorKit/JEditorPane.  

The new RichTextArea control intends to bridge this gap by providing a dedicated control for displaying and editing rich text.




## Goals

RichTextArea control addresses a number of common use cases:

- read-only presentation of rich text information (help pages, documentation, etc.)
- a simple editor similar to WordPad or TextEdit level, suitable for note taking or message editing.
- a code editor
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

The main design goal is to provide a good enough control to be useful out-of-the box, as well as open to extension by the application developers.

Creating a simple editable control should be easy:

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

### Design Principles

- paragraph-oriented model, up to ~2 billion rows
- virtualized text cell flow
- supports text styling with CSS or attributes embedded in the model
- supports multiple views connected to the same model
- single selection
- input map allows for control extension



### Properties

The new **RichTextArea** control exposes the following properties:

|Property	|Description	|Styleable|
|-----------|---------------|---------|
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

The data model is represented by an implementation of an abstract **StyledTextModel** class.  Conceptually, the model is a sequence of styled text paragraphs (represented by **RichParagraph** class), exposed by these three methods:

- int size()
- String getPlainText(int index)
- RichParagraph getParagraph(int index)

It is important to note that the model does not contain or manages any Nodes, as that will prevent multiple views working off the same model.

The default model for RichTextArea control is **EditableRichTextModel**.  This model stores the styled text segments styled with embedded attributes and should be good enough for majority of use cases.

Attributes supported by this model are listed in the following table:


|Attribute	|Description|
|-----------|-----------|
|BACKGROUND	|paragraph background color
|BULLET	|paragraph bullet point symbol
|BOLD	|bold typeface
|FIRST_LINE_INDENT	|paragraph's first line indent
|FONT_FAMILY	|font family
|FONT_SIZE	|font size in pixels
|ITALIC	|italic typeface
|LINE_SPACING	|paragraph line spacing, in pixels
|RIGHT_TO_LEFT	|paragraph right-to-left text orientation
|SPACE_ABOVE	|space above the paragraph
|SPACE_BELOW	|space below the paragraph
|SPACE_LEFT	|space to the left of the paragraph
|SPACE_RIGHT	|space to the right of the paragraph
|STRIKE_THROUGH	|strike-through text
|TEXT_ALIGNMENT	|paragraph text alignment
|TEXT_COLOR	|text fill color
|UNDERLINE	|underline text


### View

The main feature of RichTextArea is a virtualized text flow, where only a small number of paragraphs is laid out in a sliding window, enabling visualization and even editing of large models.

The size of the sliding window slightly exceeds the visible area, resulting in improved scrolling experience when paragraph heights differ.



### Behavior

RichTextArea control utilizes the new capabilities offered by the new **InputMap** design.  In this design, the control exposes a number of function tags identifying the public methods that convey the behavior.  There is one public method that corresponds to each function tag, allowing for customization of the behavior when required.

The table below lists the available function tags:

|Function Tag	|Description|
|---|---|
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





## Alternatives

A number of open source projects do exist:

- [](https://github.com/FXMisc/RichTextFX)
- https://github.com/gluonhq/rich-text-area
- https://github.com/andy-goryachev/FxEditor




## Risks and Assumptions

TBD



## Dependencies

This enhancement depends on the following RFEs:

- Input map: https://github.com/openjdk/jfx/pull/1254
- Tab stop policy: https://bugs.openjdk.org/browse/JDK-8314482

