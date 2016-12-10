package blue.made.bcf;

import java.io.IOException;
import java.util.*;

/**
 * Created by Sam Sartor on 3/5/2016.
 */
public class BCFList extends BCFLinearCollection implements List<BCFItem> {
    private List<BCFItem> list;

    public BCFList(List list) {
        super(BCFType.LIST);
        this.list = list;
    }

    public BCFList() {
        this(new ArrayList<>());
    }

    public String toString() {
        String out = "{ ";
        boolean skipdel = true;
        for (BCFItem i : this.list) {
            if (skipdel)
                skipdel = false;
            else
                out += ", ";
            out += i;
        }
        return out + " }";
    }

    @Override
    public void write(BCFWriter writer) throws IOException {
        BCFWriter.List lw = writer.startList();
        for (BCFItem i : list) {
            i.write(lw);
        }
        lw.end();
    }

    @Override
    protected void readData(BCFReader reader) throws IOException {
        BCFReader.List lr = reader.startList();
        while (lr.next()) {
            list.add(lr.read());
        }
    }

    public boolean isList() {
        return true;
    }

    public BCFList asList() {
        return this;
    }

    @Override
    public BCFArray convertToArray() {
        return BCFArray.copy(list);
    }

    @Override
    public BCFList convertToList() {
        return this;
    }

    public static BCFList copy(Collection<BCFItem> data) {
        BCFList list = new BCFList();
        list.addAll(data);
        return list;
    }

    public static BCFList copy(BCFItem... data) {
        return copy(Arrays.asList(data));
    }

    // ================
    // | List Methods |
    // ================

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

    @Override
    public boolean add(BCFItem bcfItem) {
        return list.add(bcfItem);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends BCFItem> c) {
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends BCFItem> c) {
        return list.addAll(index, c);
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

    @Override
    public BCFItem get(int index) {
        return list.get(index);
    }

    @Override
    public BCFItem set(int index, BCFItem element) {
        return list.set(index, element);
    }

    @Override
    public void add(int index, BCFItem element) {
        list.add(index, element);
    }

    @Override
    public BCFItem remove(int index) {
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<BCFItem> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<BCFItem> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }
}