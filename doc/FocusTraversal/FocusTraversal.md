# Public Focus Traversal API (Draft)

Andy Goryachev

<andy.goryachev@oracle.com>


## Summary

Establishes a public API for focus traversal within a JavaFX application.



## Goals

The goals of this proposal are:

- facilitate the changing of the focused Node (focus traversal) using the public API
- allow for setting a custom traversal policy on a **javafx.scene.Parent**
- allow for listening to the focus traversal events



## Non-Goals

It is not a goal of this proposal:

- to introduce a new focus traversal logic or alter the existing one



## Motivation

While focus traversal is currently implemented in JavaFX, there is no public API to access it,
making it nearly impossible for an application to provide custom focus control [0].

The lack of public API makes it impossible for a custom skin or custom control which requires
keyboard navigation to support focus traversal within the control or transfer focus outside of the custom
control.

The lack of public API also represents a functional gap between JavaFX and Swing.





## Description

The focus traversal API supports two major functions:

- changing focus away from the currently focused Node to an adjacent focusable Node as a response to key presses
(such as Tab, Shift+Tab, or arrow keys)
- customizing traversal order within a single **java.scene.Parent**

The focus traversal is provided by the **FocusTraversal** class which offers static methods
for traversing focus in various directions, determined by the **TraversalDirection** enum.

A new property, called `traversalPolicy`, is added to **java.scene.Parent**.  This property enables
customization of the order of traversal within the said parent, by specifying a custom **TraversalPolicy**.

Changes to the currently focused Node are being broadcast via the new **TraversalEvent** event.

Public focus traversal API classes reside in **javafx.scene.traversal** package [1].



### FocusTraversal

This class provides one general purpose static method which performs focus traversal in the directions
specified by the **TraversalDirection** enum:

- public static boolean **traverse**(Node node, TraversalDirection dir, TraversalMethod method)

This class also provides a number of convenience methods with the goal of simplifying code
that needs focus traversal using the keyboard:

- public static boolean **traverseDown**(Node node)
- public static boolean **traverseLeft**(Node node)
- public static boolean **traverseNext**(Node node)
- public static boolean **traverseNextInLine**(Node node)
- public static boolean **traversePrevious**(Node node)
- public static boolean **traverseRight**(Node node)
- public static boolean **traverseUp**(Node node)

**TraversalMethod** differentiates focus traversal resulting from key press versus those resulting from
mouse clicks or programmatic changes.

A typical use of the `FocusTraversal` class is in built-in and custom skins, as a response to keyboard
navigation key presses:

```java
    switch (((KeyEvent)event).getCode()) {
    case UP :
        FocusTraversal.traverse((Node) obj, TraversalDirection.UP, TraversalMethod.KEY);
        event.consume();
        break;
    }
```


### Traversal Event

Focus traversals generate a new type of event, encapsulated by the class **TraversalEvent** which extends
**javafx.event.Event**, uwing the event type `TraversalEvent.NODE_TRAVERSED`.

The event object encapsulates the Node receiving the focus and the layout bounds of the node,
transformed into the coordinates of the root element in the traversal root being used
(i.e. the Scene or the root Parent).

An event filter or event handler can be added in standard fashion to monitor these events:

```java
    Node node = ...;
    node.addEventHandler(TraversalEvent.NODE_TRAVERSED, (ev) -> {
        // Use properties of the TraversalEvent to appropriately react to this event
        Node n = ev.getNode();
        Bounds b = ev.getBounds();
    });
```


### Focus Traversal Policy

The **TraversalPolicy** abstract class, together with the new `traversalPolicy` property in **java.scene.Parent**,
allows for creation of custom focus traversal policies.  A custom policy must implement the following methods:

- public abstract Node **select**(Parent root, Node owner, TraversalDirection dir)
- public abstract Node **selectFirst**(Parent root)
- public abstract Node **selectLast**(Parent root)

The base class also provides several methods required for implementing a fully functional policy:

- protected Node **findNextFocusableNode**(Parent root, Node node, TraversalDirection dir)
- protected Node **findPreviousFocusableNode**(Parent root, Node node)
- public boolean **isParentTraversable**(Parent root)

The default policy, which is used in many standard Controls, is accessible via the following method:

- public static TraversalPolicy **getDefault**()



## Alternatives

Apart from using **Node.requestFocus()** in some limited scenarios,
there is currently no real alternative to the proposed API.



## Testing

This proposal relies on the existing unit tests and new tests that exercise the new API.
Additional behavior test suite [2] wouldn't hurt either.



## Risks and Assumptions

The proposed API basically makes the existing internal logic public (with some refactoring).
It is possible that bugs were introduced during the refactoring process.  This risk might be relatively low due to
the existing test suite, and can be further mitigated by creating a behavior test suite [2].



## Dependencies

None.



## References

- [0] [JDK-8090456](https://bugs.openjdk.org/browse/JDK-8090456) Focus Management
- [1] API Specification (Javadoc): https://cr.openjdk.org/~angorya/FocusTraversal/javadoc/
- [2] [JDK-8326869](https://bugs.openjdk.org/browse/JDK-8326869) ☂ Develop Behavior Test Suite
- [3] [Keyboard Navigation in JavaFX Controls](https://wiki.openjdk.org/display/OpenJFX/Keyboard+Navigation)
