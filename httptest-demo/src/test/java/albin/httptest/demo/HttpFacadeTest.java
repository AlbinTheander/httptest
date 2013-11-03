package albin.httptest.demo;

import static org.junit.Assert.*;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import albin.httptest.TestWebServer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

public class HttpFacadeTest {

    private TestWebServer server;
    private HttpFacade httpFacade;

    @Before
    public void setUp() throws IOException {
        server = new TestWebServer(8085);
        server.start();

        httpFacade = new HttpFacade("abc123");
    }

    @After
    public void tearDown() {
        server.stop();
    }

    @Test
    public void facadeShouldReturnText() throws MalformedURLException, IOException {
        server.whenGetting("/myPage").thenAnswer("This is my page!");

        assertEquals("This is my page!", httpFacade.getPage("http://localhost:8085/myPage"));
    }

    @Test
    public void facadeShouldSendApiKeyAsHeader() throws MalformedURLException, IOException {
        server.whenGetting("/myPage").thenAnswer("");

        httpFacade.getPage("http://localhost:8085/myPage");

        List<IHTTPSession> requests = server.getRequests();
        String apiKeyHeader = requests.get(0).getHeaders().get("api-key");

        assertEquals("abc123", apiKeyHeader);
    }

    @Test
    public void facadeShouldFollowRedirects() throws MalformedURLException, IOException {
        Response redirectionResponse = new Response(Response.Status.REDIRECT, "text/plain", "");
        redirectionResponse.addHeader("Location", "/myOtherPage");

        server.whenGetting("/myPage").thenAnswer(redirectionResponse);
        server.whenGetting("/myOtherPage").thenAnswer("Hi there!");

        String result = httpFacade.getPage("http://localhost:8085/myPage");

        assertEquals("Hi there!", result);
    }

    @Test
    public void facadeShouldReturnEmptyStringWhenGettingServerError() throws MalformedURLException, IOException {
        server.whenGetting("/myPage").thenAnswer(Response.Status.INTERNAL_ERROR);

        assertEquals("", httpFacade.getPage("http://localhost:8085/myPage"));
    }

}
