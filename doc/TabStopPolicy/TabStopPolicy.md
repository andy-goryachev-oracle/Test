# Tab Stop Policy

Andy Goryachev

<andy.goryachev@oracle.com>


## Summary

Introduce a `tabStopPolicy` property in the `TextFlow` class which, when set, overrides the existing `tabSize`
value and provides consistent way of setting tab stops at the paragraph level, regardless of the individual text
segments font.



## Goals

The goal of this proposal is to provide a better way for controlling tab stops in the `TextFlow` containing rich text.



## Non-Goals

The following are not the goals of this proposal:

- support for tab stop types (BAR, or DECIMAL), or attributes like `alignment`
- support the `leader` property (symbols to fill the empty space before the tab stop)
- support for `firstLineIndent` property
- deprecate the `TextFlow::tabsize` property



## Motivation

The existing `tabSize` property in the `TextFlow` is inadequate for representing tab stops when the content
contains text with different font sizes.

In addition to that, a rich text editor might require support for user-customizable tab stops, similar to that provided
in RTF or MS Word documents.




## Description

### TextFlow

```java
    /**
     * {@code TabAdvancePolicy} determines the tab stop positions within this {@code TextFlow}.
     * <p>
     * A non-null {@code TabAdvancePolicy} overrides values set by {@link #setTabSize(int)},
     * as well as any values set by {@link Text#setTabSize(int)} in individual {@code Text} instances within
     * this {@code TextFlow}.
     *
     * @defaultValue null
     *
     * @since 999 TODO
     */
    public final ObjectProperty<TabStopPolicy> tabStopPolicyProperty()
	
    public final TabStopPolicy getTabStopPolicy()

    public final void setTabStopPolicy(TabStopPolicy policy)
```


### TabStopPolicy

```java
/**
 * The TabStopPolicy determines the tab stop positions within the text layout.
 *
 * @since 999 TODO
 */
public class TabStopPolicy {

    /**
     * Constructs a new {@code TabStopPolicy} instance.
     *
     * @param reference the node which provides the leading edge for the document layout (can be null)
     */
    public TabStopPolicy(Region reference) {

    /**
     * The reference {@code Region} provides the leading {@code x} coordinate for this {@code TabStopPolicy}.
     * A non-null reference ensures that the tab stops are aligned within a document which is represented by
     * more than one {@code TextFlow} instance.
     * <p>
     * A null reference node results in the leading edge to be set to the leading edge of the {@code TextFlow}
     * being laid out.
     *
     * @return the reference region
     */
    public final Region getReference() {

    /**
     * Specifies the unmodifiable list of tab stops, sorted by position from smallest to largest.
     * The list can be changed using
     * {@link #addTabStop(double)},
     * {@link #clearTabStops()}, or
     * {@link #removeTabStop(TabStop)}.
     *
     * @return the non-null, unmodifiable list of tab stops, sorted by position
     */
    public final ObservableList<TabStop> tabStops() {

    /**
     * Provides default tab stops (beyond the last tab stop specified by {@code #tabStops()},
     * as a fixed repeating distance in pixels from the last tab stop position.
     * The position of default tab stops is computed at regular intervals relative to the leading edge
     * of the {@link #getReference() reference Rectangle} (or, if the reference rectangle is {@code null),
     * the leading edge of the {@code TextFlow} this policy is registered with).
     * <p>
     * The value of {@code 0} disabled the default stops.
     *
     * @return the default tab stops property, in pixels.
     * @defaultValue 0
     */
    public final DoubleProperty defaultStopsProperty() {

    public final double getDefaultStops() {

    public final void setDefaultStops(double value) {
```

### TabStop

```java
/**
 * This class encapsulates an immutable single tab stop within the {@link TabStopPolicy}.
 * <p>
 * A tab stop is at a specified distance from the
 * left margin, aligns text in a specified way, and has a specified leader.
 *
 * @since 999 TODO
 */
public class TabStop {

    /**
     * Constructs a new tab stop with the specified position.
     *
     * @param position the position in pixels
     */
    public TabStop(double position) {

    /**
     * Returns the position, in pixels, of the tab.
     * @return the position of the tab
     */
    public final double getPosition() {
```


## Alternatives

None known.



## Risks and Assumptions

Possible incompatibility with custom controls which define similar property or a property with the same name. 



## Dependencies

None.



## JBS

[JDK-8314482](https://bugs.openjdk.org/browse/JDK-8314482)
