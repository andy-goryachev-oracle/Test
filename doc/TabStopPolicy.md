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
- support the 'leader' property (symbols to fill the empty space before the tab stop)



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
     * @since 999
     */
    public final ObjectProperty<TabStopPolicy> tabStopPolicyProperty()

    public final TabStopPolicy getTabStopPolicy()

    public final void setTabStopPolicy(TabStopPolicy policy)

### TabStopPolicy

	/**
	 * Tab Stop Policy.
	 */
	public interface TabStopPolicy {
	    /**
	     * Determines whether this policy specifies a fixed tab size in terms of the width or the digit 0
	     * (any positive value), or provides the tab stops relative to document leading edge.
	     *
	     * @return the tab size
	     */
	    public int tabSize();
	
	    /**
	     * @return the non-null list of tab stops 
	     */
	    public List<TabStop> tabStops();
	    
	    /**
	     * First line indent, a positive value or 0.
	     * This value is ignored when {@link #tabSize()} returns a non-zero value.
	     *
	     * TODO
	     * It is unclear whether the TextLayout should support negative values as it might impact the size and
	     * the preferred size of the layout.
	     *
	     * @return the first line indent
	     */
	    public double firstLineIndent();
	
	    /**
	     * Provides default tab stops (beyond the last tab stop specified by {@code #tabStops()}.
	     * This value is ignored when {@link #tabSize()} returns a non-zero value.
	     *
	     * TODO
	     * It is unclear how to specify NONE value (negative perhaps?).  MS Word does not allow for NONE.
	     * @return the default tab stops, in points.
	     */
	    public double defaultStops();
	    
	    // TODO: factory method to create a simple fixed tab size policy
	}


### TabStop

	/**
	 * This class encapsulates a single tab stop.
	 * A tab stop is at a specified distance from the
	 * left margin, aligns text in a specified way, and has a specified leader.
	 * TabStops are immutable, and usually contained in {@link TabStopPolicy}.
	 */
	public class TabStop {
	    public enum Alignment {
	        CENTER,
	        LEFT,
	        RIGHT,
	        DECIMAL
	        // TODO BAR?
	    }
	    
	    public enum Leader {
	        /** Lead none */
	        NONE,
	        /** Lead dots */
	        DOTS,
	        /** Lead hyphens */
	        HYPHENS,
	        /** Lead underline */
	        UNDERLINE,
	        /** Lead thickline */
	        THICK_LINE,
	        /** Lead equals */
	        EQUALS
	    }
	    
	    private final double position;
	    private final Alignment alignment;
	    private final Leader leader;
	
	    // TODO this might be a record
	    public TabStop(double position, Alignment alignment, Leader leader) {
	        this.position = position;
	        this.alignment = alignment;
	        this.leader = leader;
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
	
	    /**
	     * Returns the leader of the tab.
	     * @return the leader of the tab
	     */
	    public Leader getLeader() {
	        return leader;
	    }
	    
	    // TODO equals, toString
	}


## Alternatives

None known.



## Risks and Assumptions

Possible incompatibility with custom controls which define similar property or a property with the same name. 



## Dependencies

None.
