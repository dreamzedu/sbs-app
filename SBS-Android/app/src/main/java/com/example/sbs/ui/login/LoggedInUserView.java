package com.example.sbs.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private String displayName;
    private int roleId;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String displayName, int roleId) {
        this.displayName = displayName;
        this.roleId = roleId;
    }

    String getDisplayName() {
        return displayName;
    }

    int getUserRole() {
        return roleId;
    }
}
