package blue.made.bcf;

import io.netty.buffer.ByteBuf;

import java.util.Collection;

/**
 * Created by Sam Sartor on 5/9/2016.
 */
public abstract class BCFLinearCollection extends BCFCollection implements Collection<BCFItem> {
    protected BCFLinearCollection(BCFType type) {
        super(type);
    }

    // =============
    // | More Adds |
    // =============

    public void add(boolean value) {
        add(BCF.store(value));
    }

    public void add(byte value) {
        add(BCF.store(value));
    }

    public void add(short value) {
        add(BCF.store(value));
    }

    public void add(int value) {
        add(BCF.store(value));
    }

    public void add(long value) {
        add(BCF.store(value));
    }

    public void add(float value) {
        add(BCF.store(value));
    }

    public void add(double value) {
        add(BCF.store(value));
    }

    public void add(String value) {
        add(BCF.store(value));
    }

    public void addWrapped(String value) {
        add(BCF.wrap(value));
    }

    public void add(ByteBuf value) {
        add(BCF.store(value));
    }

    public void addWrapped(ByteBuf value) {
        add(BCF.wrap(value));
    }

    public void add(byte... value) {
        add(BCF.store(value));
    }

    public void add(short... value) {
        add(BCF.store(value));
    }

    public void add(int... value) {
        add(BCF.store(value));
    }

    public void add(long... value) {
        add(BCF.store(value));
    }

    public void add(float... value) {
        add(BCF.store(value));
    }

    public void add(double... value) {
        add(BCF.store(value));
    }

    public void add(ByteBuf... value) {
        add(BCF.store(value));
    }

    public void add(String... value) {
        add(BCF.store(value));
    }
}
