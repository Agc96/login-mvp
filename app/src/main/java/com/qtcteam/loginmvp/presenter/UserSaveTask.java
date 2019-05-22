package com.qtcteam.loginmvp.presenter;

import android.os.AsyncTask;
import android.util.Log;

import com.qtcteam.loginmvp.model.db.AppDatabase;
import com.qtcteam.loginmvp.model.db.entities.User;
import com.qtcteam.loginmvp.view.ILoginView;

import java.lang.ref.WeakReference;

public class UserSaveTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "LoginMVP_UserSaveTask";

    private WeakReference<ILoginView> view;
    private User user;

    protected UserSaveTask(ILoginView view, User user) {
        this.view = new WeakReference<>(view);
        this.user = user;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        // Verificar que la vista todavía está disponible
        ILoginView view = this.view.get();
        if (view == null) return null;

        // Iniciar la base de datos, si es que aún no se hizo
        AppDatabase database = AppDatabase.getInstance(view.getApplicationContext());
        if (database == null) {
            Log.d(TAG, "La base de datos es NULL, tal vez ya terminó la actividad?");
            return false;
        }

        // Guardar los datos del usuario en la BD, solo si no se ha guardado con anterioridad
        int count = database.userDao().countByUsername(user.getUsername());
        if (count <= 0) {
            database.userDao().insert(user);
        }
        return true;
    }

}