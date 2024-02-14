# Rich Text Area Discussion

This document captures discussion around [RichTextArea](RichTextArea.md) (RTA) features.


## Should a null model be supported?

AG: Yes, it's a valid scenario.  RTA treats the null model as an empty, view-only model.


## Should RichTextArea eventually be in javafx.scene.control or in javafx.scene.control.rich package?

AG: I prefer RTA to be in its own package.


## Which properties should be Styleable?

TBD


## Bidi and caret behavior

There is a greater discussion (see [PR 1220](https://github.com/openjdk/jfx/pull/1220#issuecomment-1770459622)) about improvements to caret shape in the presense of mixed LTR/RTL text.  JavaFX utilizes the "split caret", which is not what the modern software
(like the latest MS Word) use.

We may need to talk about possible display options, animation, obtaining current input language from the platform, etc.

TODO CaretInfo enhancement TBD

[JDK-8296266](https://bugs.openjdk.org/browse/JDK-8296266): TextArea: Navigation breaks with RTL text (Bug - P3)


## SelectionModel.setSelection(Marker, Marker)

Should it clamp selection to the document start/end?


## Corner node

How do we want to set the corner node which may appear when both scroll bars are visible?

ag: add a supplier to ConfigurationParameters, similarly to the scroll bars themselves?
I don't want to force the dev to extend the skin just for that.


## Naming controversies

The list of names that generated the most comments:

- StyleAttrs
- ConfigurationParameters
- addSquiggly


## Should JEP list all the attributes declared in StyleAttrs?

ag: I think providing a complete list in the JEP has some value, even though the full information is present 
in the javadoc.
