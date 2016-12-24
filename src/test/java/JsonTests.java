import blue.made.bcf.BCF;
import blue.made.bcf.BCFArray;
import blue.made.bcf.BCFList;
import blue.made.bcf.BCFMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Sumner Evans on 2016/12/23.
 */
public class JsonTests {
    @Test
    public void testToJson() {
        BCFMap map = new BCFMap();
        map.put("str", "foo");
        map.put("int", 2);
        map.put("float", 3.14f);
        map.put("bool", false);

        BCFArray arr = new BCFArray();
        arr.add("foo");
        arr.add("bar");
        arr.add("baz");
        map.put("arr", arr);

        BCFMap nested = new BCFMap();
        nested.put("a", "b");
        map.put("nested", nested);

        JsonObject json = BCF.toJson(map).getAsJsonObject();
        assertEquals("foo", json.get("str").getAsString());
        assertEquals(2, json.get("int").getAsInt());
        assertEquals(3.14f, json.get("float").getAsFloat());
        assertEquals(false, json.get("bool").getAsBoolean());

        JsonArray jArray = json.get("arr").getAsJsonArray();
        assertEquals("foo", jArray.get(0).getAsString());
        assertEquals("bar", jArray.get(1).getAsString());
        assertEquals("baz", jArray.get(2).getAsString());

        assertEquals("b", json.get("nested").getAsJsonObject().get("a").getAsString());
    }

    @Test
    public void testFromJson() {
        JsonObject json = new JsonObject();
        json.add("str", new JsonPrimitive("foo"));
        json.add("int", new JsonPrimitive(2));
        json.add("float", new JsonPrimitive(3.14f));
        json.add("bool", new JsonPrimitive(false));

        JsonArray arr = new JsonArray();
        arr.add("foo");
        arr.add("bar");
        arr.add("baz");
        json.add("arr", arr);

        JsonObject nested = new JsonObject();
        nested.add("a", new JsonPrimitive("b"));
        json.add("nested", nested);

        BCFMap map = BCF.fromJson(json).asMap();
        assertEquals("foo", map.get("str").asString());
        assertEquals(2, map.get("int").asNumeric().intValue());
        assertEquals(3.14f, map.get("float").asNumeric().floatValue());
        assertEquals(false, map.get("bool").asBoolean());

        BCFList bArr = map.get("arr").asList();
        assertEquals("foo", bArr.get(0).asString());
        assertEquals("bar", bArr.get(1).asString());
        assertEquals("baz", bArr.get(2).asString());

        assertEquals("b", map.get("nested").asMap().get("a").asString());
    }
}
