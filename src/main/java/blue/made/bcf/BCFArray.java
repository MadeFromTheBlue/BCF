package blue.made.bcf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Sam Sartor on 5/9/2016.
 */
public class BCFArray extends BCFLinearCollection {
    private ArrayList<BCFItem> list = new ArrayList<>();
    private BCFType arrayType = null;

    public BCFArray() {
        super(BCFType.ARRAY);
    }

    public String toString() {
        String out = "[ ";
        boolean skipdel = true;
        for (BCFItem i : this.list) {
            if (skipdel)
                skipdel = false;
            else
                out += ", ";
            out += i;
        }
        return out + " ]";
    }

    public BCFItem get(int idx) {
        return list.get(idx);
    }

    @Override
    public void write(BCFWriter writer) throws IOException {
        BCFWriter.Array aw;
        if (list.isEmpty()) aw = writer.startArray(BCFType.NULL, 0);
        else aw = writer.startArray(arrayType, list.size());
        for (BCFItem i : list) {
            i.write(aw);
        }
    }

    @Override
    protected void readData(BCFReader reader) throws IOException {
        BCFReader.Array ar = reader.startArray();
        list.ensureCapacity(ar.size + list.size());
        while (ar.next()) {
            list.add(ar.read());
        }
    }

    public boolean isArray() {
        return true;
    }

    public BCFArray asArray() {
        return this;
    }

    @Override
    public BCFArray convertToArray() {
        return this;
    }

    @Override
    public BCFList convertToList() {
        BCFList list = new BCFList();
        list.addAll(this);
        return list;
    }

    public BCFType getArrayType() {
        if (list.isEmpty()) return null;
        return arrayType;
    }

    void addUnsafe(BCFItem i) {
        list.add(i);
    }

    void typeUnsafe(BCFType type) {
        arrayType = type;
    }

    public static BCFArray copy(Collection<BCFItem> data) {
        BCFArray arr = new BCFArray();
        arr.addAll(data);
        return arr;
    }

    public static BCFArray copy(BCFItem... data) {
        return copy(Arrays.asList(data));
    }

    // ======================
    // | Collection Methods |
    // ======================

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<BCFItem> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    /**
     * @throws IllegalStateException If the type of the items already in the BCFArray does not match the type of bcfItem.
     */
    @Override
    public boolean add(BCFItem bcfItem) {
        if (list.isEmpty()) arrayType = bcfItem.type;
        if (bcfItem.type == arrayType) {
            return list.add(bcfItem);
        } else {
            throw new IllegalStateException("BCFItem type mismatch: array has type " + arrayType + " but the item has type " + bcfItem.type + ".");
        }
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    /**
     * @throws IllegalStateException If the type of the items already in the BCFArray does not match the type of any of the items in c. If any of the types mismatch, none are added.
     */
    @Override
    public boolean addAll(Collection<? extends BCFItem> c) {
        boolean first = list.isEmpty();
        for (BCFItem i : c) {
            if (first) {
                arrayType = i.type;
                first = false;
            }
            if (i.type != arrayType)
                throw new IllegalStateException("BCFItem type mismatch: array has type " + arrayType + ".");
        }
        return list.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        list.clear();
    }
}
