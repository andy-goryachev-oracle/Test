package goryachev.research.rta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import jfx.incubator.scene.control.richtext.TextPos;
import jfx.incubator.scene.control.richtext.model.BasicTextModel.Content;
import jfx.incubator.scene.control.richtext.model.StyleAttributeMap;

/**
 * This is a custom implementation of the BasicTextModel.Content interface to
 * be used with a RichTextArea BasicTextModel. This is where all the document
 * editing work gets done: insert, replace, and delete. It doesn't currently
 * support styled text, but it does support custom AbstractSegment types.
 */
public class SegmentBasicTextContent implements Content
{
	private List<List<AbstractSegment<?>>> paragraphs = new ArrayList<>
	(
		List.of( new ArrayList<>( List.of(new TextSegment("")) ) )
	);

	private record SegmentPos( int segNo, int offset ) {}

	/**
	 * For a given paragraph index, find the segment index and the
	 * segment offset that contains the paragraph offset position.
	 */
	private SegmentPos findSegment( int index, int offset )
	{
		int lastLen = 0, length = 0, segNo = -1;
		var segList = paragraphs.get( index );

		while ( length < offset && ++segNo < segList.size() )
		{
			lastLen = segList.get( segNo ).length();
			length += lastLen;
		}

		if ( length > offset ) // offset is within the last segment processed
		{
			offset -= (length - lastLen); // adjust offset to within segment
			return new SegmentPos( segNo, offset );
		}

		// offset is between the last segment processed and the next one
		if ( length == offset ) return new SegmentPos( Math.max( 0, segNo ), lastLen );

		return new SegmentPos( segNo, offset - length );  // after this paragraph
	}

	public List<AbstractSegment<?>> getSegments( int index )
	{
		return paragraphs.get( index );
	}

	@Override public String getText( int index )
	{
		return paragraphs.get( index ).stream()
			.map( seg -> seg.getNavigationText() )
			.collect( Collectors.joining() );
	}

	@Override public void insertLineBreak( int index, int offset )
	{
		if ( index >= paragraphs.size() ) paragraphs.add( new ArrayList<>( List.of(new TextSegment("") )) );
		else
		{
			var insertPos = findSegment( index, offset );
			var p = paragraphs.get( index );
			var segNo = insertPos.segNo();
			var seg = p.get( segNo );

			if ( segNo == p.size()-1 && insertPos.offset() == seg.length() )
				paragraphs.add( index+1, new ArrayList<>( List.of(new TextSegment("")) ) );
			else
			{
				if ( insertPos.offset() < seg.length() )  // split within a segment
    			{
    				var leftSeg = seg.subSequence( 0, insertPos.offset() );
    				var rightSeg = seg.subSequence( insertPos.offset(), seg.length() );

    				p.set( segNo, leftSeg );
    				p.add( segNo+1, rightSeg );
    			}

    			var rightList = p.subList( segNo+1, p.size() );
    			paragraphs.add( index+1, new ArrayList<>( rightList ) );
    			rightList.clear();
			}
		}
	}

	public void insertSegment( int index, int offset, AbstractSegment<?> customSegment )
	{
		if ( customSegment == null ) return;

		var insertPos = findSegment( index, offset );
		var p = paragraphs.get( index );
		var segNo = insertPos.segNo();
		var seg = p.get( segNo );

		var leftSeg = seg.subSequence( 0, insertPos.offset() );
		var rightSeg = seg.subSequence( insertPos.offset(), seg.length() );

		p.set( segNo, leftSeg );
		p.add( segNo+1, customSegment );
		if ( rightSeg.length() > 0 ) p.add( segNo+2, rightSeg );
	}

	@Override public int insertTextSegment( int index, int offset, String text, StyleAttributeMap styles )
	{
		var insertPos = findSegment( index, offset );
		var p = paragraphs.get( index );
		var segNo = insertPos.segNo();
		var seg = p.get( segNo );

		var leftSeg = seg.subSequence( 0, insertPos.offset() );
		var rightSeg = seg.subSequence( insertPos.offset(), seg.length() );
		var newTextSeg = new TextSegment( text );

		// Merge with left & right segments, and replace/insert as needed
		var mergeLeft = leftSeg.join( newTextSeg );

		if ( mergeLeft.isPresent() )
		{
			var mergeRight = mergeLeft.get().join( rightSeg );

			if ( mergeRight.isPresent() ) leftSeg = mergeRight.get();
			else
			{
				if ( rightSeg.length() > 0 ) p.add( segNo+1, rightSeg );
				leftSeg = mergeLeft.get();
			}
		}
		else // Merge with left failed, so try merging with right
		{
			var mergeRight = newTextSeg.join( rightSeg );

			if ( mergeRight.isPresent() ) p.add( segNo+1, mergeRight.get() );
			else
			{
				var segPos = segNo;
				if ( newTextSeg.length() > 0 ) p.add( ++segPos, newTextSeg );
				if ( rightSeg.length() > 0 ) p.add( ++segPos, rightSeg );
			}
		}

		p.set( segNo, leftSeg );

		return text.length();
	}

	@Override public boolean isWritable()
	{
		return true;
	}

	@Override public void removeRange( TextPos start, TextPos end )
	{
		int startIndex = start.index(), endIndex = end.index();
		var startPos = findSegment( startIndex, start.offset() );
		var endPos = findSegment( endIndex, end.offset() );

	    // 1. Get start segment and trim the end off of it
		var leftSegNo = startPos.segNo();
	    var startList = paragraphs.get( startIndex );
	    var leftSeg = startList.get( leftSegNo );
		leftSeg = leftSeg.subSequence( 0, startPos.offset() );

		// 2. Get end segment and trim the start off of it
		var rightSegNo = endPos.segNo();
		var endList = paragraphs.get( endIndex );
		var rightSeg = endList.get( rightSegNo );
		rightSeg = rightSeg.subSequence( endPos.offset(), rightSeg.length() );

		// 3. Try to merge the two segments
		var merged = leftSeg.join( rightSeg );

		if ( merged.isPresent() ) endList.set( rightSegNo, merged.get() );
		else
		{
			if ( leftSeg.length() > 0 ) startList.set( leftSegNo++, leftSeg );
			if ( rightSeg.length() > 0 ) endList.set( rightSegNo, rightSeg );
			else rightSegNo++;
		}

		// 4. Cleanup paragraphs
		if ( startList == endList ) // Start and end paragraphs are the same
		{
			leftSegNo = Math.min( leftSegNo, rightSegNo );
			startList.subList( leftSegNo, rightSegNo ).clear();
		}
		else // Remove intermediary, and join start & end paragraphs
		{
			paragraphs.subList( startIndex+1, endIndex+1 ).clear();
    		startList.subList( leftSegNo, startList.size() ).clear();
    		endList.subList( 0, rightSegNo ).clear();
    		startList.addAll( endList );
		}
	}

	@Override public int size()
	{
		return paragraphs.size();
	}
}
