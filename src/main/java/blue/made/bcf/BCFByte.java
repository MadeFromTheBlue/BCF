package blue.made.bcf;

import java.io.IOException;

/**
 * Created by Sam Sartor on 3/5/2016.
 */
public class BCFByte extends BCFNumeric {
	public byte data;

	BCFByte() {
		super(BCFType.BYTE);
	}

	public BCFByte(byte data) {
		this();
		this.data = data;
	}

	@Override
	public void write(BCFWriter writer) throws IOException {
		writer.write(data);
	}

	@Override
	protected void readData(BCFReader reader) throws IOException {
		data = reader.readByte();
	}

	@Override
	public Number asNumeric() {
		return data;
	}
}
