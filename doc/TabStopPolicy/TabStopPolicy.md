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
	 * Tab Stop Policy.
	 *
	 * TODO
	 * @since 999
	 */
	public interface TabStopPolicy {
	
	    /**
	     * Specifies the list of tab stops.
	     *
	     * @return the non-null observable list of tab stops 
	     */
	    public ObservableList<TabStop> tabStops();
	
	    /**
	     * Provides default tab stops (beyond the last tab stop specified by {@code #tabStops()}, as a distance
	     * in points from the last tab stop position.
	     *
	     * TODO
	     * It is unclear how to specify NONE value (negative perhaps?).  MS Word does not allow for NONE, but allows 0.
	     * @return the default tab stops, in pixels.
	     */
	    public double defaultStops();
	}


### TabStop

	/**
	 * This class encapsulates a single tab stop.
	 * A tab stop is at a specified distance from the leading margin.
	 * TabStops are immutable, and usually contained in {@link TabStopPolicy}.
	 *
	 * TODO
	 * @since 999
	 */
	public interface TabStop {
	    /**
	     * Returns the position, in points, of the tab.
	     * @return the position of the tab
	     */
	    public double getPosition() {
	        return position;
	    }
	}


## Alternatives

None known.



## Risks and Assumptions

Possible incompatibility with custom controls which define similar property or a property with the same name. 



## Dependencies

None.

[JDK-8314482](https://bugs.openjdk.org/browse/JDK-8314482)

