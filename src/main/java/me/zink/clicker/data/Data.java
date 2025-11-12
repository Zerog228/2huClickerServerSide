package me.zink.clicker.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Data {
    @JsonProperty("id")
    private long id;

    public long getID(){
        return id;
    }
}
