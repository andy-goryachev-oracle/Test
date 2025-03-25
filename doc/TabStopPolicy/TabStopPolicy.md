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

    /**
     * Tab stop policy.
     * This value overrides the {@code tabSize} of this TextFlow as well as
     * in contained {@link Text} nodes.
     *
	 * TODO
	 * @since 999
     */
    public final ObjectProperty<TabStopPolicy> tabStopPolicyProperty()
	
    public final TabStopPolicy getTabStopPolicy()
	
    public final void setTabStopPolicy(TabStopPolicy policy)

### TabStopPolicy

	/**
	 * TabStopPolicy determines the tab stop positions within the text layout.
	 *
	 * @since 999 TODO
	 */
	public class TabStopPolicy {
	
	    /**
	     * Constructs a new {@code TabStopPolicy} instance.
	     */
	    public TabStopPolicy() {
	
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
	     * Adds a new tab stop at the specified position.
	     * This method does nothing if the position coincides with an already existing tab stop.
	     *
	     * @param position the tab stop position
	     */
	    public final void addTabStop(double position) {
	
	    /**
	     * Removes the specified tab stop.
	     *
	     * @param stop the tab stop to remove
	     */
	    public final void removeTabStop(TabStop stop) {
	
	    /**
	     * Removes all tab stops.
	     */
	    public final void clearTabStops() {
	
	    /**
	     * Provides default tab stops (beyond the last tab stop specified by {@code #tabStops()}, as a distance
	     * in points from the last tab stop position.
	     *
	     * TODO
	     * It is unclear how to specify NONE value (negative perhaps?).  MS Word does not allow for NONE, but allows 0.
	     *
	     * @return the default tab stops property, in pixels.
	     * @defaultValue TODO
	     */
	    public final DoubleProperty defaultStops() {
	
	    public final double getDefaultStops() {
	
	    public final void setDefaultStops(double value) {


### TabStop

	/**
	 * This class encapsulates an immutable single tab stop within the {@link TabStopPolicy}.
	 * <p>
	 * A tab stop is at a specified distance from the
	 * left margin, aligns text in a specified way, and has a specified leader.
	 * 
	 * @since 999 TODO
	 */
	public final class TabStop {
	
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
	    public double getPosition() {


## Alternatives

None known.



## Risks and Assumptions

Possible incompatibility with custom controls which define similar property or a property with the same name. 



## Dependencies

None.



## JBS

[JDK-8314482](https://bugs.openjdk.org/browse/JDK-8314482)

