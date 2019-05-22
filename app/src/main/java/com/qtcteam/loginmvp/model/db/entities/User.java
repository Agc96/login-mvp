package com.qtcteam.loginmvp.model.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "USUARIO")
public class User {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_USUARIO")
    private int userId;
    @ColumnInfo(name = "NOMBRES")
    private String names;
    @ColumnInfo(name = "CORREO")
    private String email;
    @ColumnInfo(name = "USERNAME")
    private String username;
    @ColumnInfo(name = "PASSWORD")
    private String password;

    public User(int userId, String names, String email, String username, String password) {
        this.userId = userId;
        this.names = names;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public int getUserId() {
        return userId;
    }
    public String getNames() {
        return names;
    }
    public String getEmail() {
        return email;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }

}
