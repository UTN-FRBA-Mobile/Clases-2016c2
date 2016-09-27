package com.example.emanuel.myapplication.slack;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.emanuel.myapplication.slack.response.Event;
import com.example.emanuel.myapplication.slack.response.ResponseParser;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.internal.ws.WebSocket;
import com.squareup.okhttp.internal.ws.WebSocketListener;

import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by emanuel on 25/9/16.
 */
public class RTMService extends Service {

    public static final String NewEventIntentAction = "NewEventIntentAction";
    public static final String EventExtraKey = "EventExtra";

    private static final String TAG = RTMService.class.getName();
    private static final String apiToken = "THE_TOKEN";

    private final IBinder binder = new Binder();
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler();
    private boolean shouldConnect = false;
    private boolean obtainingUrl = false;
    private String websocketUrl = null;
    private WebSocket webSocket = null;
    private boolean connected = false;
    private int lastId = 1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        shouldConnect = true;
        connectIfRequired();
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        shouldConnect = false;
        if (webSocket != null && connected) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        webSocket.close(1000, "end");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        executor.shutdown();
        return super.onUnbind(intent);
    }

    private void connectIfRequired() {
        if (!shouldConnect || obtainingUrl || webSocket != null) {
            return;
        }
        if (websocketUrl == null) {
            obtainWebSocketURI();
            return;
        }

        final OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(websocketUrl)
                .build();
        webSocket = WebSocket.newWebSocket(client, request);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "Conectando al WebSocket.");
                    webSocket.connect(new WebSocketListener() {
                        @Override
                        public void onMessage(BufferedSource payload, WebSocket.PayloadType type) throws IOException {
                            Log.d(TAG, "Mensaje recibido.");
                            final String message = payload.readUtf8();
                            Event event = ResponseParser.instance.parseEvent(message);
                            Intent intent = new Intent(NewEventIntentAction);
                            intent.putExtra(EventExtraKey, event);
                            sendBroadcast(intent);
                            payload.close();
                        }

                        @Override
                        public void onClose(int code, String reason) {
                            webSocket = null;
                            connected = false;
                            Log.d(TAG, "Conexión cerrada.");
                            retryConnectionDelayed();
                        }

                        @Override
                        public void onFailure(IOException e) {
                            e.printStackTrace();
                            Log.d(TAG, "Error de conexión.");
                            retryConnectionDelayed();
                        }
                    });
                    connected = true;
                    websocketUrl = null;
                    Log.d(TAG, "Conectado.");
                    // Trigger shutdown of the dispatcher's executor so this process can exit cleanly.
                    client.getDispatcher().getExecutorService().shutdown();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "Falló la conexión al WebSocket.");
                    retryConnectionDelayed();
                }
            }
        });
    }

    private void obtainWebSocketURI() {
        obtainingUrl = true;
        URL url;
        try {
            url = new URL("https://slack.com/api/rtm.start?token=" + apiToken);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }
        Log.d(TAG, "Adquiriendo URL del WebSocket.");
        com.example.emanuel.myapplication.Request request = new com.example.emanuel.myapplication.Request(url) {
            @Override
            protected void onReceivedBody(int responseCode, String body) {
                obtainingUrl = false;
                if (responseCode == 200) {
                    try {
                        JSONObject json = new JSONObject(body);
                        websocketUrl = json.getString("url");
                        Log.d(TAG, "ws url: " + websocketUrl);
                        connectIfRequired();
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "Falló la llamada inicial.");
                retryConnectionDelayed();
            }

            @Override
            protected void onError(Exception e) {
                obtainingUrl = false;
                Log.d(TAG, "Falló la llamada inicial.");
                retryConnectionDelayed();
            }
        };
        executor.execute(request);
    }

    private void retryConnectionDelayed() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                connectIfRequired();
            }
        }, 1000);
    }

    public boolean isConnected() {
        return connected;
    }

    public void sendMessage(final String channelId, final String text) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject json = new JSONObject();
                    json.put("type", "message");
                    json.put("id", nextId());
                    json.put("channel", channelId);
                    json.put("text", text);
                    Buffer payload = new Buffer();
                    payload.writeString(json.toString(), Charset.defaultCharset());
                    webSocket.sendMessage(WebSocket.PayloadType.TEXT, payload);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    int nextId() {
        return lastId++;
    }

    public class Binder extends android.os.Binder {

        RTMService getService() {
            return RTMService.this;
        }
    }
}
