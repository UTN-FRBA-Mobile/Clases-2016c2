package com.example.emanuel.myapplication.slack.response;

import java.io.Serializable;

/**
 * Created by emanuel on 25/9/16.
 */
public class Event implements Serializable {

    private String type;
    private Double ts;

    public String getType() {
        return type;
    }

    public Double getTs() {
        return ts;
    }

    @Override
    public String toString() {
        return "Evento: " + type;
    }
}
