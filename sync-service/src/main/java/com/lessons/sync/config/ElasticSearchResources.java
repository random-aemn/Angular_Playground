package com.lessons.sync.config;

import com.ning.http.client.AsyncHttpClient;

public class ElasticSearchResources {
    private final AsyncHttpClient asyncHttpClient;
    private final String          elasticSearchUrl;


    // Used for test classes
    public ElasticSearchResources(String aElasticSearchUrl, AsyncHttpClient asyncHttpClient) {
        this.elasticSearchUrl = aElasticSearchUrl;
        this.asyncHttpClient = asyncHttpClient;
    }


    public String getElasticSearchUrl() {
        return elasticSearchUrl;
    }

    public AsyncHttpClient getAsyncHttpClient() {
        return this.asyncHttpClient;
    }
}
