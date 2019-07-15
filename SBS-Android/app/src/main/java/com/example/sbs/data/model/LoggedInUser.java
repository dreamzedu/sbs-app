package com.example.sbs.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userId;
    private String displayName;
    private int roleId;

    public LoggedInUser(String userId, String displayName, int roleId) {
        this.userId = userId;
        this.displayName = displayName;
        this.roleId = roleId;

    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getRoleId() {
        return roleId;
    }
}
