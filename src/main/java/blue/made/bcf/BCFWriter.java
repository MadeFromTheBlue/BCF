package blue.made.bcf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Sam Sartor on 5/11/2016.
 */
public class BCFWriter {
    public class List extends BCFWriter {
        List() {
            super(BCFWriter.this.out);
        }

        @Override
        protected void postWrite() {
            childInProgress = false;
        }

        public void end() throws IOException {
            checkState();
            out.writeByte(BCFType.END.id);
            atEnd = true;
            BCFWriter.this.postWrite();
        }
    }

    public class Map extends BCFWriter {
        private boolean ready = false;

        Map() {
            super(BCFWriter.this.out);
        }

        public void writeName(String name) throws IOException {
            checkState();
            if (ready) throw new IllegalStateException("There is already a name written for the next item");
            byte[] bytes = name.getBytes(BCFMap.encoding);
            if (bytes.length > 255) throw new IllegalArgumentException("The name is over 255 bytes");
            out.writeByte(bytes.length);
            out.write(bytes);
            ready = true;
        }


        @Override
        protected void preWrite(BCFType type) throws IOException {
            checkState();
            if (!ready) throw new IllegalStateException("This item does not have a name written");
            out.writeByte(type.id);
        }

        @Override
        protected void postWrite() {
            this.childInProgress = false;
            this.ready = false;
        }

        public void end() throws IOException {
            checkState();
            if (!ready) out.writeByte(0); // empty name string
            out.writeByte(BCFType.END.id);
            atEnd = true;
            BCFWriter.this.postWrite();
        }
    }

    public class Array extends BCFWriter {
        private int i = 0;
        public final BCFType type;
        public final int length;

        Array(BCFType type, int length) {
            super(BCFWriter.this.out);
            this.type = type;
            this.length = length;
            if (length <= 0) {
                atEnd = true;
                BCFWriter.this.postWrite();
            }
        }

        @Override
        protected void preWrite(BCFType type) throws IOException {
            checkState();
            if (this.type != type)
                throw new IllegalStateException(type + " does not match the current type of this array (" + this.type + ")");
        }

        @Override
        protected void postWrite() {
            childInProgress = false;
            if (++i >= length) {
                atEnd = true;
                BCFWriter.this.postWrite();
            }
        }
    }

    protected boolean childInProgress = false;
    protected boolean atEnd = false;
    private final DataOutput out;

    public BCFWriter(DataOutput stream) {
        out = stream;
    }

    public BCFWriter(OutputStream stream) {
        this((DataOutput) new DataOutputStream(stream));
    }

    public BCFWriter(ByteBuf dat) {
        this((DataOutput) new ByteBufOutputStream(dat));
    }

    public void writeNull() throws IOException {
        preWrite(BCFType.NULL);
        postWrite();
    }

    public void write(byte data) throws IOException {
        preWrite(BCFType.BYTE);
        out.writeByte(data);
        postWrite();
    }

    public void write(short data) throws IOException {
        preWrite(BCFType.SHORT);
        out.writeShort(data);
        postWrite();
    }

    public void write(int data) throws IOException {
        preWrite(BCFType.INT);
        out.writeInt(data);
        postWrite();
    }

    public void write(long data) throws IOException {
        preWrite(BCFType.LONG);
        out.writeLong(data);
        postWrite();
    }

    public void write(float data) throws IOException {
        preWrite(BCFType.FLOAT);
        out.writeFloat(data);
        postWrite();
    }

    public void write(double data) throws IOException {
        preWrite(BCFType.DOUBLE);
        out.writeDouble(data);
        postWrite();
    }

    public void write(boolean data) throws IOException {
        preWrite(BCFType.BOOLEAN);
        out.writeBoolean(data);
        postWrite();
    }

    public void write(String data) throws IOException {
        preWrite(BCFType.STRING);
        byte[] bytes = data.getBytes(BCFString.charset);
        out.writeInt(bytes.length);
        out.write(bytes);
        postWrite();
    }

    public void write(ByteBuf data) throws IOException {
        preWrite(BCFType.RAW);
        int len = data.readableBytes();
        out.writeInt(len);
        if (data.hasArray()) out.write(data.array(), data.readerIndex() + data.arrayOffset(), len);
        else if (out instanceof OutputStream) data.getBytes(data.readerIndex(), (OutputStream) out, len);
        else {
            byte[] bytes = new byte[len];
            data.getBytes(data.readerIndex(), bytes);
            out.write(bytes);
        }
        postWrite();
    }

    public List startList() throws IOException {
        preWrite(BCFType.LIST);
        childInProgress = true;
        return new List();
    }

    public Map startMap() throws IOException {
        preWrite(BCFType.MAP);
        childInProgress = true;
        return new Map();
    }

    public Array startArray(BCFType type, int length) throws IOException {
        preWrite(BCFType.ARRAY);
        childInProgress = true;
        out.writeByte(type.id);
        out.writeInt(length < 0 ? 0 : length);
        return new Array(type, length);
    }

    protected void checkState() {
        if (atEnd)
            throw new IllegalStateException("The current BCF item has been completed, no more writes are possible");
        if (childInProgress)
            throw new IllegalStateException("A child of this item is in progress, it must be finished");
    }

    protected void preWrite(BCFType type) throws IOException {
        checkState();
        out.writeByte(type.id);
    }

    protected void postWrite() {
        childInProgress = false;
    }

    public void write(BCFItem i) throws IOException {
        i.write(this);
    }
}
