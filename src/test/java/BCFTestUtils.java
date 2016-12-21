import blue.made.bcf.BCFReader;
import blue.made.bcf.BCFWriter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.util.function.Consumer;

/**
 * Created by Sam Sartor on 12/21/2016.
 */
public class BCFTestUtils {
    public static void testReadWrite(CheckedConsumer<BCFWriter> write, CheckedConsumer<BCFReader> read) throws Exception {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        write.accept(new BCFWriter(buf));
        read.accept(new BCFReader(buf));
    }
}
