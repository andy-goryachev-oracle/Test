# Rich Text Area (Incubator) Data Format

Andy Goryachev

<andy.goryachev@oracle.com>

Draft, June 13, 2025



## Summary

This document describes the data format used by `RichTextArea`'s default text model `RichTextModel`
for saving/loading/copying/pasting of the rich text.

This format (as a part of an incubator module) is likely to change once the decision to integrate it
into the JavaFX core is made.


## Example

As an example, the following rich text

![simple text example](demo-text.png)

is represented by the following file:

```
{}1 normal {b}BOLD{} {tc|FF0000}color{!}
2{!}
```



## Description

The document is represented as a text file in UTF-8 encoding, each paragraph separated by a single `LF` symbol:

```
(<PARAGRAPH><LF>)[]
```

A paragraph is a list of zero or more text segments, followed by the optional paragraph attributes:

```
PARAGRAPH: {
  TEXT_SEGMENT[]
  PARAGRAPH_ATTRIBUTES
}
```

Each text segment consists of one or more character attributes, followed by the text.  The text may contain
escape symbols:

```
TEXT_SEGMENT: {
  CHARACTER_ATTRIBUTE[]
  text
}
```

Each character attribute is enclosed in curly braces, and contains either the attribute name, or the attribute name
with a value separated by the `|` symbol:

```
CHARACTER_ATTRIBUTE: {name}
CHARACTER_ATTRIBUTE: {name|value}
```

An empty attribute is represented by `{}` and indicates the beginning of the next text segment.

Each paragraph might be followed by one or more paragraph attributes:

```
PARAGRAPH_ATTRIBUTE: {!name}
PARAGRAPH_ATTRIBUTE: {!name|value}
```

A special token `{!}` indicates that the paragraph contains no attributes.



### Text Escapes

The three characters `{`, `%`, `}` are escaped using two-byte hexadecimal representation `%XX`.



### Style Compression

To avoid repeating the same attributes over and over, the format employs a form of compression, where the duplicate
attributes are replaced by special tokens:

```
CHARACTER_ATTRIBUTE_LOOKUP: {number}
PARAGRAPH_ATTRIBUTE_LOOKUP: {!number}
```

where `number` is the index of the duplicate attribute map in the document. 

Example:

![style deduplication example](demo-text2.png)

```
{tc|4D804D}line1{!}
{0}line2{!}
{0}line3{!}
```



## Character Attributes

|Name    |StyleAttributeMap     |Type        |Comments                                                      |
|:-------|:---------------------|:-----------|:-------------------------------------------------------------|
|b       |BOLD                  |boolean     |
|ff      |FONT_FAMILY           |String      | Note 1
|fs      |FONT_SIZE             |double      | must be > 0 and finite
|i       |ITALIC                |boolean     |
|ss      |STRIKE_THROUGH        |boolean     |
|tc      |TEXT_COLOR            |Color       | 6 hex digits `RRGGBB`.  Example: {tc&#x007c;4D804D}
|u       |UNDERLINE             |boolean     |

Notes:

1. the standard JavaFX font substitution is performed to render text when the specified font family cannot be found.


## Paragraph Attributes

|Name         |StyleAttributeMap      |Type                |Comments                                                      |
|:------------|:----------------------|:-------------------|:-------------------------------------------------------------|
|alignment    |TEXT_ALIGNMENT         |TextAlignment       | `C`, `J`, `L`, `R`
|bg           |BACKGROUND             |Color               | 6 hex digits `RRGGBB`.  Example: {bg&#x007c;4D804D}
|bullet       |BULLET                 |String              |
|dir          |PARAGRAPH_DIRECTION    |ParagraphDirection  | `L`, `R`
|firstIndent  |FIRST_LINE_INDENT      |double              | must be >= 0 and finite
|lineSpacing  |LINE_SPACING           |double              | must be >= 0 and finite
|spaceAbove   |SPACE_ABOVE            |double              | must be >= 0 and finite
|spaceBelow   |SPACE_BELOW            |double              | must be >= 0 and finite
|spaceLeft    |SPACE_LEFT             |double              | must be >= 0 and finite
|spaceRight   |SPACE_RIGHT            |double              | must be >= 0 and finite



## Future Enhancements

- format version
- tab stops paragraph attributes
- embedded image attributes

