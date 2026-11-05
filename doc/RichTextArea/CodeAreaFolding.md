# CodeArea - Folding

Andy Goryachev

<andy.goryachev@oracle.com>


## Summary

Adding a code folding capability to the `CodeArea` incubator.



## Problem

Code folding is a highly requested and popular feature for code editors.

The currently incubating version of the `CodeArea` does not support folding, nor it is easy to implement such a feature
using current public API (by design, since the feature was being planned in a follow-up to the first release).



## Solution

Code folding is enabled via the following new APIs:

- `CodeArea.foldingEnabled` styleable property, mapped to `-fx-folding-enabled` CSS token, which enables folding UI
- `boolean isFoldingSupported()` method in the `CodeTextModel` that tells whether the model supports folding
- `List<FoldRange> getFoldRanges(int start, int end)` method in the `CodeTextModel` that retrieves the folding ranges

The `FoldRange` class represents two or more paragraphs that can be collapsed with the following data points:
- start paragraph index
- end paragraph index
- a `RichParagraph` to be shown in place of the collapsed paragraphs

The lifecycle of `FoldRange`s returned by the `CodeArea.getFoldRanges()` is intended to be rather short -
the folds become stale after a paragraph is added or removed within or before the selected range.
The view (`CodeAreaSkin`) keeps track of the edits and re-queries the folds when required.


### Behavior

- an edit inside the collapsed range immediately expands
- folding range are requested / processed only within the sliding window
- don't expect too many collapsed regions for performance reasons


## Specification

```java
TBD
```

## Alternatives

1. Code folding is intended for `CodeArea` since this is the most obvious case.  Should this feature be extended to
the `RichTextArea` as well?  What would be the use cases?

