package com.example.sbs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.sbs.data.DataBaseHelper;
import com.example.sbs.ui.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        DataBaseHelper dbHelper = new DataBaseHelper(this);
        try {
            dbHelper.createDataBase(false);
        }
        catch(Exception ex)
        {

        }
        Intent intent = null;
        if (true)
        {
            intent = new Intent(getApplicationContext(),
                    MainAdminActivity.class);
            intent.putExtra("loggedInUser", "{'roleId': 1, 'username':'test user'}");
        }
        else {
            intent = new Intent(getApplicationContext(),
                    SyncDbActivity.class);
        }
        startActivity(intent);
        finish();
    }

}
