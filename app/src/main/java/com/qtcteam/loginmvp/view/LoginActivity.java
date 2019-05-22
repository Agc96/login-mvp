package com.qtcteam.loginmvp.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.qtcteam.loginmvp.R;
import com.qtcteam.loginmvp.model.api.ApiAdapter;
import com.qtcteam.loginmvp.model.api.scheme.in.LoginInRO;
import com.qtcteam.loginmvp.model.api.scheme.out.UserOutRO;
import com.qtcteam.loginmvp.model.db.AppDatabase;
import com.qtcteam.loginmvp.model.db.entities.User;
import com.qtcteam.loginmvp.presenter.ILoginPresenter;
import com.qtcteam.loginmvp.presenter.LoginPresenter;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements ILoginView {

    private static final String TAG = "LoginMVP_Act_Login";
    public static final String LOGIN_EXTRA_NAMES = "names";
    public static final String LOGIN_EXTRA_EMAIL = "email";
    private EditText mUsername;
    private EditText mPassword;
    private ILoginPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.login_txt_title);

        mUsername  = findViewById(R.id.login_ipt_username);
        mPassword  = findViewById(R.id.login_ipt_password);
        mPresenter = new LoginPresenter(this);
    }

    /**
     * Inicia sesión consultando al servicio API REST si es que hay conexión a Internet. Si es que
     * Internet no está disponible, consulta según el historial de inicio de sesión guardado en la
     * base de datos.
     * @param v El botón que se presionó para iniciar este método. No se utiliza este atributo.
     */
    public void login(View v) {
        // Esconder el teclado
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        // Obtener datos de usuario
        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();

        // Iniciar sesión y notificar al usuario que se está iniciando sesión
        mPresenter.login(username, password);
        showMessage(R.string.login_msg_loading);
    }

    /**
     * Realiza una consulta al usuario si es que este desea iniciar sesión sin conexión, esto es,
     * verificando su usuario y contraseña ante un registro histórico de inicios de sesión en la
     * base de datos.
     */
    public void askForLoginOffline() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.login_dlg_offline_title)
                .setMessage(R.string.login_dlg_offline_msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.loginOffline();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    /**
     * Inicia la actividad que contiene el menú principal de la aplicación, luego de iniciar sesión
     * correctamente.
     * @param names El nombre completo del usuario.
     * @param email El correo electrónico del usuario.
     */
    public void goToHomePage(String names, String email) {
        // Iniciar la actividad principal
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(LOGIN_EXTRA_NAMES, names);
        intent.putExtra(LOGIN_EXTRA_EMAIL, email);
        startActivity(intent);
        // Cerrar esta actividad
        finish();
    }

    /**
     * Muestra un mensaje en una notificación Toast.
     * @param stringId El identificador del mensaje a mostrar.
     */
    public void showMessage(@StringRes int stringId) {
        Toast.makeText(this, stringId, Toast.LENGTH_SHORT).show();
    }

    /**
     * Muestra un cuadro de diálogo con un mensaje, indicando un error al iniciar sesión.
     * @param message El mensaje a mostrar.
     */
    public void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.login_dlg_error_title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    /**
     * Versión de {@link LoginActivity#showErrorDialog(String)} que utiliza un identificador del
     * mensaje a mostrar, que deberá ser formateado según los argumentos brindados.
     * @param stringId El identificador del mensaje a mostrar.
     * @param arguments Argumentos para el formateo del mensaje a mostrar.
     */
    public void showErrorDialog(@StringRes int stringId, Object... arguments) {
        String message = String.format(getString(stringId), arguments);
        showErrorDialog(message);
    }

    /**
     * Antes de terminar de destruir la actividad, limpiamos la referencia a la vista que tiene el
     * presentador, evitando así <a href="https://en.wikipedia.org/wiki/Memory_leak">fugas de memoria</a>.
     */
    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

}
