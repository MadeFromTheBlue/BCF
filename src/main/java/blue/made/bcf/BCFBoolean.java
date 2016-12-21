package blue.made.bcf;

import java.io.IOException;

/**
 * Created by Sumner Evans on 2016/12/20.
 */
public class BCFBoolean extends BCFItem {
    private boolean data;

    protected BCFBoolean() {
        super(BCFType.BOOLEAN);
    }

    public BCFBoolean(boolean data) {
        this();
        this.data = data;
    }

    @Override
    public void write(BCFWriter writer) throws IOException {
        writer.write(data);
    }

    @Override
    protected void readData(BCFReader reader) throws IOException {
        data = reader.readBoolean();
    }

    @Override
    public boolean isBoolean() {
        return true;
    }

    @Override
    public boolean asBoolean() {
        return data;
    }

    @Override
    public String toString() {
        return data ? "true" : "false";
    }
}
