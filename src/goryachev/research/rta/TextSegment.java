package goryachev.research.rta;
import java.util.Optional;

import javafx.scene.Node;
import javafx.scene.text.Text;

public class TextSegment extends AbstractSegment<String>
{
	public TextSegment( String text )
	{
		super( text );
	}

	@Override
	public Node createNode( String style )
	{
		Text  textNode = new Text( getData() );
		if ( style != null && ! style.isBlank() ) textNode.getStyleClass().add( style );
		return textNode;
	}

	@Override
	public String getNavigationText() { return getData(); }

	@Override
	public int length() { return getData().length(); }

	@Override
	public AbstractSegment<?> subSequence( int start, int end )
	{
		if ( start == 0 && end == length() )  return this;
		return new TextSegment( getData().substring( start, end ) );
	}

	@Override
	public Optional<AbstractSegment<?>> join( AbstractSegment<?> nextSeg )
	{
		if ( nextSeg instanceof TextSegment )
		{
			return Optional.of( new TextSegment( getData() + nextSeg.getNavigationText() ) );
		}
		return Optional.empty();
	}

}
