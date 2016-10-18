package com.example.emanuel.myapplication;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by emanuel on 25/9/16.
 */
public class Request implements Runnable {

    public interface Listener {

        void onReceivedBody(int responseCode, String body);
        void onError(Exception e);
    }

    public interface RequestFactory {

        Request makeRequest(URL url, Listener listener);
    }
    private static RequestFactory factory = new RequestFactory() {
        @Override
        public Request makeRequest(URL url, Listener listener) {
            return new Request(url, listener);
        }
    };

    private URL url;
    private Listener listener;

    public static Request makeRequest(URL url, Listener listener) {
        return factory.makeRequest(url, listener);
    }

    public static void setFactory(RequestFactory newFactory) {
        factory = newFactory;
    }

    public Request(URL url, Listener listener) {
        this.url = url;
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream stream = connection.getInputStream();
            int responseCode = connection.getResponseCode();
            ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
            byte buffer[] = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = stream.read(buffer)) > 0) {
                responseBody.write(buffer, 0, bytesRead);
            }
            listener.onReceivedBody(responseCode, responseBody.toString());
        }
        catch (Exception e) {
            listener.onError(e);
        }
    }
}
