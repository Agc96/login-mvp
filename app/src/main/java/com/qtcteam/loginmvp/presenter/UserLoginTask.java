package com.qtcteam.loginmvp.presenter;

import android.os.AsyncTask;
import android.util.Log;

import com.qtcteam.loginmvp.R;
import com.qtcteam.loginmvp.model.db.AppDatabase;
import com.qtcteam.loginmvp.model.db.entities.User;
import com.qtcteam.loginmvp.view.ILoginView;

import java.lang.ref.WeakReference;

public class UserLoginTask extends AsyncTask<Void, Void, User> {

    private static final String TAG = "LoginMVP_UserLoginTask";

    private WeakReference<ILoginView> view;
    private String username;
    private String password;

    protected UserLoginTask(ILoginView view, String username, String password) {
        this.view = new WeakReference<>(view);
        this.username = username;
        this.password = password;
    }

    @Override
    protected User doInBackground(Void... voids) {
        // Verificar que la vista todavía está disponible
        ILoginView view = this.view.get();
        if (view == null) return null;

        // Iniciar la base de datos, si es que aún no se hizo
        AppDatabase database = AppDatabase.getInstance(view.getApplicationContext());
        if (database == null) {
            Log.d(TAG, "La base de datos es NULL, tal vez ya terminó la actividad?");
            return null;
        }

        // Buscar el usuario con el nombre de usuario y contraseña proporcionados.
        return database.userDao().login(username, password);
    }

    @Override
    protected void onPostExecute(User user) {
        // Verificar que la vista todavía está disponible
        ILoginView view = this.view.get();
        if (view == null) return;

        // Verificar si el usuario fue encontrado
        if (user != null) {
            view.goToHomePage(user.getNames(), user.getEmail());
        } else {
            view.showErrorDialog(R.string.login_dlg_error_msg_not_found);
        }
    }

}
