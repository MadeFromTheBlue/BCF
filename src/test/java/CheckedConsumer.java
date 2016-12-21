import java.io.IOException;

/**
 * Created by Sam Sartor on 12/21/2016.
 */
@FunctionalInterface
public interface CheckedConsumer<T> {
    public void accept(T value) throws Exception;
}
