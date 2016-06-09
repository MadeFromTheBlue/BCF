package blue.made.bcf;

/**
 * Created by Sam Sartor on 5/13/2016.
 */
public abstract class BCFCollection extends BCFItem implements Iterable<BCFItem> {
	protected BCFCollection(BCFType type) {
		super(type);
	}

	@Override
	public boolean isCollection() {
		return true;
	}

	@Override
	public BCFCollection asCollection() {
		return this;
	}

	/**
	 * Copies this collection's elements into an array, throwing an exception if the elements of this collection are a non-constant type.
	 */
	public abstract BCFArray convertToArray();

	/**
	 * Copies this collection's elements into a list.
	 */
	public abstract BCFList convertToList();

}
