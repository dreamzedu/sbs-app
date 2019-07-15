package com.example.sbs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sbs.data.BlockRepositoryLocal;
import com.example.sbs.data.BlockRepositoryRemote;
import com.example.sbs.data.UserRepositoryLocal;
import com.example.sbs.data.UserRepositoryRemote;
import com.example.sbs.data.helpers.ErrorMessageHelper;
import com.example.sbs.data.model.Block;
import com.example.sbs.data.model.Permission;
import com.example.sbs.data.model.Role;
import com.example.sbs.data.model.User;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AddBlockActivity extends AppCompatActivity implements IAsyncTaskInvoker {

    ProgressDialog progress = null;
    BlockRepositoryLocal repoLocal;
    BlockRepositoryRemote repoRemote;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_block);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    private void PopulateBlockDetail(Block obj) {
        ((EditText)findViewById(R.id.txtBlockId)).setText(obj.id);
        ((EditText)findViewById(R.id.txtBlockName)).setText(obj.name);
    }

    private Block GetBlockFromControls()
    {
        Block  obj = new Block();

        obj.id = ((EditText)findViewById(R.id.txtBlockId)).getText().toString();
        obj.name = ((EditText)findViewById(R.id.txtBlockName)).getText().toString();

        return obj;
    }

    public void SaveBlockClicked(View view)
    {
        Block u = GetBlockFromControls();
        BlockRepositoryRemote repoRemote = new BlockRepositoryRemote(this);
        if(u.id != null && u.id.trim() != "")
        {
            repoRemote.updateBlockAsync(u);
        }
        else {
            repoRemote.saveBlockAsync(u);
        }
    }

    public void OnBeforeExecute(final String httpAction) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                progress = new ProgressDialog(AddBlockActivity.this);
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setMessage(getString(R.string.processing_request));
                progress.setIndeterminate(true);
                progress.show();
            }
        });
    }

    public void OnAfterExecute(final String result, final String httpAction) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.dismiss();
                try {

                    JSONObject obj = null;
                    if(result != null) {
                        obj = new JSONObject(result);
                    }

                    if(obj != null && obj.has("error"))
                    {
                        String validation = obj.getString("error");
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddBlockActivity.this);
                        builder.setTitle("Error").setMessage(validation).setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Do something

                            }
                        });
                        builder.show();
                    }
                    else
                    {
                        Toast.makeText(AddBlockActivity.this, R.string.success_msg, Toast.LENGTH_SHORT).show();
                        Block b =GetBlockFromControls();
                        if(httpAction == "post")
                        {
                            if(obj == null || obj.toString().equals("0"))
                            {
                                throw new Exception("error");
                            }
                            b.id = obj.toString();
                            repoLocal.insertBlock(b);
                        }
                        else if(httpAction.equals("put"))
                        {
                            repoLocal.updateBlock(b);
                        }
                        Intent data = new Intent();
                        Gson g = new Gson();
                        data.setData(Uri.parse(g.toJson(b)));
                        setResult(RESULT_OK, data);
                        AddBlockActivity.this.finish();
                    }
                }
                catch(Exception ex)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddBlockActivity.this);
                    builder.setTitle("Error").setMessage(R.string.generic_error).setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Do something

                        }
                    });
                    builder.show();
                }

            }
        });
    }

    @Override
    public void OnError(String errorCode) {
        progress.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(AddBlockActivity.this);
        builder.setTitle("Error").setMessage(ErrorMessageHelper.GetMessage(errorCode, this)).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Do something

            }
        });
        builder.show();
    }

    @Override
    public Context getContext()
    {
        return this;
    }
}
