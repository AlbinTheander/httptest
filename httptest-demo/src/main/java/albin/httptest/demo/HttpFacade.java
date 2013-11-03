package albin.httptest.demo;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpFacade {

    private final String apiKey;

    public HttpFacade(String apiKey) {
        this.apiKey = apiKey;
    }

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
