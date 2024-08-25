package com.lessons.sync.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "_shards", })   // Tell Jackson to ignore the "_shards" fields
public class ElasticSearchDtoGetCount {
    private Integer count;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
