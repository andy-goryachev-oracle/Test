# Public Focus Traversal API (3rd Draft)

Andy Goryachev

<andy.goryachev@oracle.com>


## Summary

Establishes a public API for focus traversal within a JavaFX application.



## Goals

The goals of this proposal are:

- facilitate the changing of the focused Node (focus traversal) using the public API



## Non-Goals

It is not a goal of this proposal:

- to introduce a public API for a focus traversal policy
- to redesign the focus traversal mechanism in JavaFX
- to alter the existing focus traversal behavior
- to allow for focus traversal configuration via CSS



## Motivation

While focus traversal is currently implemented in JavaFX, there is no public API to access it.
The lack of public API makes it impossible for a custom skin or custom control to support focus traversal
in the specific direction [0].



## Description

The focus traversal is provided adding a single method to the **Node** class:

`public final boolean **requestFocusTraversal**(TraversalDirection direction)`

where **TraversalDirection** enumerates the search direction relative to the current node [1].

Typically, controls do not need to handle focus traversal keys explicitly, relying instead on the built-in
traversal logic, unless:

- the traversal is conditional upon the state of the control
- the key used to traverse is used in a non-traversal capacity (one example is the **tab** key in the context of a text editor)

The following example illustrates the use of new API in the context of a text editor conditionally handling
of the **tab** key:

```java
    Node from = ...
    KeyEvent ev = ...
    if (!ev.isAltDown() && !ev.isControlDown() && !ev.isMetaDown() && !ev.isShiftDown() && !ev.isShortcutDown()) {
        switch (ev.getCode()) {
        case TAB:
            if (isEditable()) {
                insertTab();
            } else {
                from.requestFocusTraversal(TraversalDirection.NEXT);
            }
            ev.consume();
            break;
	    }
	}
```



## Alternatives

In the author's opinion, there is no reasonable alternative for a custom skin/component to initiate the focus
traversal in the specific direction.




## Testing

This proposal relies on the existing unit tests and new tests that exercise the new API.



## Risks and Assumptions

The proposed API basically makes the existing internal focus traversal methods public.

It is possible that bugs were introduced during the refactoring process.  This risk might be relatively low due to
the existing test suite, and can be further mitigated by creating a behavior test suite.



## Dependencies

None.



## References

- [0] [JDK-8091673](https://bugs.openjdk.org/browse/JDK-8091673) Public focus traversal API for use in custom controls
- [1] API Specification (Javadoc): https://cr.openjdk.org/~angorya/FocusTraversal.3/javadoc/
- [2] [Keyboard Navigation in JavaFX Controls](https://wiki.openjdk.org/display/OpenJFX/Keyboard+Navigation)
