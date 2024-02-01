# Rich Text Area Discussion

This document captures discussion around [RichTextArea](RichTextArea.md) (RTA) features.


## Should a null model be supported?

AG: Yes, it's a valid scenario.  RTA treats the null model as an empty, immutable model.


## Should RichTextArea eventually be in javafx.scene.control or in javafx.scene.control.rich package?

AG: I prefer RTA to be in its own package.


## Which properties should be Styleable?

TBD


## Bidi and Caret Behavior

There is a greater discussion (see [PR 1220](https://github.com/openjdk/jfx/pull/1220#issuecomment-1770459622)) about improvements to caret shape in the presense of mixed LTR/RTL text.  JavaFX utilizes the "split caret", which is not what the modern software
(like the latest MS Word) use.

We may need to talk about possible display options, animation, obtaining current input language from the platform, etc.

TODO CaretInfo enhancement TBD

[JDK-8296266](https://bugs.openjdk.org/browse/JDK-8296266): TextArea: Navigation breaks with RTL text (Bug - P3)


## SelectionModel.setSelection(Marker, Marker)

Should it clamp selection to the document start/end?

