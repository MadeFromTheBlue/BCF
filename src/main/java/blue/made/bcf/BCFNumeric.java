package blue.made.bcf;

/**
 * Created by Sam Sartor on 3/5/2016.
 */
public abstract class BCFNumeric extends BCFItem {
    public BCFNumeric(BCFType type) {
        super(type);
    }

    public static BCFNumeric storeBest(double n) {
        if ((long) n == n) {
            if (n >= Byte.MIN_VALUE && n <= Byte.MAX_VALUE) {
                return BCF.store((byte) n);
            } else if (n >= Integer.MIN_VALUE && n <= Integer.MAX_VALUE) {
                return BCF.store((short) n);
            } else if (n >= Long.MIN_VALUE && n <= Long.MAX_VALUE) {
                return BCF.store((int) n);
            } else {
                return BCF.store((long) n);
            }
        }
        return BCF.store(n);
    }

    @Override
    public String toString() {
        return String.valueOf(this.asNumeric());
    }

    public byte asByte() {
        return this.asNumeric().byteValue();
    }

    public short asShort() {
        return this.asNumeric().shortValue();
    }

    public int asInt() {
        return this.asNumeric().intValue();
    }

    public long asLong() {
        return this.asNumeric().longValue();
    }

    public float asFloat() {
        return this.asNumeric().floatValue();
    }

    public double asDouble() {
        return this.asNumeric().doubleValue();
    }

    public boolean asBoolean() {
        return this.asNumeric().intValue() != 0;
    }

    public boolean isNumeric() {
        return true;
    }

    public boolean isBoolean() { return true; }

    public abstract Number asNumeric();

    public Number asNumeric(Number ifNaN) {
        return asNumeric();
    }

    public BCFNumeric asNumericItem() {
        return this;
    }

    public BCFNumeric asNumericItem(Number ifNaN) {
        return this;
    }


}
