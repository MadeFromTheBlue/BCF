import blue.made.bcf.BCFWriter;
import org.junit.*;

import java.nio.ByteBuffer;

import static org.junit.Assert.*;

/**
 * Created by Sam Sartor on 12/21/2016.
 */
public class PrimTests {
    @Test
    public void readWriteByte() throws Exception {
        byte val = 0x10;
        BCFTestUtils.testReadWrite(w -> w.write(val), r -> {
            r.next();
            assertEquals(r.readByte(), val);
        });
    }

    @Test
    public void readWriteShort() throws Exception {
        short val = 0x10ff;
        BCFTestUtils.testReadWrite(w -> w.write(val), r -> {
            r.next();
            assertEquals(r.readShort(), val);
        });
    }

    @Test
    public void readWriteInt() throws Exception {
        int val = 0x10ffffff;
        BCFTestUtils.testReadWrite(w -> w.write(val), r -> {
            r.next();
            assertEquals(r.readInt(), val);
        });
    }

    @Test
    public void readWriteLong() throws Exception {
        long val = 0x10ffffffffffffffL;
        BCFTestUtils.testReadWrite(w -> w.write(val), r -> {
            r.next();
            assertEquals(r.readLong(), val);
        });
    }

    @Test
    public void readWriteFloat() throws Exception {
        float val = 341.23523F;
        BCFTestUtils.testReadWrite(w -> w.write(val), r -> {
            r.next();
            assertEquals(r.readFloat(), val, .000001);
        });
    }

    @Test
    public void readWriteDouble() throws Exception {
        double val = 341.2324609324;
        BCFTestUtils.testReadWrite(w -> w.write(val), r -> {
            r.next();
            assertEquals(r.readDouble(), val, .000001);
        });
    }


    @Test
    public void readWriteBoolean() throws Exception {
        BCFTestUtils.testReadWrite(w -> {
            w.write(true);
            w.write(false);
        }, r -> {
            r.next();
            assertEquals(r.readBoolean(), true);
            r.next();
            assertEquals(r.readBoolean(), false);
        });
    }
}
