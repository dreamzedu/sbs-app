package com.example.sbs;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

public class MainAdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        String loggedInUser = getIntent().getStringExtra("loggedInUser");

        try {
            JSONObject obj = new JSONObject(loggedInUser);
            String username = obj.getString("username");
            TextView txtUser = (TextView) findViewById(R.id.txtUsername);
            txtUser.setText(txtUser.getText() + username);
            applyAccessLevels(obj.getInt("roleId"));
        }
        catch(Exception ex)
        {

        }
    }

    private void applyAccessLevels(Integer roleId) {
        if(roleId == 1)
        {
            findViewById(R.id.groupManageMasterData).setVisibility(View.VISIBLE);
            findViewById(R.id.groupViewReports).setVisibility(View.VISIBLE);
            findViewById(R.id.nav_manage_survey).setVisibility(View.VISIBLE);
        }
    }


    public void onNavigationButtonClicked(View item) {
        int id = item.getId();

        if (id == R.id.nav_manage_survey) {
            Intent intent = new Intent(getApplicationContext(),
                    ManageSurveyActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_take_survey) {
            Intent intent = new Intent(getApplicationContext(),
                    SurveyActivity.class);
            startActivity(intent);

        }else if (id == R.id.nav_draft_survey) {
            Intent intent = new Intent(getApplicationContext(),
                    SurveysToSyncActivity.class);
            startActivity(intent);

        }else if (id == R.id.nav_manage_panchayat) {
            Intent intent = new Intent(getApplicationContext(),
                    PanchayatListActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_manage_user) {
            Intent intent = new Intent(getApplicationContext(),
                    UserListActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_report_district) {
            Intent intent = new Intent(getApplicationContext(),
                    ReportByDistrictActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_report_benef) {
            Intent intent = new Intent(getApplicationContext(),
                    BlockListActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_report_block) {
            Intent intent = new Intent(getApplicationContext(),
                    BlockListActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_report_panchayat) {
            Intent intent = new Intent(getApplicationContext(),
                    BlockListActivity.class);
            startActivity(intent);
        }
    }
}
