package com.lessons.models.authentication;


public class InitialUserInfoDTO {

    private final Integer userId;
    private final boolean isLocked;

    // ---------------- Constructor & Getters ----------------------------

    public InitialUserInfoDTO(Integer aUserId, boolean aIsLocked) {
        this.userId = aUserId;
        this.isLocked = aIsLocked;
    }



    public Integer getUserId() {
        return userId;
    }

    public boolean getIsLocked() {
        return isLocked;
    }


}
