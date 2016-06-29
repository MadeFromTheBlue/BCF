package blue.made.bcf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Created by Sam Sartor on 3/5/2016.
 */
public class BCFString extends BCFItem {
	public static final Charset charset = StandardCharsets.UTF_8;

	public String data;

	BCFString() {
		super(BCFType.STRING);
	}

	public BCFString(String s) {
		this();
		data = s;
	}

	public String toString() {
		return "\"" + this.data + "\"";
	}

	@Override
	public void write(BCFWriter writer) throws IOException {
		writer.write(data);
	}

	@Override
	protected void readData(BCFReader reader) throws IOException {
		data = reader.readString();
	}

	public boolean isString() {
		return true;
	}
	public BCFString asStringItem() {
		return this;
	}
	public String asString() {
		return this.data;
	}
	public String asString(String ifNotString) {
		return this.data;
	}

	public ByteBuf decodeBase64() {
		return Unpooled.wrappedBuffer(Base64.getDecoder().decode(this.data));
	}
}
