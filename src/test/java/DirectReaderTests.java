import blue.made.bcf.*;
import blue.made.bcf.stream.BCFReader;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.junit.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by Sam Sartor on 12/21/2016.
 */
public class DirectReaderTests {
    @Test
    public void emptyTest() throws IOException {
        BCFReader reader = BCFReader.from(BCF.store(new HashMap<>()));

        reader.beginCollection();

        assertNull(reader.peek());
        assertNull(reader.next());
        assertNull(reader.nextName());

        reader.endCollection();

        assertNull(reader.peek());
        assertNull(reader.next());
    }

    @Test
    public void listTest() throws IOException {
        BCFList item = BCF.store(
                BCF.store(1, 2, 3),
                BCF.store("hello"),
                BCF.store(10),
                BCF.store(false));
        BCFReader reader = BCFReader.from(item);

        assertEquals(reader.peek(), BCFType.LIST);
        reader.beginCollection();

        assertEquals(reader.peek(), BCFType.ARRAY);
        assertSame(reader.next(), item.get(0));

        assertEquals(reader.peek(), BCFType.STRING);
        assertEquals(reader.next().asString(), "hello");

        assertEquals(reader.peek(), BCFType.INT);
        assertEquals(reader.next().asNumeric().intValue(), 10);

        assertEquals(reader.peek(), BCFType.BOOLEAN);
        assertEquals(reader.next().asBoolean(), false);

        reader.endCollection();
        assertNull(reader.peek());
    }

    @Test
    public void mapTest() throws IOException {
        BCFMap item = BCF.storeMap(
                "array", new int[] {1, 2, 3},
                "int", 10,
                "list", Arrays.asList(10, true, "hello"),
                "str", "world");
        BCFReader reader = BCFReader.from(item);

        reader.beginCollection();

        while (reader.hasNext()) {
            switch (reader.nextName()) {
                case "array":
                    assertEquals(reader.peek(), BCFType.ARRAY);
                    BCFArray arr = reader.next().asArray();
                    assertEquals(arr.size(), 3);
                    break;
                case "int":
                    assertEquals(reader.peek(), BCFType.INT);
                    assertEquals(reader.nexInt(), 10);
                    break;
                case "list":
                    assertEquals(reader.peek(), BCFType.LIST);
                    BCFList list = reader.next().asList();
                    assertEquals(list.size(), 3);
                    break;
                case "str":
                    assertEquals(reader.peek(), BCFType.STRING);
                    assertEquals(reader.nextString(), "world");
                    break;
            }
        }
    }

    @Test
    public void arrayTest() throws IOException {
        BCFReader reader = BCFReader.from(BCF.store(0, 1, 2, 3, 4, 5));

        assertEquals(reader.peek(), BCFType.ARRAY);
        reader.beginCollection();
        for (int i = 0; i <= 5; i++) {
            assertEquals(reader.nexInt(), i);
        }
        reader.endCollection();
        assertNull(reader.peek());
    }
}
