package albin.httptest.android.test;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

import org.mockito.Matchers;

import albin.httptest.TestWebServer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import junit.framework.TestCase;

public class HttpTest extends TestCase {

    private TestWebServer server;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        server = new TestWebServer(8085);
        server.start();
    }

    @Override
    protected void tearDown() throws Exception {
        server.stop();

        super.tearDown();
    }

    public void testUrlConnectionSendsKeepAliveHeader() throws MalformedURLException, IOException {
        server.whenGetting(Matchers.anyString()).thenAnswer(Response.Status.NO_CONTENT);

        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8085/something").openConnection();

        // This will trigger the actual connection.
        connection.getResponseCode();
        connection.disconnect();

        List<IHTTPSession> requests = server.getRequests();
        String connectionHeader = requests.get(0).getHeaders().get("connection");
        assertEquals("Keep-Alive", connectionHeader);


    }

}
