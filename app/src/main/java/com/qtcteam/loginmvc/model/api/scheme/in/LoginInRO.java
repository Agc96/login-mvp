package com.qtcteam.loginmvc.model.api.scheme.in;

import com.google.gson.annotations.SerializedName;

public class LoginInRO extends BaseInRO {

    @SerializedName("username")
    private String username;
    @SerializedName("password")
    private String password;

    public LoginInRO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }

}
