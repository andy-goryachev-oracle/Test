# Focus Traversal Policy

Andy Goryachev

<andy.goryachev@oracle.com>

2025/08/01


## Summary

Establishes a public API for focus traversal within a JavaFX application.



## Goals

The goals of this proposal are:

- allow for setting a custom traversal policy on a **javafx.scene.Parent**
- simplify development of skins and custom components which include nested Controls



## Non-Goals

It is not a goal of this proposal:

- to redesign the focus traversal mechanism in JavaFX
- to alter the existing focus traversal behavior
- to allow for focus traversal configuration via CSS



## Motivation

The main problem being solved is to support custom, non-standard, or dynamic focus traversal within
a single **javafx.scene.Parent**, applicable to both internal Skins and custom components.



## Description

A new property, called `traversalPolicy`, is added to **java.scene.Parent**.  This property enables
customization of the order of traversal within the said parent, by specifying a custom **TraversalPolicy**.


### Focus Traversal Policy

In many scenarios, the built-in focus traversal logic is sufficient.  In the situations where a custom traversal
is required, or traversal depends on some condition, a custom traversal policy might be needed.
The **TraversalPolicy** abstract class, together with the new `traversalPolicy` property in **java.scene.Parent**,
allows for creation of custom focus traversal policies.  A custom policy must implement the following methods:

- public abstract Node **select**(Parent root, Node owner, TraversalDirection dir)
- public abstract Node **selectFirst**(Parent root)
- public abstract Node **selectLast**(Parent root)

The base class also provides several methods required for implementing a fully functional policy:

- protected Node **findNextFocusableNode**(Parent root, Node node)
- protected Node **findNextInLineFocusableNode**(Parent root, Node node)
- protected Node **findPreviousFocusableNode**(Parent root, Node node)
- public boolean **isParentTraversable**(Parent root)

The default policy, which is used in many standard Controls, is accessible via the following method:

- public static TraversalPolicy **getDefault**()





## Alternatives

One alternative being discussed is the Focus Delegation APIs [1], [2].
The focus delegation APIs suffer, in my opinion, from attempting to solve the problem partially and
at the wrong level, that is by adding properties to the Node (instead of a single property to the Parent as in 
this proposal).  It is unclear (to me) how to construct a custom traversal that involves multiple controls,
especially supporting dynamic changes to traversal, or traversal that dependse on the information already entered.

The other alternative is for the application code to add an event filter to one or more nodes and attempt to
override the default behavior.


## Risks and Assumptions

TBD



## Dependencies

None.



## References

- [0] [JDK-8090456](https://bugs.openjdk.org/browse/JDK-8090456) Focus Management
- [1] [Focus Delegation API](https://gist.github.com/mstr2/44d94f0bd5b5c030e26a47103063aa29)
- [2] [Focus Delegation PR](https://github.com/openjdk/jfx/pull/1632)
