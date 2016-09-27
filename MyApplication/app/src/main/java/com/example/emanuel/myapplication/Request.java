package com.example.emanuel.myapplication;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by emanuel on 25/9/16.
 */
public abstract class Request implements Runnable {

    URL url;

    public Request(URL url) {
        this.url = url;
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
            onReceivedBody(responseCode, responseBody.toString());
        }
        catch (Exception e) {
            onError(e);
        }
    }

    protected abstract void onReceivedBody(int responseCode, String body);

    protected abstract void onError(Exception e);
}
