package blue.made.bcf.stream;

import blue.made.bcf.BCFArray;
import blue.made.bcf.BCFItem;
import blue.made.bcf.BCFType;
import io.netty.buffer.ByteBuf;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Sam Sartor on 12/21/2016.
 */
public interface BCFReader {
    static interface Layer {
        public boolean hasNext();
        public boolean isNamed();
        public int size();
        public BCFType layer();
        public BCFItem next();
        public String nextName();
    }

    /**
     * Creates a BCFReader that views the provided item.
     * <br>
     * Note that {@link #next()} will provide direct, mutable references to parts of the item.
     */
    public static BCFReader from(BCFItem item) {
        return new BCFReader() {
            private Deque<Layer> stack = new LinkedList<>();
            private BCFItem current = item;
            private String currentName = null;
            private boolean isNamed = false;

            public void loadNext() {
                if (stack.isEmpty()) {
                    current = null;
                    return;
                }
                Layer top = stack.peekLast();
                currentName = null;
                current = null;
                isNamed = top.isNamed();
                if (top.hasNext()) {
                    if (isNamed) currentName = top.nextName();
                    current = top.next();
                }
            }

            @Override
            public BCFType peek() {
                if (current == null) return null;
                return current.type;
            }

            @Override
            public BCFItem next() {
                BCFItem out = current;
                loadNext();
                return out;
            }

            @Override
            public boolean hasNext() throws IOException {
                return current != null;
            }

            @Override
            public byte nextByte() throws IOException {
                return next().asNumeric().byteValue();
            }

            @Override
            public short nextShort() throws IOException {
                return next().asNumeric().shortValue();
            }

            @Override
            public int nexInt() throws IOException {
                return next().asNumeric().intValue();
            }

            @Override
            public long nextLong() throws IOException {
                return next().asNumeric().longValue();
            }

            @Override
            public float nextFloat() throws IOException {
                return next().asNumeric().floatValue();
            }

            @Override
            public boolean nextBool() throws IOException {
                return next().asBoolean();
            }

            @Override
            public ByteBuf nextRaw() throws IOException {
                return next().asRaw();
            }

            @Override
            public String nextString() throws IOException {
                return next().asString();
            }

            @Override
            public String nextName() throws IOException {
                if (!isNamed) throw new IllegalStateException("The current item is unnamed");
                return currentName;
            }

            public void check(Function<BCFItem, Boolean> check, String expected) throws IOException {
                if (current == null) throw new IllegalStateException("Expected " + expected + " but was at end");
                if (!check.apply(current)) throw new IllegalStateException("Expected " + expected + " but was " + current.type.name());
            }

            @Override
            public void beginCollection() throws IOException {
                check(BCFItem::isCollection, "a collection");

                if (current.isMap()) {
                    stack.push(new Layer() {
                        private Iterator<Map.Entry<String, BCFItem>> entries = current.asMap().entrySet().iterator();
                        private Map.Entry<String, BCFItem> next;

                        @Override
                        public boolean hasNext() {
                            return entries.hasNext();
                        }

                        @Override
                        public boolean isNamed() {
                            return true;
                        }

                        @Override
                        public int size() {
                            throw new IllegalStateException("Maps do not have a known size");
                        }

                        @Override
                        public BCFType layer() {
                            return BCFType.MAP;
                        }

                        @Override
                        public BCFItem next() {
                            if (next == null) next = entries.next();
                            BCFItem out = next.getValue();
                            next = null;
                            return out;
                        }

                        @Override
                        public String nextName() {
                            if (next == null) next = entries.next();
                            return next.getKey();
                        }
                    });
                } else {
                    boolean isarray = current.type == BCFType.ARRAY;
                    int size = isarray ? ((BCFArray) current).size() : -1;
                    BCFType type = current.type;

                    stack.push(new Layer() {
                        private Iterator<BCFItem> entries = current.asCollection().iterator();
                        private BCFItem next;

                        @Override
                        public boolean hasNext() {
                            return entries.hasNext();
                        }

                        @Override
                        public boolean isNamed() {
                            return false;
                        }

                        @Override
                        public int size() {
                            if (!isarray) throw new IllegalStateException("Lists do not have a known size");
                            return size;
                        }

                        @Override
                        public BCFType layer() {
                            return type;
                        }

                        @Override
                        public BCFItem next() {
                            if (next == null) next = entries.next();
                            BCFItem out = next;
                            next = null;
                            return out;
                        }

                        @Override
                        public String nextName() {
                            throw new NotImplementedException();
                        }
                    });
                }
                loadNext();
            }

            @Override
            public void discardCollection() {
                if (stack.isEmpty()) throw new IllegalStateException("Expected to be reading a collection but was in top level");
                stack.pop();
                loadNext();
            }

            @Override
            public void endCollection() {
                if (stack.isEmpty()) throw new IllegalStateException("Expected to be reading a collection but was in top level");
                if (stack.peek().hasNext()) throw new IllegalStateException("Expected collection end but found additional items");
                stack.pop();
                loadNext();
            }

            @Override
            public BCFType currentCollection() {
                if (stack.isEmpty()) return null;
                return stack.peekLast().layer();
            }

            @Override
            public List<BCFType> collectionStack() {
                return stack.stream().map(l -> l.layer()).collect(Collectors.toList());
            }
        };
    }

