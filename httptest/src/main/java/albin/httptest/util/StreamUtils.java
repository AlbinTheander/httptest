package albin.httptest.util;

import java.io.Closeable;
import java.io.IOException;

public abstract class StreamUtils {

    public static void closeSilently(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                // Ignore silently. Nothing more to do anyway.
            }
        }
    }

}
