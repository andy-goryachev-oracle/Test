package goryachev.research.rta;
import java.util.Optional;
import java.util.function.Supplier;

import javafx.scene.Node;

/**
 * An subclassed AbstractSegment holds a data object and has methods to:
 * 1) interact with other segments and 2) to create a Node for presentation.
 *
 * See LabelSegment for an example of an AbstractSegment which can be
 * embedded in the text.
 *
 * You may extend AbstractSegment to add your own custom
 * segment types like maybe a HyperlinkSegment or ImageSegment.
 *
 * @author Jurgen
 */
public abstract class AbstractSegment<T> implements Supplier<Node>
{
	protected final T data;

	public AbstractSegment( T data )
	{
		this.data = data;
	}

	public String getNavigationText() { return "ª"; }
	public T getData() { return data; }
	public int length() { return 1; }

	public AbstractSegment<?> subSequence( int start, int end )
	{
		if ( start != end )  return this;
		return new TextSegment("");
	}

	public Optional<AbstractSegment<?>> join( AbstractSegment<?> nextSeg )
	{
		return Optional.empty();
	}

	public abstract Node createNode( String style );

	@Override public Node get() { return createNode( null ); }

	/**
	 * RichTextFX uses this for undo and redo.
	 */
    @Override public boolean equals( Object obj )
    {
    	if ( obj == this )  return true;
    	else if ( obj instanceof AbstractSegment && getClass().equals( obj.getClass() ) )
        {
            return getData().equals( ((AbstractSegment<?>) obj).getData() );
        }

        return false;
    }

    @Override
	public String toString()
    {
    	return getNavigationText();
    }
}
