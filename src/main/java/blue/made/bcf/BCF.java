package blue.made.bcf;

import com.google.gson.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Binary Container Format
 * Created by Sam Sartor on 3/5/2016.
 */
public class BCF {
    private static final Gson gson = new Gson();

    public static BCFItem read(BCFReader reader) throws IOException {
        reader.next();
        return reader.read();
    }

    /**
     * Convert the given BCF data into the nearest Json equivalent. BCFRaws will be converted to a RFC4648 base64 string
     * of the stored bytes, both BCFLists and BCFArrays will be converted into arrays. All other types have a
     * close approximation.
     *
     * @param bcf The BCF data to convert
     * @return A Json element containing the data
     * @see BCFItem#toJson()
     */
    public static JsonElement toJson(BCFItem bcf) {
        if (bcf.isNumeric())
            return new JsonPrimitive(bcf.asNumeric());
        if (bcf.isBoolean())
            return new JsonPrimitive(bcf.asBoolean());
        if (bcf.isString())
            return new JsonPrimitive(bcf.asString());
        if (bcf.isRaw())
            return new JsonPrimitive(bcf.asRawItem().endcodeBase64());
        if (bcf.isMap()) {
            JsonObject obj = new JsonObject();
            for (Map.Entry<String, BCFItem> e : bcf.asMap().entrySet()) {
                obj.add(e.getKey(), toJson(e.getValue()));
            }
            return obj;
        }
        if (bcf.isCollection()) {
            JsonArray array = new JsonArray();
            for (BCFItem i : bcf.asCollection().convertToList()) {
                array.add(toJson(i));
            }
            return array;
        }
        return JsonNull.INSTANCE;
    }

    /**
     * Converts a Json element to the nearest BCF equivalent. Arrays will be converted into
     * BCFLists not BCFArrays. All other types have a close approximation.
     *
     * @param json
     * @return
     */
    public static BCFItem fromJson(JsonElement json) {
        if (json.isJsonPrimitive()) {
            JsonPrimitive p = json.getAsJsonPrimitive();
            if (p.isNumber())
                return store(p.getAsNumber());
            if (p.isBoolean())
                return store(p.getAsBoolean());
            if (p.isString())
                return store(p.getAsString());
        }

        if (json.isJsonArray()) {
            BCFList list = new BCFList();
            JsonArray array = json.getAsJsonArray();
            array.forEach(e -> list.add(fromJson(e)));
            return list;
        }

        if (json.isJsonObject()) {
            BCFMap map = new BCFMap();
            JsonObject obj = json.getAsJsonObject();
            obj.entrySet().forEach(e -> map.put(e.getKey(), fromJson(e.getValue())));
            return map;
        }
        return BCFNull.INSTANCE;
    }

    public static BCFBoolean store(boolean b) {
        return new BCFBoolean(b);
    }

    public static BCFByte store(byte b) {
        return new BCFByte(b);
    }

    public static BCFShort store(short s) {
        return new BCFShort(s);
    }

    public static BCFInt store(int i) {
        return new BCFInt(i);
    }

    public static BCFLong store(long l) {
        return new BCFLong(l);
    }

    public static BCFFloat store(float f) {
        return new BCFFloat(f);
    }

    public static BCFDouble store(double d) {
        return new BCFDouble(d);
    }

    public static BCFNumeric store(Number n) {
        if (n instanceof Byte)
            return store(n.byteValue());
        else if (n instanceof Short)
            return store(n.shortValue());
        else if (n instanceof Integer)
            return store(n.intValue());
        else if (n instanceof Long)
            return store(n.longValue());
        else if (n instanceof Float)
            return store(n.floatValue());
        else if (n instanceof Double)
            return store(n.doubleValue());
        else
            return BCFNumeric.storeBest(n.doubleValue());
    }

    public static BCFArray store(byte... bytes) {
        BCFArray out = new BCFArray();
        out.typeUnsafe(BCFType.BYTE);
        for (byte v : bytes) out.addUnsafe(store(v));
        return out;
    }

    public static BCFArray store(short... shorts) {
        BCFArray out = new BCFArray();
        out.typeUnsafe(BCFType.SHORT);
        for (short v : shorts) out.addUnsafe(store(v));
        return out;
    }

    public static BCFArray store(int... ints) {
        BCFArray out = new BCFArray();
        out.typeUnsafe(BCFType.INT);
        for (int v : ints) out.addUnsafe(store(v));
        return out;
    }

    public static BCFArray store(long... longs) {
        BCFArray out = new BCFArray();
        out.typeUnsafe(BCFType.LONG);
        for (long v : longs) out.addUnsafe(store(v));
        return out;
    }

    public static BCFArray store(float... floats) {
        BCFArray out = new BCFArray();
        out.typeUnsafe(BCFType.FLOAT);
        for (float v : floats) out.addUnsafe(store(v));
        return out;
    }

    public static BCFArray store(double... doubles) {
        BCFArray out = new BCFArray();
        out.typeUnsafe(BCFType.DOUBLE);
        for (double v : doubles) out.addUnsafe(store(v));
        return out;
    }

    public static BCFArray store(boolean... bools) {
        BCFArray out = new BCFArray();
        out.typeUnsafe(BCFType.BOOLEAN);
        for (boolean v : bools) out.addUnsafe(store(v));
        return out;
    }

