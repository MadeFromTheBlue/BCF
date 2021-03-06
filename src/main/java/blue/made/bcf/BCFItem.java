package blue.made.bcf;

import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * Created by Sam Sartor on 3/5/2016.
 */

public abstract class BCFItem {
    public final BCFType type;

    protected BCFItem(BCFType type) {
        this.type = type;
    }

    public abstract void write(BCFWriter writer) throws IOException;

    protected abstract void readData(BCFReader reader) throws IOException;

    public JsonElement toJson() {
        return BCF.toJson(this);
    }

    // Numeric
    public boolean isNumeric() {
        return false;
    }

    public BCFNumeric asNumericItem() {
        throw new IllegalStateException("This BCFItem is not a number");
    }

    public BCFNumeric asNumericItem(Number ifNaN) {
        return BCF.store(ifNaN);
    }

    public Number asNumeric() {
        throw new IllegalStateException("This BCFItem is not a number");
    }

    public Number asNumeric(Number ifNaN) {
        return ifNaN;
    }

    // Boolean
    public boolean isBoolean() {
        return false;
    }

    public boolean asBoolean() {
        throw new IllegalStateException("This BCFItem is not a boolean");
    }

    // Map
    public boolean isMap() {
        return false;
    }

    public BCFMap asMap() {
        throw new IllegalStateException("This BCFItem is not a map");
    }

    // String
    public boolean isString() {
        return false;
    }

    public BCFString asStringItem() {
        throw new IllegalStateException("This BCFItem is not a string");
    }

    public String asString() {
        throw new IllegalStateException("This BCFItem is not a string");
    }

    public String asString(String ifNotString) {
        return ifNotString;
    }

    // List
    public boolean isList() {
        return false;
    }

    public BCFList asList() {
        throw new IllegalStateException("This BCFItem is not a list");
    }

    // Raw
    public boolean isRaw() {
        return false;
    }

    public BCFRaw asRawItem() {
        throw new IllegalStateException("This BCFItem is not raw data");
    }

    public ByteBuf asRaw() {
        throw new IllegalStateException("This BCFItem is not raw data");
    }

    // Array
    public boolean isArray() {
        return false;
    }

    public BCFArray asArray() {
        throw new IllegalStateException("This BCFItem is not an array");
    }

    // Collection
    public boolean isCollection() {
        return false;
    }

    public BCFCollection asCollection() {
        throw new IllegalStateException("This BCFItem is not a collection");
    }

    // Null
    public boolean isNull() {
        return false;
    }
}
