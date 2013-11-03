package albin.httptest.util;

import java.io.InputStream;
import java.util.Scanner;

public class ResourceUtil {

    public static String toString(String path) {
        InputStream input = ResourceUtil.class.getResourceAsStream(path);
        Scanner s = new Scanner(input, "utf-8").useDelimiter("\\A");
        String text = s.hasNext() ? s.next() : "";
        StreamUtils.closeSilently(input);
        return text;
    }
}
