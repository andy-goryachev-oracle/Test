# Public Focus Traversal API (2nd Draft)

Andy Goryachev

<andy.goryachev@oracle.com>


## Summary

Establishes a public API for focus traversal within a JavaFX application.



## Goals

The goals of this proposal are:

- facilitate the changing of the focused Node (focus traversal) using the public API



## Non-Goals

It is not a goal of this proposal:

- to introduc a public API for a focus traversal policy
- to redesign the focus traversal mechanism in JavaFX
- to alter the existing focus traversal behavior
- to allow for focus traversal configuration via CSS



## Motivation

While focus traversal is currently implemented in JavaFX, there is no public API to access it,
making it nearly impossible for a custom component to effect focus traversal [0].

The lack of public API makes it impossible for a custom skin or custom control which requires
keyboard navigation to support focus traversal within the control or transfer focus outside of the custom
control.

The lack of public API also represents a functional gap between JavaFX and Swing.




## Description

The focus traversal is provided by the **FocusTraversal** class which offers static methods
for traversing focus in various directions.

Public focus traversal API classes reside in **javafx.scene.traversal** package [1].



### FocusTraversal

This class provides a number of static method which performs focus traversal from the given Node
in specific directions:

- public static boolean **traverseDown**(Node node)
- public static boolean **traverseLeft**(Node node)
- public static boolean **traverseNext**(Node node)
- public static boolean **traversePrevious**(Node node)
- public static boolean **traverseRight**(Node node)
- public static boolean **traverseUp**(Node node)

Typically, controls do not need to handle focus traversal keys explicitly, relying instead on the built-in
traversal logic, unless:

- the traversal is conditional upon the state of the control
- the key used to traverse is used in a non-traversal capacity (one example is the **tab** key in the context of a text editor)

A typical use of the `FocusTraversal` class is in built-in and custom skins, as a response to keyboard
navigation key presses:

```java
    Node from = ...
    KeyEvent ev = ...
    if (!ev.isAltDown() && !ev.isControlDown() && !ev.isMetaDown() && !ev.isShiftDown() && !ev.isShortcutDown()) {
        switch (ev.getCode()) {
        case TAB:
            if (isEditable()) {
                insertTab();
            } else {
                FocusTraversal.traverseNext(from);
            }
            ev.consume();
            break;
	    }
	}
```

The new API are provided as static methods of `FocusTraversal` class instead of the instance methods of `Node` class
because these calls change the state of the application at large, not just the particular node.



## Alternatives

In the author's opinion, there is no reasonable alternative for a custom skin/component to initiate the focus
traversal in the specific direction.




## Testing

This proposal relies on the existing unit tests and new tests that exercise the new API.



## Risks and Assumptions

The proposed API basically makes the existing internal focus traversal methods public.

It is possible that bugs were introduced during the refactoring process.  This risk might be relatively low due to
the existing test suite, and can be further mitigated by creating a behavior test suite [2].



## Dependencies

None.



## References

- [0] [JDK-8091673](https://bugs.openjdk.org/browse/JDK-8091673) Public focus traversal API for use in custom controls
- [1] API Specification (Javadoc): https://cr.openjdk.org/~angorya/FocusTraversal.2/javadoc/
- [2] [Keyboard Navigation in JavaFX Controls](https://wiki.openjdk.org/display/OpenJFX/Keyboard+Navigation)
