package com.qtcteam.loginmvp.model.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.qtcteam.loginmvp.model.db.dao.UserDao;
import com.qtcteam.loginmvp.model.db.entities.User;

@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "loginmvp.db";
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,
                    DB_NAME).build();
        }
        return INSTANCE;
    }

    /// region DAOs
    public abstract UserDao userDao();
    /// endregion

}
