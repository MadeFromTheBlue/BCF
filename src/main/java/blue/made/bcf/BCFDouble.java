package blue.made.bcf;

import java.io.IOException;

/**
 * Created by Sam Sartor on 3/5/2016.
 */
public class BCFDouble extends BCFNumeric {
	public double data;

	BCFDouble() {
		super(BCFType.DOUBLE);
	}

	public BCFDouble(double data) {
		this();
		this.data = data;
	}

	@Override
	public void write(BCFWriter writer) throws IOException {
		writer.write(data);
	}

	@Override
	protected void readData(BCFReader reader) throws IOException {
		data = reader.readDouble();
	}

	@Override
	public Number asNumeric() {
		return data;
	}
}
