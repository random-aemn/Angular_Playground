package com.lessons.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GetChart2DataDTO {
    @JsonProperty("name")
    private String name;

    @JsonProperty("data")
    private List<Integer> data;

//    GETTERS AND SETTERS


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getData() {
        return data;
    }

    public void setData(List<Integer> data) {
        this.data = data;
    }
}
