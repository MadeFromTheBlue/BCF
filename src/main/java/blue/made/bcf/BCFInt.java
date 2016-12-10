package blue.made.bcf;

import java.io.IOException;

/**
 * Created by Sam Sartor on 3/5/2016.
 */
public class BCFInt extends BCFNumeric {
    public int data;

    BCFInt() {
        super(BCFType.INT);
    }

    public BCFInt(int data) {
        this();
        this.data = data;
    }

    @Override
    public void write(BCFWriter writer) throws IOException {
        writer.write(data);
    }

    @Override
    protected void readData(BCFReader reader) throws IOException {
        data = reader.readInt();
    }

    @Override
    public Number asNumeric() {
        return data;
    }
}
