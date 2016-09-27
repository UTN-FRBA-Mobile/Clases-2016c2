package com.example.emanuel.myapplication.slack.response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by emanuel on 25/9/16.
 */
public class ResponseParser {

    public static final ResponseParser instance = new ResponseParser();

    final Gson gson;

    ResponseParser() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Event.class, new EventAdapter());
        gson = builder.create();
    }

    public Event parseEvent(String jsonString) {
        return gson.fromJson(jsonString, Event.class);
    }
}
