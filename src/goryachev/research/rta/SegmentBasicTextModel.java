package goryachev.research.rta;

import java.util.List;
import jfx.incubator.scene.control.richtext.TextPos;
import jfx.incubator.scene.control.richtext.model.BasicTextModel;
import jfx.incubator.scene.control.richtext.model.RichParagraph;


public class SegmentBasicTextModel extends BasicTextModel
{
	protected final SegmentBasicTextContent content;

	public SegmentBasicTextModel()
	{
		this( new SegmentBasicTextContent() );
		// TODO Register a custom DataHandler for copy/paste, when StyledTextModel.replace can process INLINE_NODES.
	}

	private SegmentBasicTextModel( SegmentBasicTextContent segContent )
	{
		super( segContent );
		content = segContent;
	}

    public void replaceSegment( TextPos start, TextPos end, AbstractSegment<?> customSegment )
    {
		// FIXME var ch = UndoableChange.create(this, start, end);				// com.sun.UndoableChange not accessible -> protected UndoableChange createUndo( ... ) ???
    	if ( ! start.equals( end ) ) content.removeRange( start, end );
		content.insertSegment( start.index(), start.offset(), customSegment );
		// var newEnd = TextPos.ofLeading( pos.index(), pos.offset() + 1 );
        // FIXME add(ch, newEnd);												// is private -> protected void addUndo( ... ) ???
    	fireChangeEvent( start, end, 0, 0, 0 );
   }

	@Override public RichParagraph getParagraph( int index )
	{
		var rb = RichParagraph.builder();

		for ( var seg : content.getSegments( index ) )
		{
			if ( seg instanceof TextSegment )
			{
				if ( seg.length() > 0 )
				{
					rb.addSegment( seg.toString() );
				}
			}
			else rb.addInlineNode( seg );
		}

		return rb.build();
	}

	public List<AbstractSegment<?>> getParagraphSegments( int index )
	{
		return content.getSegments( index );
	}
}
