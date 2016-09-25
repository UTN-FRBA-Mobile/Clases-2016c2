package com.example.emanuel.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by emanuel on 5/9/16.
 */
public class RecyclerFragment extends Fragment {

    RecyclerView recyclerView;
    MiAdapter adapter = new MiAdapter();
    Executor executor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recycler_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchWeatherAsync();
    }

    private void fetchWeatherAsync() {
        // mostrar el loading
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<Forecast> forecasts = fetchWeatherSync();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // ocultar el loading
                            updateForecasts(forecasts);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onWeatherRequestFailed();
                        }
                    });
                }
            }
        });
    }

    private List<Forecast> fetchWeatherSync() throws IOException, JSONException {
        URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?lat=35&lon=139&cnt=10&appid=8421cf3aebd32d95d30c13c9187cc535");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        InputStream stream = connection.getInputStream();
        ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
        byte buffer[] = new byte[1024];
        int bytesRead = 0;
        while ((bytesRead = stream.read(buffer)) > 0) {
            responseBody.write(buffer, 0, bytesRead);
        }
        JSONObject jsonObject = new JSONObject(responseBody.toString());
        return parseList(jsonObject.getJSONArray("list"));
    }

    private List<Forecast> parseList(JSONArray list) throws JSONException {
        int len = list.length();
        List<Forecast> forecasts = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            JSONObject object = list.getJSONObject(i);
            if (object.has("rain")) {
                forecasts.add(new Forecast(object.getDouble("rain")));
            }
        }
        return forecasts;
    }

    private void updateForecasts(List<Forecast> forecasts) {
        adapter.setItems(forecasts);
    }

    private void onWeatherRequestFailed() {

    }
}
