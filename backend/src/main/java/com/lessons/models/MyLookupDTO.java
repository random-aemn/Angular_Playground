package com.lessons.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MyLookupDTO {

    // @JsonProperty annotation lets Spring know how to match up the backend variable with the frontend variable
    // The text in quotes MUST match the variable names on the frontend DTO

    @JsonProperty("id")
    private int id;

    @JsonProperty("value")
    private String value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