    public static BCFArray store(ByteBuf... bufs) {
        BCFArray out = new BCFArray();
        out.typeUnsafe(BCFType.RAW);
        for (ByteBuf v : bufs) out.addUnsafe(store(v));
        return out;
    }

    public static BCFArray store(String... strings) {
        BCFArray out = new BCFArray();
        out.typeUnsafe(BCFType.STRING);
        for (String v : strings) out.addUnsafe(store(v));
        return out;
    }

    public static BCFRaw store(ByteBuf buf) {
        return new BCFRaw(buf.copy());
    }

    public static BCFRaw store(ByteBuf buf, int from, int to) {
        BCFRaw out = new BCFRaw(buf, from, to);
        out.fork();
        return out;
    }

    public static BCFRaw store(ByteBuffer buf) {
        return new BCFRaw(Unpooled.wrappedBuffer(buf).copy());
    }

    public static BCFString store(String s) {
        return new BCFString("" + s);
    }

    public static BCFList store(Collection<BCFItem> l) {
        BCFList out = new BCFList();
        out.addAll(l);
        return out;
    }

    public static BCFArray storeArray(Collection<BCFItem> l) {
        BCFArray out = new BCFArray();
        out.addAll(l);
        return out;
    }

    public static BCFList store(BCFItem... list) {
        return store(Arrays.asList(list));
    }

    public static BCFArray storeArray(BCFItem... list) {
        return storeArray(Arrays.asList(list));
    }

    public static BCFMap store(Map<String, BCFItem> m) {
        BCFMap out = new BCFMap();
        out.putAll(m);
        return out;
    }

    /**
     * Accepts and stores values of the types:
     * <ul>
     *     <li>{@link BCFItem}</li>
     *     <li>{@link Number} (byte, short, int, float, etc.)</li>
     *     <li>{@link Boolean}</li>
     *     <li>{@link ByteBuf}</li>
     *     <li>{@link ByteBuffer}</li>
     *     <li>{@link String}</li>
     *     <li>{@link Iterable} (where the items can be any of these types)</li>
     *     <li>{@code Object[]} (where the items can be any of these types)</li>
     *     <li>{@code byte[]}, {@code short[]}, {@code float[]}, {@code boolean[]}, etc.</li>
     *     <li>{@link Map} (where the keys are {@link String}s and the values can be any of these types)</li>
     * </ul>
     */
    @SuppressWarnings("unchecked")
    public static BCFItem storeAny(Object value) {
        if (value == null) return BCFNull.INSTANCE;
        if (value instanceof BCFItem) return (BCFItem) value;
        if (value instanceof Number) return BCF.store((Number) value);
        if (value instanceof Boolean) return BCF.store((boolean) value);
        if (value instanceof ByteBuf) return BCF.store((ByteBuf) value);
        if (value instanceof ByteBuffer) return BCF.store((ByteBuffer) value);
        if (value instanceof String) return BCF.store((String) value);
        if (value instanceof Iterable) {
            Iterable<?> c = (Iterable<?>) value;
            return BCF.wrap(StreamSupport.stream(c.spliterator(), false).map(BCF::storeAny).collect(Collectors.toList()));
        }
        if (value instanceof Object[]) {
            Object[] a = (Object[]) value;
            return BCF.wrap(Stream.of(a).map(BCF::storeAny).collect(Collectors.toList()));
        }
        if (value instanceof byte[]) return BCF.store((byte[]) value);
        if (value instanceof short[]) return BCF.store((short[]) value);
        if (value instanceof int[]) return BCF.store((int[]) value);
        if (value instanceof long[]) return BCF.store((long[]) value);
        if (value instanceof float[]) return BCF.store((float[]) value);
        if (value instanceof double[]) return BCF.store((double[]) value);
        if (value instanceof boolean[]) return BCF.store((boolean[]) value);
        if (value instanceof Map) {
            Map<String, ?> m = (Map<String, ?>) value;
            BCFMap map = new BCFMap(new HashMap<>(2 * m.size() / 3));
            m.forEach((k, v) -> map.put(k, storeAny(v)));
            return map;
        }
        throw new IllegalArgumentException(value + " is of a type not accepted by storeAny");
    }

    public static BCFMap storeMap(Object... keysAndValues) {
        BCFMap map = new BCFMap(new HashMap<>(2 * keysAndValues.length / 3));
        for (int i = 0; i < keysAndValues.length; i += 2) {
            map.put((String) keysAndValues[i], storeAny(keysAndValues[i + 1]));
        }
        return map;
    }

    // Store

    public static BCFRaw wrap(ByteBuf buf) {
        return new BCFRaw(buf);
    }

    public static BCFRaw wrap(ByteBuf buf, int from, int to) {
        return new BCFRaw(buf, from, to);
    }

    public static BCFRaw wrap(ByteBuffer buf) {
        return new BCFRaw(Unpooled.wrappedBuffer(buf));
    }

    public static BCFString wrap(String s) {
        return new BCFString(s);
    }

    public static BCFList wrap(List<BCFItem> l) {
        return new BCFList(l);
    }

    public static BCFList wrap(BCFItem... list) {
        return wrap(Arrays.asList(list));
    }

    public static BCFMap wrap(Map<String, BCFItem> m) {
        return new BCFMap(m);
    }
}
