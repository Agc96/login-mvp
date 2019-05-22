package com.qtcteam.loginmvp.view;

import android.content.Context;

public interface IView {

    /**
     * Retorna una referencia a la aplicación entera. Normalmente se usa este método al momento de
     * crear la base de datos.
     */
    Context getApplicationContext();

    /**
     * Muestra un mensaje en una notificación Toast.
     * @param stringId El identificador del mensaje a mostrar.
     */
    void showMessage(int stringId);

}
