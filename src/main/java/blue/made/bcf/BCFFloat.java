package blue.made.bcf;

import java.io.IOException;


/**
 * Created by Sam Sartor on 3/5/2016.
 */
public class BCFFloat extends BCFNumeric {
	public float data;

	BCFFloat() {
		super(BCFType.FLOAT);
	}

	public BCFFloat(float data) {
		this();
		this.data = data;
	}

	@Override
	public void write(BCFWriter writer) throws IOException {
		writer.write(data);
	}

	@Override
	protected void readData(BCFReader reader) throws IOException {
		data = reader.readFloat();
	}

	@Override
	public Number asNumeric() {
		return data;
	}
}
