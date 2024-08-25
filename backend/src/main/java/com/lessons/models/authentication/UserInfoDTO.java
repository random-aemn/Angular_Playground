package com.lessons.models.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;


public class UserInfoDTO {

    @JsonProperty("loggedInUserId")
    private final Integer loggedInUserId;

    @JsonProperty("loggedInUserName")
    private final String loggedInUserName;

    @JsonProperty("loggedInFullName")
    private final String loggedInFullName;

    @JsonProperty("pageRoutes")
    private final Map<String, Boolean> pageRoutes;

    @JsonProperty("displayedRoleName")
    private final String displayedRoleName;

    // ----------------------- Constructors & Getters -------------------------------------------

    public UserInfoDTO(Integer aLoggedInUserId, String aLoggedInUserName, String aLoggedInFullName, Map<String, Boolean> aPageRoutes, String aDisplayedRoleName) {
        this.loggedInUserId   = aLoggedInUserId;
        this.loggedInUserName = aLoggedInUserName;
        this.loggedInFullName = aLoggedInFullName;
        this.pageRoutes       = aPageRoutes;
        this.displayedRoleName = aDisplayedRoleName;
    }

    public String getDisplayedRoleName() {
        return displayedRoleName;
    }

    public Integer getLoggedInUserId() {
        return loggedInUserId;
    }

    public String getLoggedInUserName() {
        return loggedInUserName;
    }

    public String getLoggedInFullName() {
        return loggedInFullName;
    }

    public Map<String, Boolean> getPageRoutes() {
        return pageRoutes;
    }
}
