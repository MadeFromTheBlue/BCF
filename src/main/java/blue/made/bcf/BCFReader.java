package blue.made.bcf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;

import java.io.*;

/**
 * Created by Sam Sartor on 5/11/2016.
 */
public class BCFReader {
    public class List extends BCFReader {
        List() {
            super(BCFReader.this.in);
        }

        @Override
        protected void onEnd() {
            super.onEnd();
            BCFReader.this.finish();
        }
    }

    public class Array extends BCFReader {
        public final BCFType itemType;
        public final int size;
        private int i = 0;

        Array(BCFType itemType, int size) {
            super(BCFReader.this.in);
            this.itemType = itemType;
            if (size <= 0) {
                this.size = 0;
                nextType = BCFType.END;
                onEnd();
            } else {
                this.size = size;
                nextType = itemType;
            }
        }

        @Override
        protected void finish() {
            super.finish();
            i++;
            if (i >= size) {
                onEnd();
            }
        }

        @Override
        protected void preread() throws IOException {
        }

        @Override
        protected void onEnd() {
            super.onEnd();
            BCFReader.this.finish();
        }
    }

    public class Map extends BCFReader {
        private String nextName = null;

        Map() {
            super(BCFReader.this.in);
        }

        @Override
        protected void preread() throws IOException {
            byte[] namebytes;
            try {
                namebytes = new byte[in.readByte() & 0xFF];
            } catch (EOFException e) {
                nextName = null;
                nextType = BCFType.END;
                return;
            }
            in.readFully(namebytes);
            nextName = new String(namebytes, BCFString.charset);
            nextType = BCFType.from(in.readByte());
        }

        /**
         * Gets the name of the {@link #next() current item}.
         *
         * @return
         */
        public String currentName() throws IOException {
            return nextName;
        }

        @Override
        protected void onEnd() {
            super.onEnd();
            BCFReader.this.finish();
        }
    }

    private final DataInput in;
    protected boolean childInProgress = false;
    protected boolean atEnd = false;
    protected boolean ready = false;
    protected BCFType nextType = null;

    public BCFReader(DataInput stream) {
        in = stream;
    }

    public BCFReader(InputStream stream) {
        this((DataInput) new DataInputStream(stream));
    }

    public BCFReader(ByteBuf dat) {
        this((DataInput) new ByteBufInputStream(dat));
    }

    protected void preread() throws IOException {
        try {
            byte typeid = in.readByte();
            nextType = BCFType.from(typeid);
        } catch (EOFException e) {
            nextType = BCFType.END;
        }
    }

    /**
     * Checks if {@link #next()} is legal to call. This does not check the next tag in the sequence (so will
     * return true even if the next tag is END or if the stream is at EOF), it merely checks that the current item is
     * marked as fully processed and that a new tag can be started with {@link #next()}. The point at which the current
     * item is marked as fully processed depends on the item in question. Arrays are marked as soon as the last item
     * is read while Maps and lists require a final next() call to find the end marker before being considered complete.
     * Primitives (ints, strings, raws) are marked by their respective read calls before returning.
     */
    public boolean canNext() {
        return !childInProgress;
    }

    /**
     * Updates the current item to the next item in the stream by reading the type data (if not an array) and
     * name (if a map), but does not read the item data itself. Can only be called if the active current item is
     * done being read.
     * <br><br>
     * Do not confuse the <I>current</I> item with <i>this</i> item. For example, this BCFReader object may have been
     * returned from {@link #startMap} or a collection start. When that collection contains no more items, {@link #next()}
     * will return false. Each of the items in that collection would appear as the current item, prepared sequentially
     * for reading by {@link #next()}. The current item is just whatever BCF item appears next in the stream from
     * the perspective of this reader.
     *
     * @return true if {@link #next()} can be called
     * @throws IOException           If some other read error occurs (not EOF)
     * @throws IllegalStateException If the current item is still in progress
     * @see #canNext()
     */
    public boolean next() throws IOException {
        if (childInProgress)
            throw new IllegalStateException("A child of this item is still being read, it must be finished");
        if (atEnd) return false;
        if (!ready) {
            preread();
            if (nextType == BCFType.END) {
                onEnd();
            } else {
                ready = true;
                childInProgress = true;
            }
        }
        return ready;
    }

