# Tab Stop Policy

Andy Goryachev

<andy.goryachev@oracle.com>


## Summary

This proposal introduces a tabStopPolicy property in TextFlow (and possibly Text) which, when set, overrides the existing tabSize
value and provides consistent way of setting tab stops at the paragraph level regardless of the individual text segments'
font size. 



## Goals

The goal of this proposal is to provide a better way to control tab stops, by supporting the existing capability of
specifying a fixed tab size as well as a variable number of user-definable tab stops positioned relative
to the document edge.



## Non-Goals

- support for a BAR (|) tab stop
- support the `leader` property (symbols to fill the empty space before the tab stop)
- provide a replacement for `tabsize` property



## Motivation

The existing tabSize property in Text and TextFlow controls is inadequate to represent tab stops in the context
of a rich text control, failing spectacularly when TextFlow contains multiple Text nodes which use different fonts.

In addition to that, a rich text editor (either the RichTextArea control currently being developed,
or a custom rich text editor) might need support for user-customizable tab stops offered by RTF or MS Word documents.




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
	public final class TabStopPolicy {
	
	    /**
	     * Creates an immutable {@code TabStop} instance.
	     *
	     * @param tabStops the tab stops (a copy will be made)
	     * @param firstLineIndent the first line indent, in points
	     * @param defaultStops the default stops, in points
	     */
		public TabStopPolicy(List<TabStop> tabStops, double firstLineIndent, double defaultStops);
	
	    /**
	     * Specifies the list of tab stops.
	     *
	     * @return the non-null list of tab stops 
	     */
	    public List<TabStop> tabStops() {
	        return tabStops;
	    }
	    
	    /**
	     * First line indent, in points, a positive value.  Negative or 0 values are treated as no first line indent.
	     *
	     * TODO
	     * It is unclear whether the TextLayout should support negative values as it might impact the size and
	     * the preferred size of the layout.
	     *
	     * @return the first line indent, in points
	     */
	    public double firstLineIndent() {
	        return firstLineIndent;
	    }
	
	    /**
	     * Provides default tab stops (beyond the last tab stop specified by {@code #tabStops()}, as a distance
	     * in points from the last tab stop position.
	     *
	     * TODO
	     * It is unclear how to specify NONE value (negative perhaps?).  MS Word does not allow for NONE.
	     * @return the default tab stops, in points.
	     */
	    public double defaultStops() {
	        return defaultStops;
	    }
	    
	    // TODO hashCode, equals, toString
	}


### TabStop

	/**
	 * This class encapsulates a single tab stop.
	 * A tab stop is at a specified distance from the
	 * left margin, aligns text in a specified way, and has a specified leader.
	 * TabStops are immutable, and usually contained in {@link TabStopPolicy}.
	 *
	 * TODO
	 * @since 999
	 */
	public final class TabStop {
	    public enum Alignment {
	        CENTER,
	        LEFT,
	        RIGHT,
	        DECIMAL
	    }
	    
	    private final double position;
	    private final Alignment alignment;
	
	    /**
	     * Creates an immutable {@code TabStop} instance.
	     *
	     * @param position the tab stop position in points
	     * @param alignment the alignment
     	*/
	    public TabStop(double position, Alignment alignment) {
	        this.position = position;
	        this.alignment = alignment;
	    }
	    
	    /**
	     * Returns the position, in points, of the tab.
	     * @return the position of the tab
	     */
	    public double getPosition() {
	        return position;
	    }
	    
	    /**
	     * Returns the alignment of the tab.
	     * @return the alignment of the tab
	     */
	    public Alignment getAlignment() {
	        return alignment;
	    }
	    
	    // TODO hashCode, equals, toString
	}


## Alternatives

None known.



## Risks and Assumptions

Possible incompatibility with custom controls which define similar property or a property with the same name. 



## Dependencies

None.
