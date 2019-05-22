package com.qtcteam.loginmvp.presenter;

public interface ILoginPresenter extends IPresenter {

    void login(String username, String password);
    void loginOffline();

}
