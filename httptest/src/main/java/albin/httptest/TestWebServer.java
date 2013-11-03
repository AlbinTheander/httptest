package albin.httptest;

import static org.mockito.Mockito.*;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;

import albin.httptest.util.ResourceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestWebServer extends NanoHTTPD {

    @Mock
    private ResponseHelper responseHelper;

    private List<IHTTPSession> requests = Collections.synchronizedList(new ArrayList<NanoHTTPD.IHTTPSession>());

    public TestWebServer(int port) {
        super(port);
        MockitoAnnotations.initMocks(this);
    }

    @Override
    public Response serve(IHTTPSession session) {
        requests.add(session);
        Response response = responseHelper.getResponse(session.getUri());
        if (response == null) {
            response = new Response(Status.BAD_REQUEST, MIME_PLAINTEXT, "");
        }
        return response;
    }

    public ServerRequest whenGetting(String path) {
        return new ServerRequest(path);
    }

    public List<IHTTPSession> getRequests() {
        return requests;
    }

    public class ServerRequest {

        private final String path;

        private OngoingStubbing<Response> onGoingStubbing;

        public ServerRequest(String path) {
            this.path = path;
        }

        public ServerRequest thenAnswerWithResource(String resourcePath) {
            return thenAnswer(ResourceUtil.toString(resourcePath));
        }

        public ServerRequest thenAnswer(String answer) {
            return thenAnswer(new Response(answer));
        }

        public ServerRequest thenAnswer(Status status) {
            return thenAnswer(new Response(status, "", ""));
        }

        public ServerRequest thenAnswer(Response response) {
            if (onGoingStubbing == null) {
                onGoingStubbing = when(responseHelper.getResponse(path));
            }
            onGoingStubbing = onGoingStubbing.thenReturn(response);
            return this;
        }
    }

    private static interface ResponseHelper {
        public Response getResponse(String path);
    }

}
