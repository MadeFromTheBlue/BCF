package blue.made.bcf;

import java.io.IOException;

/**
 * Created by Sam Sartor on 3/5/2016.
 */
public class BCFLong extends BCFNumeric {
    public long data;

    BCFLong() {
        super(BCFType.LONG);
    }

    public BCFLong(long data) {
        this();
        this.data = data;
    }

    @Override
    public void write(BCFWriter writer) throws IOException {
        writer.write(data);
    }

    @Override
    protected void readData(BCFReader reader) throws IOException {
        data = reader.readLong();
    }

    @Override
    public Number asNumeric() {
        return data;
    }
}
