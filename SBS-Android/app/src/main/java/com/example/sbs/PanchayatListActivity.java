package com.example.sbs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sbs.adapters.BlockListAdapter;
import com.example.sbs.adapters.PanchayatListAdapter;
import com.example.sbs.data.BlockRepositoryLocal;
import com.example.sbs.data.BlockRepositoryRemote;
import com.example.sbs.data.PanchayatRepositoryLocal;
import com.example.sbs.data.PanchayatRepositoryRemote;
import com.example.sbs.data.helpers.ErrorMessageHelper;
import com.example.sbs.data.model.Block;
import com.example.sbs.data.model.Panchayat;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class PanchayatListActivity extends AppCompatActivity implements IAsyncTaskInvoker {

    BlockRepositoryLocal repoBlockLocal;

    List<Block> blocks = new ArrayList<Block>();
    List<Panchayat> panchayats = new ArrayList<Panchayat>();
    ProgressDialog progress = null;
    PanchayatRepositoryRemote repoPanchayatRemote;
    String delPanchayatId;
    String selectedBlockId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panchayat_list);
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

        blockSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0) {
                    selectedBlockId = "";
                    FetchPanchayats("");
                } else {
                    Block selecteditem = blocks.get(position - 1);
                    selectedBlockId = selecteditem.id;
                    FetchPanchayats(selecteditem.id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

    }

    private void FetchPanchayats(String id) {

        repoPanchayatRemote = new PanchayatRepositoryRemote(this);
        repoPanchayatRemote.getAllPanchayatsAsync(id);
    }

    private void BindPanchayats() {
        if (panchayats == null || panchayats.size() <= 0) {
            findViewById(R.id.lblNoRec).setVisibility(View.VISIBLE);
            findViewById(R.id.listPanchayats).setVisibility(View.GONE);
        } else {
            findViewById(R.id.lblNoRec).setVisibility(View.GONE);
            findViewById(R.id.listPanchayats).setVisibility(View.VISIBLE);
            PanchayatListAdapter adapter = new PanchayatListAdapter(this, panchayats);
            ListView list = (ListView) findViewById(R.id.listPanchayats);
            list.setAdapter(adapter);
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

    public void UpdatePanchayatClicked(View view) {
        if (panchayats != null && panchayats.size() > 0) {
            String pId = view.getTag().toString();
            for (Panchayat s : panchayats) {
                if (s.id == pId) {
                    Gson g = new Gson();
                    Intent intent = new Intent(getApplicationContext(),
                            AddPanchayatActivity.class);
                    s.blockId = selectedBlockId;
                    intent.putExtra("Panchayat", g.toJson(s));
                    startActivityForResult(intent, 1);
                    break;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && data != null) {
            Gson g = new Gson();
            Panchayat u = g.fromJson(data.getData().toString(), Panchayat.class);
            if (u != null) {
                int index = RetrieveIndexById(panchayats, u.id);
                if (index > -1) {
                    panchayats.set(index, u);
                }
                else
                {
                    panchayats.add(u);
                }

                BindPanchayats();
            }
        }
    }

    private Integer RetrieveIndexById(List<Panchayat> list, String id)
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

    public void DeletePanchayatClicked(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete).setMessage(R.string.sure_to_delete).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                delPanchayatId = null;
                if (panchayats != null && panchayats.size() > 0) {
                    String blockId = view.getTag().toString();
                    for (Panchayat s : panchayats) {
                        if (s.id == blockId) {
                            delPanchayatId = blockId;
                            repoPanchayatRemote.deletePanchayatAsync(s.id);
                            break;
                        }
                    }
                }
            }
        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Do Nothing
            }
        });

        builder.show();
    }


    public void OnBeforeExecute(final String httpAction) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                progress = new ProgressDialog(PanchayatListActivity.this);
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                if(httpAction.toLowerCase() == "get")
                    progress.setMessage(getString(R.string.fetching_data));
                else
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

                    JSONArray panchayatsJson = null;
                    JSONObject obj = null;

                    if (result != null) {
                        panchayatsJson = (JSONArray) new JSONTokener(result).nextValue();
                    }

                    if (panchayatsJson != null && panchayatsJson.length() == 1) {
                        obj = panchayatsJson.getJSONObject(0);
                    }
                    if (obj != null && obj.has("error")) {
                        String error = obj.getString("error");
                        AlertDialog.Builder builder = new AlertDialog.Builder(PanchayatListActivity.this);
                        builder.setTitle("Error").setMessage(error).setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Do something

                            }
                        });
                        builder.show();
                    } else {
                        if (httpAction.toLowerCase() == "get") {
                            //JSONArray object = (JSONArray) new JSONTokener(result).nextValue();
                            panchayats = new ArrayList<>();
                            if (panchayatsJson != null) {
                                for (int i = 0; i < panchayatsJson.length(); i++) {
                                    JSONObject actor = panchayatsJson.getJSONObject(i);

                                    Gson gson = new Gson();
                                    Panchayat u = gson.fromJson(actor.toString(), Panchayat.class);
                                    panchayats.add(u);
                                }
                            }
                            BindPanchayats();
                        } else if (httpAction.toLowerCase() == "delete") {
                            if (panchayats != null && panchayats.size() > 0) {
                                Panchayat usr= RetrievePanchayatById(panchayats, delPanchayatId);
                                if(usr!=null) {
                                    panchayats.remove(usr);
                                }
                                BindPanchayats();
                            }
                            Toast.makeText(PanchayatListActivity.this, R.string.success_msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                catch(Exception ex) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PanchayatListActivity.this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(PanchayatListActivity.this);
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

    private Panchayat RetrievePanchayatById(List<Panchayat> panchayats, String id)
    {
        if(panchayats != null) {
            for (int i = 0; i < panchayats.size(); i++) {
                if (panchayats.get(i).id == id) {
                    return panchayats.get(i);
                }
            }
        }
        return null;
    }


}
