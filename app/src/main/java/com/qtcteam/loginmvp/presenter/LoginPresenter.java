package com.qtcteam.loginmvp.presenter;

import android.util.Log;

import com.qtcteam.loginmvp.R;
import com.qtcteam.loginmvp.model.api.ApiAdapter;
import com.qtcteam.loginmvp.model.api.scheme.in.LoginInRO;
import com.qtcteam.loginmvp.model.api.scheme.out.UserOutRO;
import com.qtcteam.loginmvp.model.db.entities.User;
import com.qtcteam.loginmvp.view.ILoginView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class LoginPresenter implements ILoginPresenter {

    private static final String TAG = "LoginMVP_Pres_Login";
    private ILoginView view;
    private String username;
    private String password;

    public LoginPresenter(ILoginView view) {
        this.view = view;
    }

    /**
     * Valida e intenta iniciar sesión conectándose al servidor REST. Si falla la conexión (debido
     * principalmente a que no se tiene una conexión a Internet), solicita una pregunta al usuario
     * si desea iniciar sesión sin conexión.
     * @param username El nombre de usuario ingresado.
     * @param password La contraseña en texto plano ingresada.
     */
    public void login(String username, String password) {
        // Verificar que los datos sean correctos
        if (!verifyLoginData(username, password)) return;
        this.username = username;
        this.password = password;

        LoginInRO loginInRO = new LoginInRO(username, password);
        Call<UserOutRO> call = ApiAdapter.getInstance().login(loginInRO);
        call.enqueue(new Callback<UserOutRO>() {
            @Override @EverythingIsNonNull
            public void onResponse(Call<UserOutRO> call, Response<UserOutRO> response) {
                processUserResponse(response);
            }
            @Override @EverythingIsNonNull
            public void onFailure(Call<UserOutRO> call, Throwable t) {
                view.askForLoginOffline();
            }
        });
    }

    /**
     * Verifica si el usuario y la contraseña ingresados son válidos, antes de conectarse con el
     * servidor REST.
     * @param username El nombre de usuario ingresado.
     * @param password La contraseña en texto plano ingresada.
     * @return Verdadero si ambos campos son válidos, falso de otra manera.
     */
    private boolean verifyLoginData(String username, String password) {
        if (username.isEmpty()) {
            view.showMessage(R.string.login_msg_username_empty);
            return false;
        }
        if (password.isEmpty()) {
            view.showMessage(R.string.login_msg_password_empty);
            return false;
        }
        return true;
    }

    /**
     * Verifica y procesa la respuesta referente al inicio de sesión. Si los datos son válidos,
     * se guardan los datos del usuario en base de datos y se inicia la actividad que contiene al
     * menú principal.
     * @param response Respuesta HTTP obtenida desde el servidor REST.
     * @see UserSaveTask
     */
    private void processUserResponse(Response<UserOutRO> response) {
        // Verificar respuesta del servidor REST
        UserOutRO userOutRO = verifyResponse(response);
        if (userOutRO == null) return;

        // Guardar datos del usuario en la base de datos
        String names = userOutRO.getNames();
        String email = userOutRO.getEmail();
        User user = new User(0, names, email, username, password);
        new UserSaveTask(view, user).execute();

        // Ir a la pantalla principal y cerrar esta actividad
        view.goToHomePage(names, email);
    }

    /**
     * Verifica si la respuesta brindada por el servidor REST es válida, es decir, si el código
     * HTTP recibido es satisfactorio (p. ej. 200 OK), si se pudo obtener el objeto JSON embebido,
     * y además el JSON no indica un código de error (diferente de 0).
     * @param response Respuesta HTTP obtenida desde el servidor REST.
     * @return Verdadero si las tres condiciones para que la respuesta sea válida son correctas.
     * Falso de otra manera.
     */
    private UserOutRO verifyResponse(Response<UserOutRO> response) {
        // Verificar que la respuesta es satisfactoria
        if (!response.isSuccessful()) {
            view.showErrorDialog(R.string.api_dlg_http_msg, response.code());
            return null;
        }
        // Verificar el contenido de la respuesta en JSON
        UserOutRO userOutRO = response.body();
        if (userOutRO == null) {
            view.showErrorDialog(R.string.api_dlg_empty_msg);
            return null;
        }
        // Verificar que la respuesta no indique un error
        int errorCode = userOutRO.getErrorCode();
        if (errorCode != 0) {
            String message = userOutRO.getMessage();
            if (message == null) {
                view.showErrorDialog(R.string.api_dlg_service_msg, errorCode);
            } else {
                view.showErrorDialog(message);
            }
            return null;
        }
        // Si ha pasado la verificación, devolver el objeto JSON
        return userOutRO;
    }

    /**
     * Invoca una consulta en la base de datos para verificar si el usuario y la contraseña
     * ingresados pertenecen a un usuario que inició sesión en línea con anterioridad.
     * @see UserLoginTask
     */
    @Override
    public void loginOffline() {
        if (username == null || password == null) {
            Log.e(TAG, "Esta función debe ser llamada después de login().");
        } else {
            new UserLoginTask(view, username, password).execute();
        }
    }

    @Override
    public void onDestroy() {
        view = null;
    }

}
