package com.qtcteam.loginmvp.model.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.qtcteam.loginmvp.model.db.entities.User;

@Dao
public interface UserDao {

    @Query("SELECT * FROM USUARIO WHERE USERNAME = :username AND PASSWORD = :password LIMIT 1")
    User login(String username, String password);

    @Query("SELECT COUNT(*) FROM USUARIO WHERE USERNAME = :username")
    int countByUsername(String username);

    @Insert
    void insert(User user);

    @Delete
    void delete(User user);

}
