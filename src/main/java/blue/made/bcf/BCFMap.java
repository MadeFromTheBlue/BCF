package blue.made.bcf;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by Sam Sartor on 3/5/2016.
 */
public class BCFMap extends BCFCollection implements Map<String, BCFItem> {
	public static final Charset encoding = StandardCharsets.UTF_8;

	private Map<String, BCFItem> map;

	public BCFMap(Map<String, BCFItem> map) {
		super(BCFType.MAP);
		this.map = map;
	}

	public BCFMap() {
		this(new HashMap<>());
	}

	public String toString() {
		String out = "{ ";
		boolean skipdel = true;
		for (Map.Entry<String, BCFItem> e : this.map.entrySet()) {
			if (skipdel)
				skipdel = false;
			else
				out += ", ";
			out += e.getKey() + ": " + e.getValue();
		}
		return out + " }";
	}

	@Override
	public void write(BCFWriter writer) throws IOException {
		BCFWriter.Map mw = writer.startMap();
		for (java.util.Map.Entry<String, BCFItem> e : map.entrySet()) {
			mw.writeName(e.getKey());
			e.getValue().write(mw);
		}
		mw.end();
	}

	@Override
	protected void readData(BCFReader reader) throws IOException {
		BCFReader.Map mr = reader.startMap();
		while (mr.next()) {
			map.put(mr.currentName(), mr.read());
		}
	}

	public boolean isMap() {
		return true;
	}
	public BCFMap asMap() {
		return this;
	}

	@Override
	public BCFArray convertToArray() {
		return BCFArray.copy(this.values());
	}

	@Override
	public BCFList convertToList() {
		return BCFList.copy(this.values());
	}

	// ===============
	// | Map Methods |
	// ===============

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public BCFItem get(Object key) {
		return map.get(key);
	}

	@Override
	public BCFItem put(String key, BCFItem value) {
		return map.put(key, value);
	}

	@Override
	public BCFItem remove(Object key) {
		return map.remove(key);
	}

	@Override
	public void putAll(java.util.Map m) {
		map.putAll(m);
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Set<String> keySet() {
		return map.keySet();
	}

	@Override
	public Collection<BCFItem> values() {
		return map.values();
	}

	@Override
	public Set<Entry<String, BCFItem>> entrySet() {
		return map.entrySet();
	}

	// =============
	// | More Puts |
	// =============

	public void put(String key, boolean value) {
		map.put(key, BCF.store(value));
	}

	public void put(String key, byte value) {
		map.put(key, BCF.store(value));
	}

	public void put(String key, short value) {
		map.put(key, BCF.store(value));
	}

	public void put(String key, int value) {
		map.put(key, BCF.store(value));
	}

	public void put(String key, long value) {
		map.put(key, BCF.store(value));
	}

	public void put(String key, float value) {
		map.put(key, BCF.store(value));
	}

	public void put(String key, double value) {
		map.put(key, BCF.store(value));
	}

	public void put(String key, String value) {
		map.put(key, BCF.store(value));
	}

	public void putWrapped(String key, String value) {
		map.put(key, BCF.wrap(value));
	}

	public void put(String key, ByteBuf value) {
		map.put(key, BCF.store(value));
	}

	public void putWrapped(String key, ByteBuf value) {
		map.put(key, BCF.wrap(value));
	}

	public void put(String key, byte... value) {
		map.put(key, BCF.store(value));
	}

	public void put(String key, short... value) {
		map.put(key, BCF.store(value));
	}

	public void put(String key, int... value) {
		map.put(key, BCF.store(value));
	}

	public void put(String key, long... value) {
		map.put(key, BCF.store(value));
	}

	public void put(String key, float... value) {
		map.put(key, BCF.store(value));
	}

	public void put(String key, double... value) {
		map.put(key, BCF.store(value));
	}

	public void put(String key, ByteBuf... value) {
		map.put(key, BCF.store(value));
	}

	public void put(String key, String... value) {
		map.put(key, BCF.store(value));
	}

	@Override
	public Iterator<BCFItem> iterator() {
		return map.values().iterator();
	}
}