    private void checkReady() throws IOException {
        if (atEnd) throw new IllegalStateException("No items available, at end");
        if (!ready) throw new IllegalStateException("Not ready for reading, call next()");
    }

    protected void onEnd() {
        atEnd = true;
    }

    protected void finish() {
        ready = false;
        childInProgress = false;
    }

    /**
     * Gets the type of the {@link #next() current item}.
     *
     * @return
     */
    public BCFType currentType() {
        return nextType;
    }

    public Number readNumber() throws IOException {
        checkReady();
        Number n;
        switch (nextType) {
            case BYTE:
                n = in.readByte();
                break;
            case SHORT:
                n = in.readShort();
                break;
            case INT:
                n = in.readInt();
                break;
            case LONG:
                n = in.readLong();
                break;
            case FLOAT:
                n = in.readFloat();
                break;
            case DOUBLE:
                n = in.readDouble();
                break;
            default:
                throw new IllegalStateException("BCF type " + nextType + " is not a number");
        }
        finish();
        return n;
    }

    public byte readByte() throws IOException {
        return readNumber().byteValue();
    }

    public short readShort() throws IOException {
        return readNumber().shortValue();
    }

    public int readInt() throws IOException {
        return readNumber().intValue();
    }

    public long readLong() throws IOException {
        return readNumber().longValue();
    }

    public float readFloat() throws IOException {
        return readNumber().floatValue();
    }

    public double readDouble() throws IOException {
        return readNumber().doubleValue();
    }

    public boolean readBoolean() throws IOException {
        checkReady();
        if (nextType != BCFType.BOOLEAN) throw new IllegalStateException("BCF type " + nextType + " is not a boolean");
        boolean b = in.readBoolean();
        finish();
        return b;
    }

    public String readString() throws IOException {
        checkReady();
        if (nextType != BCFType.STRING) throw new IllegalStateException("BCF type " + nextType + " is not a string");
        byte[] bytes = new byte[in.readInt()];
        in.readFully(bytes);
        String s = new String(bytes, BCFString.charset);
        finish();
        return s;
    }

    public ByteBuf readRaw() throws IOException {
        checkReady();
        if (nextType != BCFType.RAW) throw new IllegalStateException("BCF type " + nextType + " is not a raw");
        byte[] bytes = new byte[in.readInt()];
        in.readFully(bytes);
        finish();
        return Unpooled.wrappedBuffer(bytes);
    }


    public Map startMap() throws IOException {
        checkReady();
        if (nextType != BCFType.MAP) throw new IllegalStateException("BCF type " + nextType + " is not a map");
        return new Map();
    }

    public List startList() throws IOException {
        checkReady();
        if (nextType != BCFType.LIST) throw new IllegalStateException("BCF type " + nextType + " is not a list");
        return new List();
    }

    public Array startArray() throws IOException {
        checkReady();
        if (nextType != BCFType.ARRAY) throw new IllegalStateException("BCF type " + nextType + " is not an array");
        BCFType artype = BCFType.from(in.readByte());
        int len = in.readInt();
        return new Array(artype, len);
    }

    public BCFReader startCollection() throws IOException {
        switch (nextType) {
            case ARRAY:
                return startArray();
            case LIST:
                return startList();
            case MAP:
                return startMap();
            default:
                throw new IllegalStateException("BCF type " + nextType + " is not a collection");
        }
    }

    public BCFItem read() throws IOException {
        checkReady();
        if (nextType == BCFType.END) return null;
        BCFItem i = nextType.createDefault();
        i.readData(this);
        return i;
    }

    public void gotoEnd() throws IOException {
        while (next()) ;
    }
}