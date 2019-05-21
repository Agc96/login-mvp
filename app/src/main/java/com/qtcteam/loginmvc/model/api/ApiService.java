package com.qtcteam.loginmvc.model.api;

import com.qtcteam.loginmvc.model.api.scheme.in.LoginInRO;
import com.qtcteam.loginmvc.model.api.scheme.out.UserOutRO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("users/login")
    Call<UserOutRO> login(@Body LoginInRO user);

}
