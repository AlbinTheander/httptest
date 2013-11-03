package albin.httptest.demo;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A simple class used go get content from any http-address while sending an
 * api-key with the request.
 */
public class HttpFacade {

    private final String apiKey;

    /**
     * Create a new Instance of the HttpFacade, using the specified api key.
     *
     * @param apiKey
     *            the api key to use in all requests.
     */
    public HttpFacade(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Gets the content of the page at the specified http address. This method
     * will return an empty string if the response code is anything other than
     * 200-OK.
     *
     * @param httpUrl
     *            the URL to fetch. This must be an http-url.
     * @return the content from the server.
     * @throws MalformedURLException
     *             if the specied URL is not a valud URL.
     * @throws IOException
     *             if something went wrong while getting the content.
     */
    public String getPage(String httpUrl) throws MalformedURLException, IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(httpUrl).openConnection();
        connection.addRequestProperty("Api-Key", apiKey);
        String content = readContent(connection);
        connection.disconnect();
        return content;
    }

    private String readContent(HttpURLConnection connection) throws IOException {
        BufferedReader reader = null;
        String content = "";

        try {
            StringBuilder builder = new StringBuilder();
            int statusCode = connection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                if (connection.getErrorStream() != null) {
                    reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                } else {
                    return "";
                }
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            content = builder.toString();
        } finally {
            closeSilently(reader);
        }

        return content;
    }

    private void closeSilently(Closeable reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                // Do nothing
            }
        }
    }

}
