package blue.made.bcf;

/**
 * Created by Sam Sartor on 5/11/2016.
 */
public enum BCFType {
	NULL(0) {
		public BCFItem createDefault() {
			return BCFNull.INSTANCE;
		}
	},
	BYTE(1) {
		public BCFItem createDefault() {
			return new BCFByte();
		}
	},
	SHORT(2) {
		public BCFItem createDefault() {
			return new BCFShort();
		}
	},
	INT(3) {
		public BCFItem createDefault() {
			return new BCFInt();
		}
	},
	LONG(4) {
		public BCFItem createDefault() {
			return new BCFLong();
		}
	},
	FLOAT(5) {
		public BCFItem createDefault() {
			return new BCFFloat();
		}
	},
	DOUBLE(6) {
		public BCFItem createDefault() {
			return new BCFDouble();
		}
	},
	RAW(7) {
		public BCFItem createDefault() {
			return new BCFRaw();
		}
	},
	STRING(8) {
		public BCFItem createDefault() {
			return new BCFString();
		}
	},
	LIST(9) {
		public BCFItem createDefault() {
			return new BCFList();
		}
	},
	MAP(10) {
		public BCFItem createDefault() {
			return new BCFMap();
		}
	},
	ARRAY(11) {
		public BCFItem createDefault() {
			return new BCFArray();
		}
	},
	END(255) {
		public BCFItem createDefault() {
			throw new UnsupportedOperationException("BCF type end can not be created as an object");
		}
	};

	private static BCFType[] types = new BCFType[256];

	static {
		for (BCFType t : values()) {
			types[t.id & 0xFF] = t;
		}
	}

	public final byte id;

	BCFType(int id) {
		this.id = (byte) id;
	}

	public abstract BCFItem createDefault();
	public static BCFType from(byte id) {
		return types[id & 0xFF];
	}
}
