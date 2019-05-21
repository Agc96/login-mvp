package com.qtcteam.loginmvc.controller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.qtcteam.loginmvc.R;
import com.qtcteam.loginmvc.model.api.ApiAdapter;
import com.qtcteam.loginmvc.model.api.scheme.in.LoginInRO;
import com.qtcteam.loginmvc.model.api.scheme.out.UserOutRO;
import com.qtcteam.loginmvc.model.db.AppDatabase;
import com.qtcteam.loginmvc.model.db.entities.User;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LOGINMVC_ACT_LOGIN";
    public static final String LOGIN_EXTRA_NAMES = "NAMES";
    public static final String LOGIN_EXTRA_EMAIL = "EMAIL";
    private EditText mUsername;
    private EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.login_txt_title);

        mUsername = findViewById(R.id.login_ipt_username);
        mPassword = findViewById(R.id.login_ipt_password);
    }

    public void login(View v) {
        // Esconder el teclado
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        // Verificar datos de usuario
        final String username = mUsername.getText().toString();
        final String password = mPassword.getText().toString();
        if (!verifyLoginData(username, password)) return;

        // Consultar al API REST
        LoginInRO loginInRO = new LoginInRO(username, password);
        Call<UserOutRO> call = ApiAdapter.getInstance().login(loginInRO);
        call.enqueue(new Callback<UserOutRO>() {
            @Override
            public void onResponse(@NonNull Call<UserOutRO> call, @NonNull Response<UserOutRO> response) {
                processUserResponse(response, username, password);
            }
            @Override
            public void onFailure(@NonNull Call<UserOutRO> call, @NonNull Throwable t) {
                loginOffline(username, password);
            }
        });

        // Notificar al usuario que se está iniciando sesión
        Toast.makeText(this, R.string.login_msg_loading, Toast.LENGTH_SHORT).show();
    }

    private boolean verifyLoginData(String username, String password) {
        // Verificar nombre de usuario
        if (username.isEmpty()) {
            Toast.makeText(this, R.string.login_msg_username_empty, Toast.LENGTH_SHORT).show();
            return false;
        }
        // Verificar contraseña
        if (password.isEmpty()) {
            Toast.makeText(this, R.string.login_msg_password_empty, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void processUserResponse(Response<UserOutRO> response, String username, String password) {
        // Verificar respuesta de la API REST
        UserOutRO userOutRO = verifyResponse(response);
        if (userOutRO == null) return;

        // Guardar datos del usuario en la base de datos
        String names = userOutRO.getNames();
        String email = userOutRO.getEmail();
        User user = new User(0, names, email, username, password);
        new UserSaveTask(this, user).execute();

        // Ir a la pantalla principal y cerrar esta actividad
        goToHomepage(user);
    }

    private UserOutRO verifyResponse(Response<UserOutRO> response) {
        // Verificar que la respuesta es satisfactoria
        if (!response.isSuccessful()) {
            String message = String.format(getString(R.string.api_dlg_http_msg),
                    response.code());
            showErrorDialog(message);
            return null;
        }
        // Obtener respuesta en JSON
        UserOutRO userOutRO = response.body();
        if (userOutRO == null) {
            showErrorDialog(getString(R.string.api_dlg_empty_msg));
            return null;
        }
        // Verificar que la respuesta no indique un error
        int errorCode = userOutRO.getErrorCode();
        if (errorCode != 0) {
            String message = userOutRO.getMessage();
            if (message == null) {
                message = String.format(getString(R.string.api_dlg_service_msg), errorCode);
            }
            showErrorDialog(message);
            return null;
        }
        return userOutRO;
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.login_dlg_error_title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void loginOffline(final String username, final String password) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.login_dlg_offline_title)
                .setMessage(R.string.login_dlg_offline_msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new UserLoginTask(LoginActivity.this, username, password).execute();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void goToHomepage(User user) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(LOGIN_EXTRA_NAMES, user.getNames());
        intent.putExtra(LOGIN_EXTRA_EMAIL, user.getEmail());
        startActivity(intent);
        // Cerrar esta actividad
        finish();
    }

    private static class UserSaveTask extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<LoginActivity> activity;
        private User user;

        UserSaveTask(LoginActivity activity, User user) {
            this.activity = new WeakReference<>(activity);
            this.user = user;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            AppDatabase database = AppDatabase.getInstance(activity.get());
            if (database == null) {
                Log.d(TAG, "UserSaveTask: DB is null, maybe the activity has finished?");
                return false;
            }
            int count = database.userDao().countByUsername(user.getUsername());
            if (count <= 0) {
                database.userDao().insert(user);
            }
            return true;
        }
    }

    private static class UserLoginTask extends AsyncTask<Void, Void, User> {

        private WeakReference<LoginActivity> activity;
        private String username;
        private String password;

        UserLoginTask(LoginActivity activity, String username, String password) {
            this.activity = new WeakReference<>(activity);
            this.username = username;
            this.password = password;
        }

        @Override
        protected User doInBackground(Void... voids) {
            AppDatabase database = AppDatabase.getInstance(activity.get());
            if (database == null) {
                Log.d(TAG, "UserLoginTask: DB is null, maybe the activity has finished?");
                return null;
            }
            return database.userDao().login(username, password);
        }

        @Override
        protected void onPostExecute(User user) {
            // Check if activity still exists
            LoginActivity activity = this.activity.get();
            if (activity == null) return;
            // Check if user was found
            if (user != null) {
                activity.goToHomepage(user);
            } else {
                activity.showErrorDialog(activity.getString(R.string.login_dlg_error_msg_not_found));
            }
        }

    }

}
