# Public Focus Traversal API (Draft)

Andy Goryachev

<andy.goryachev@oracle.com>


## Summary

Establishes a public API for focus traversal within a JavaFX application,
similarly to the way it was done in Swing.



## Goals

The goals of this proposal are:

- facilitate the changing of the focused Node (focus traversal) using the public API
- allow for setting a custom traversal policy on a **javafx.scene.Parent** via the new propery
- allow for listening to the focus traversal events



## Non-Goals

It is not the goal of this proposal:

- to introduce a new focus traversal logic or expand on the existing one



## Motivation

While focus traversal is currently implemented in JavaFX, there is no public API to access it,
making it nearly impossible for an aplication to provide custom focus control.  This also represents
a functional gap between JavaFX and Swing.

The public API would be beneficial for application developers as well as the custom Skin and Control developers.

A number of open JBS tickets call for creating the public API for controlling focus [0].




## Description

It is important to say that the new API merely expose the existing focus traversal logic,
albeit slightly refactored to reduce the number of classes and generally make it simpler.

The focus traversal API is comprised of the **FocusTraversal** class which publishes static methods
for traversing focus in various directions, as represented by the **TraversalDirection** enum.

Changes to the currently focused Node are broadcast via the new **TraversalEvent** event.

A new property called `traversalPolicy` is added to **java.scene.Parent** which allows for setting
a custom **TraversalPolicy**.

Most of the new classes reside in **javafx.scene.traversal** package [1].



### FocusTraversal

This class provides a number of static methods enabling focus traversal in the directions
specified by **TraversalDirection** enum:

- public static boolean **traverse**(Node node, TraversalDirection dir, TraversalMethod method)
- public static boolean **traverseDown**(Node node)
- public static boolean **traverseLeft**(Node node)
- public static boolean **traverseNext**(Node node)
- public static boolean **traverseNextInLine**(Node node)
- public static boolean **traversePrevious**(Node node)
- public static boolean **traverseRight**(Node node)
- public static boolean **traverseUp**(Node node)

**TraversalMethod** differentiates focus traversal resulting from key press versus those resulting from
mouse clicks or programmatic changes.

Additionally, two new static (application-wide) read-only properties are provided (subject to discussion):

- **currentlyFocusedNode**
- **currentlyFocusedWindow**



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

The abstract **TraversalPolicy** class, together with the new `traversalPolicy` property in **java.scene.Parent**,
allow for creation of custom focus traversal policies.  A custom policy must implement the following
abstract methods:

- public abstract Node select(Parent root, Node owner, TraversalDirection dir)
- public abstract Node selectFirst(Parent root)
- public abstract Node selectLast(Parent root)

The default policy, used in many standard Controls, is accessible via `public static getDefault()` method.



## Alternatives

Apart from using **Node.requestFocus()** in some limited scenarios,
there is currently no real alternative to the proposed API.



## Testing

This proposal relies on the existing unit tests.  Additional behavior test suite [2] wouldn't hurt either.



## Risks and Assumptions

The proposed API basically makes the existing internal logic public (with some refactoring).
It is possible that bugs were introduced during the refactoring process.  This risk is mitigated by the existing
test suite, and can be further mitigated by creating a behavior test suite [2].



## Dependencies

None.



## References

- [0] [JDK-8090456](https://bugs.openjdk.org/browse/JDK-8090456) Focus Management
- [1] API Specification (Javadoc): https://cr.openjdk.org/~angorya/FocusTraversal/javadoc/
- [2] [JDK-8326869](https://bugs.openjdk.org/browse/JDK-8326869) ☂ Develop Behavior Test Suite