    /*
    public static BCFReader from(DataInputStream in) {
        return new BCFReader() {
        };
    }

    public static BCFReader from(InputStream in) {
        if (in instanceof DataInputStream) return from((DataInputStream) in);
        else return from(new DataInputStream(in));
    }
    */

    /**
     * Returns the type of the next item in the stream or null if the source has no more data.
     *
     * @throws IOException if the source contains invalid data
     */
    public BCFType peek() throws IOException;

    /**
     * Consumes the source to obtain the next element, providing an in-memory form of the data or returning null
     * if there is no more data available in the source or if the stream is at the end of the current collection.
     * @throws IOException if the source contains invalid data
     */
    public BCFItem next() throws IOException;

    /**
     * Is there another item in the stream or collection.
     * @throws IOException if the source contains invalid data
     */
    public boolean hasNext() throws IOException;

    /**
     * Consumes the source to obtain the next element, and asserts that it is a BYTE (or other numerical value).
     *
     * @return the next item
     * @throws IOException if the source contains invalid data or no more data
     * @throws IllegalStateException if current item is not a number
     */
    public byte nextByte() throws IOException;

    /**
     * Consumes the source to obtain the next element, and asserts that it is a SHORT (or other numerical value).
     *
     * @return the next item
     * @throws IOException if the source contains invalid data or no more data
     * @throws IllegalStateException if current item is not a number
     */
    public short nextShort() throws IOException;

    /**
     * Consumes the source to obtain the next element, and asserts that it is an INT (or other numerical value).
     *
     * @return the next item
     * @throws IOException if the source contains invalid data or no more data
     * @throws IllegalStateException if current item is not a number
     */
    public int nexInt() throws IOException;

    /**
     * Consumes the source to obtain the next element, and asserts that it is a LONG (or other numerical value).
     *
     * @return the next item
     * @throws IOException if the source contains invalid data or no more data
     * @throws IllegalStateException if current item is not a number
     */
    public long nextLong() throws IOException;

    /**
     * Consumes the source to obtain the next element, and asserts that it is a FLOAT (or other numerical value).
     *
     * @return the next item
     * @throws IOException if the source contains invalid data or no more data
     * @throws IllegalStateException if current item is not a number
     */
    public float nextFloat() throws IOException;

    /**
     * Consumes the source to obtain the next element, and asserts that it is a BOOLEAN (or other numerical value).
     *
     * @return the next item
     * @throws IOException if the source contains invalid data or no more data
     * @throws IllegalStateException if current item is not a number
     */
    public boolean nextBool() throws IOException;

    /**
     * Consumes the source to obtain the next element, and asserts that it is a RAW
     *
     * @return the next item
     * @throws IOException if the source contains invalid data or no more data
     * @throws IllegalStateException if current item is not a raw
     */
    public ByteBuf nextRaw() throws IOException;

    /**
     * Consumes the source to obtain the next element, and asserts that it is a STRING
     *
     * @return the next item
     * @throws IOException if the source contains invalid data or no more data
     * @throws IllegalStateException if current item is not a string
     */
    public String nextString() throws IOException;

    /** If the current collection is a map, then the name of the current item is returned or null if there are no
     *  more items in the map. Otherwise, an {@link IllegalStateException} is thrown.
     * @throws IOException if the source contains invalid data
     * @throws IllegalStateException if the current item is not a map
     */
    public String nextName() throws IOException;

    /**
     * Asserts that the next item in the stream is a collection and begins reading the values in it.
     *
     * @throws IOException if the source contains invalid data
     */
    public void beginCollection() throws IOException;

    /**
     * Ends the current collection and discards amy remaining items.
     *
     * @throws IOException if the source contains invalid data
     */
    public void discardCollection() throws IOException;

    /**
     * Ends the current collection and asserts that the collection contains no more items.
     *
     * @throws IOException if the source contains invalid data
     */
    public void endCollection() throws IOException;

    /**
     * Returns the current collection type or null if the stream is currently reading the top level.
     */
    public BCFType currentCollection();

    /**
     * Returns a list showing the current collection hierarchy. {@link #currentCollection()} would return the last
     * item in this list.
     */
    public List<BCFType> collectionStack();
}
