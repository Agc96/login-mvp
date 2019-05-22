package com.qtcteam.loginmvp.presenter;

public interface IPresenter {

    /**
     * Limpia la referencia a la vista que tiene el presentador, evitando así
     * <a href="https://en.wikipedia.org/wiki/Memory_leak">fugas de memoria</a>.
     */
    void onDestroy();

}
