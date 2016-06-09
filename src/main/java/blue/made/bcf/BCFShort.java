package blue.made.bcf;

import java.io.IOException;

/**
 * Created by Sam Sartor on 3/5/2016.
 */
public class BCFShort extends BCFNumeric {
	public short data;

	BCFShort() {
		super(BCFType.SHORT);
	}

	public BCFShort(short data) {
		this();
		this.data = data;
	}

	@Override
	public void write(BCFWriter writer) throws IOException {
		writer.write(data);
	}

	@Override
	protected void readData(BCFReader reader) throws IOException {
		data = reader.readShort();
	}

	@Override
	public Number asNumeric() {
		return data;
	}
}
