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
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sbs.data.BlockRepositoryLocal;
import com.example.sbs.data.BlockRepositoryRemote;
import com.example.sbs.data.PanchayatRepositoryRemote;
import com.example.sbs.data.helpers.ErrorMessageHelper;
import com.example.sbs.data.model.Block;
import com.example.sbs.data.model.Panchayat;
import com.example.sbs.data.model.Role;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddPanchayatActivity extends AppCompatActivity implements IAsyncTaskInvoker {

    ProgressDialog progress = null;
    PanchayatRepositoryRemote repoRemote;
    BlockRepositoryLocal repoBlockLocal;
    List<Block> blocks = new ArrayList<Block>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_panchayat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        repoBlockLocal = new BlockRepositoryLocal(this);
        blocks = repoBlockLocal.getBlocks(1);

        Spinner blockSpinner = (Spinner) findViewById(R.id.blockSpinner);

        // Spinner click listener
        //spinner.setOnItemSelectedListener(this);

        List<String> blockNames = new ArrayList<String>();
        blockNames.add("Select Block");
        // Creating adapter for spinner
        ArrayAdapter<String> blcokAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, blockNames);

        blockNames = GetBlockNames();
        blcokAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, blockNames);

        // attaching data adapter to spinner
        blockSpinner.setAdapter(blcokAdapter);

        String p = getIntent().getStringExtra("Panchayat");
        if(p != null)
        {
            Gson g = new Gson();
            Panchayat panchayat = g.fromJson(p, Panchayat.class);
            PopulatePanchayatDetail(panchayat);
        }
    }

    private List<String> GetBlockNames() {
        List<String> bNames = new ArrayList<String>();
        bNames.add("Select Block");
        for (int i = 0; i < blocks.size(); i++) {
            bNames.add(blocks.get(i).name);
        }
        return bNames;
    }

    private void PopulatePanchayatDetail(Panchayat obj) {
        ((EditText)findViewById(R.id.txtPanchayatId)).setText(obj.id);
        ((EditText)findViewById(R.id.txtPanchayatName)).setText(obj.name);
        ((Spinner) findViewById(R.id.blockSpinner)).setSelection(RetrieveIndexById(blocks, obj.blockId) + 1);
    }

    private Integer RetrieveIndexById(List<Block> list, String id)
    {
        if(list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).id.equals(id)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private Panchayat GetPanchayatFromControls()
    {
        Panchayat  obj = new Panchayat();

        obj.id = ((EditText)findViewById(R.id.txtPanchayatId)).getText().toString();
        obj.name = ((EditText)findViewById(R.id.txtPanchayatName)).getText().toString();
        obj.blockId = blocks.get(((Spinner)findViewById(R.id.blockSpinner)).getSelectedItemPosition() - 1).id;

        return obj;
    }
    public void SavePanchayatClicked(View view)
    {
        Panchayat u = GetPanchayatFromControls();
        PanchayatRepositoryRemote repoRemote = new PanchayatRepositoryRemote(this);
        if(u.id != null && !TextUtils.isEmpty(u.id))
        {
            repoRemote.updatePanchayatAsync(u);
        }
        else {
            repoRemote.savePanchayatAsync(u);
        }
    }


    public void OnBeforeExecute(final String httpAction) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                progress = new ProgressDialog(AddPanchayatActivity.this);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddPanchayatActivity.this);
                        builder.setTitle("Error").setMessage(validation).setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Do something

                            }
                        });
                        builder.show();
                    }
                    else
                    {
                        Toast.makeText(AddPanchayatActivity.this, R.string.success_msg, Toast.LENGTH_SHORT).show();

                        Panchayat p = GetPanchayatFromControls();
                        if(httpAction == "post")
                        {
                            if(obj == null || obj.toString().equals("0"))
                            {
                                throw new Exception("error");
                            }
                            p.id = obj.toString();
                        }
                        Intent data = new Intent();
                        Gson g = new Gson();
                        data.setData(Uri.parse(g.toJson(p)));
                        setResult(RESULT_OK, data);
                        AddPanchayatActivity.this.finish();
                    }
                }
                catch(Exception ex)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddPanchayatActivity.this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(AddPanchayatActivity.this);
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
