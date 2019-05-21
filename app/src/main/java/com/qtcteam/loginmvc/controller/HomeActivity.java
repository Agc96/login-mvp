package com.qtcteam.loginmvc.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.qtcteam.loginmvc.R;

public class HomeActivity extends AppCompatActivity {

    public static final String TAG = "LOGINMVC_ACT_HOME";
    private TextView mNames;
    private TextView mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle(R.string.home_txt_title);

        mNames = findViewById(R.id.home_txt_names);
        mEmail = findViewById(R.id.home_txt_email);
        showUserDetails();
    }

    private void showUserDetails() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mNames.setText(extras.getString(LoginActivity.LOGIN_EXTRA_NAMES));
            mEmail.setText(extras.getString(LoginActivity.LOGIN_EXTRA_EMAIL));
        }
    }

    public void logout(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
