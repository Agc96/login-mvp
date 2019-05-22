package com.qtcteam.loginmvp.view;

public interface ILoginView extends IView {

    void askForLoginOffline();
    void goToHomePage(String names, String email);
    void showErrorDialog(String message);
    void showErrorDialog(int stringId, Object... arguments);

}
