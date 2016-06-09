package blue.made.bcf;

import java.io.IOException;

/**
 * Created by Sam Sartor on 3/5/2016.
 */
public class BCFNull extends BCFItem {
	public static final BCFNull INSTANCE = new BCFNull();

	private BCFNull() {
		super(BCFType.NULL);
	}

	@Override
	public void write(BCFWriter writer) throws IOException {
	}

	@Override
	protected void readData(BCFReader reader) throws IOException {
	}

	public boolean isNull() {
		return true;
	}
}
