package com.qtcteam.loginmvp.model.api.scheme.out;

import com.google.gson.annotations.SerializedName;

public class BaseOutRO {

    @SerializedName("errorCode")
    private int errorCode;
    @SerializedName("message")
    private String message;

    public BaseOutRO(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }
    public String getMessage() {
        return message;
    }

}
