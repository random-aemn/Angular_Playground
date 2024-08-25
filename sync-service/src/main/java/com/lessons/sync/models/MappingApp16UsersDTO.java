package com.lessons.sync.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MappingApp16UsersDTO {


    /* This object represent one APP16_USERS record in ElasticSearch
     *
     * This object is converted into JSON and sent to ElasticSearch
     * So, if you need to change an ES mapping field name, then change the @JsonProperty(" ")
     *
     * NOTE:
     *   The name of the private variables do *NOT* matter
     *   The name of the public  setters  MATTERS -- spring-jdbc uses them to set the values
     *   The name of the @JsonProperty    MATTERS -- it must match the ElasticSearch field name
     */

    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("last_updated_date")
    private String lastUpdatedDate;

    @JsonProperty("created_date")
    private String createdDate;


    // -------------------- Getters & Setters --------------------------------
    // NOTE: SpringJdbc calls the setters   (to pull from the database
    //       ObjectMapper calls the getters (to store in ElasticSearch)
    // -----------------------------------------------------------------------

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(String lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
