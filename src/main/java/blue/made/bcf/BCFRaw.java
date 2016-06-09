package blue.made.bcf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.nio.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * A type used to store a raw blob of bytes. This type should be used to store numerical arrays (like int[]) when
 * possible as well as normal binary data such as images.
 *
 * @see BCF#store(ByteBuf)
 * @see BCF#store(ByteBuf, int, int)
 * @see BCF#store(ByteBuffer)
 * @see BCF#store(byte...)
 * @see BCF#store(short...)
 * @see BCF#store(int...)
 * @see BCF#store(long...)
 * @see BCF#store(float...)
 * @see BCF#store(double...)
 */
public class BCFRaw extends BCFItem {
	public ByteBuf buf;

	BCFRaw() {
		super(BCFType.RAW);
	}

	/**
	 * Wraps the given data
	 */
	public BCFRaw(ByteBuf buf) {
		this();
		this.buf = buf.duplicate();
	}

	/**
	 * Wraps the given data
	 */
	public BCFRaw(ByteBuf buf, int from, int to) {
		this();
		this.buf = buf.slice(from, to - from);
	}

	/**
	 * Wraps the given data
	 */
	public BCFRaw(byte[] bytes) {
		this();
		buf = Unpooled.wrappedBuffer(bytes);
	}

	public BCFRaw(int initialCapacity) {
		this();
		buf = Unpooled.buffer(initialCapacity);
	}

	public BCFRaw(int initialCapacity, int maxCapacity) {
		this();
		buf = Unpooled.buffer(initialCapacity, maxCapacity);
	}

	private static final char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	public String toString() {
		StringBuilder build = new StringBuilder();
		build.ensureCapacity(buf.readableBytes() * 2);
		for (int i = buf.readerIndex(); i < buf.writerIndex(); i++){
			int b = buf.getByte(i) & 0xFF;
			build.append(hex[b >> 4]);
			build.append(hex[b & 0xF]);
		}
		return build.toString();
	}

	@Override
	public void write(BCFWriter writer) throws IOException {
		writer.write(buf);
	}

	@Override
	protected void readData(BCFReader reader) throws IOException {
		buf = reader.readRaw();
	}

	public static BCFRaw ofAllWrittenBytes(ByteBuf in) {
		return new BCFRaw(in, in.arrayOffset(), in.writerIndex());
	}

	/**
	 * Copies the duplicates the data so that it is independent of any previous references or wrappers
	 */
	public void fork() {
		buf = buf.copy();
	}

	public String getString() {
		return buf.toString(BCFString.charset);
	}

	public boolean isRaw() {
		return true;
	}
	public BCFRaw asRawItem() {
		return this;
	}
	public ByteBuf asRaw() {
		return this.buf;
	}

	public String endcodeBase64() {
		return new String(Base64.getEncoder().encode(buf.nioBuffer()).array(), StandardCharsets.UTF_8);
	}
}
