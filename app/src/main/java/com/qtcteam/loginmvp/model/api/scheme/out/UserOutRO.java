package com.qtcteam.loginmvp.model.api.scheme.out;

import com.google.gson.annotations.SerializedName;

public class UserOutRO extends BaseOutRO {

    @SerializedName("names")
    private String names;
    @SerializedName("email")
    private String email;

    public UserOutRO(int errorCode, String message, String names, String email) {
        super(errorCode, message);
        this.names = names;
        this.email = email;
    }

    public String getNames() {
        return names;
    }
    public String getEmail() {
        return email;
    }

}
