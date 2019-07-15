package com.example.sbs;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.sbs.init.AppDbSyncronizer;
import com.example.sbs.network.NetworkChecker;
import com.example.sbs.ui.login.LoginActivity;

public class SyncDbActivity extends AppCompatActivity {

    ProgressDialog progress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_db);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    public void syncWithServerClicked(View view)
    {
        if(NetworkChecker.isNetworkAvailable(this)) {
            showProgressBar();
            startDbSync();
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(SyncDbActivity.this);
            builder.setTitle("Error").setMessage(R.string.no_internet).setNeutralButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Do something

                }
            });
            builder.show();
        }
    }

    private void startDbSync()
    {
        AppDbSyncronizer dbSync = new AppDbSyncronizer();
        dbSync.SyncAppDbWithServer(this);
    }

    public void dbSyncCompleteSuccessfully(String msg)
    {
        progress.dismiss();
        Toast.makeText(SyncDbActivity.this, R.string.sync_success, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getApplicationContext(),
                LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void skipClicked(View view)
    {
        Intent intent = new Intent(getApplicationContext(),
                LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void dbSyncCompleteWithError(String msg)
    {
        progress.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(SyncDbActivity.this);
        builder.setTitle("Error").setMessage(msg).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Do something

            }
        });
        builder.show();


    }

    private void showProgressBar()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                progress = new ProgressDialog(SyncDbActivity.this);
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setMessage(getString(R.string.sync_with_server));
                progress.setIndeterminate(true);
                progress.show();
            }
        });
    }

}
