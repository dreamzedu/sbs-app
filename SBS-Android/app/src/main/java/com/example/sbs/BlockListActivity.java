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
import android.widget.ListView;
import android.widget.Toast;

import com.example.sbs.adapters.BlockListAdapter;
import com.example.sbs.data.BlockRepositoryLocal;
import com.example.sbs.data.BlockRepositoryRemote;
import com.example.sbs.data.helpers.ErrorMessageHelper;
import com.example.sbs.data.model.Block;
import com.example.sbs.data.model.Block;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class BlockListActivity extends AppCompatActivity implements IAsyncTaskInvoker {

    ProgressDialog progress = null;
    BlockRepositoryRemote repoRemote;
    BlockRepositoryLocal repoLocal;
    List<Block> blocks;
    String delBlockId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        repoRemote = new BlockRepositoryRemote(this);

        repoRemote.getAllBlocksAsync(1);

    }

    private void BindBlockList()
    {
        if (blocks == null || blocks.size() <= 0) {
            findViewById(R.id.lblNoRec).setVisibility(View.VISIBLE);
        } else {
            BlockListAdapter adapter = new BlockListAdapter(this, blocks);
            ListView list = (ListView) findViewById(R.id.listBlocks);
            list.setAdapter(adapter);
        }
    }

    public void UpdateBlockClicked(View view) {
        if (blocks != null && blocks.size() > 0) {
            String blockId = view.getTag().toString();
            for (Block s : blocks) {
                if (s.id == blockId) {
                    Gson g = new Gson();
                    Intent intent = new Intent(getApplicationContext(),
                            AddBlockActivity.class);
                    intent.putExtra("Block", g.toJson(s));
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
            Block u = g.fromJson(data.getData().toString(), Block.class);
            if (u != null) {
                int index = RetrieveIndexById(blocks, u.id);
                if (index > -1) {
                    blocks.set(index, u);
                }
                else
                {
                    blocks.add(u);
                }

                BindBlockList();
            }
        }
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

    public void DeleteBlockClicked(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.save_as_draft).setMessage(R.string.sure_to_delete).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                delBlockId = null;
                if (blocks != null && blocks.size() > 0) {
                    String blockId = view.getTag().toString();
                    for (Block s : blocks) {
                        if (s.id == blockId) {
                            delBlockId = blockId;
                            repoRemote.deleteBlockAsync(s.id);
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

                progress = new ProgressDialog(BlockListActivity.this);
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

                    JSONArray blocksJson = null;
                    JSONObject obj = null;

                    if (result != null) {
                        blocksJson = (JSONArray) new JSONTokener(result).nextValue();
                    }

                    if (blocksJson != null && blocksJson.length() == 1) {
                        obj = blocksJson.getJSONObject(0);
                    }
                    if (obj != null && obj.has("error")) {
                        String error = obj.getString("error");
                        AlertDialog.Builder builder = new AlertDialog.Builder(BlockListActivity.this);
                        builder.setTitle("Error").setMessage(error).setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Do something

                            }
                        });
                        builder.show();
                    } else {
                        if (httpAction.toLowerCase() == "get") {
                            //JSONArray object = (JSONArray) new JSONTokener(result).nextValue();
                            blocks = new ArrayList<>();
                            if (blocksJson != null) {
                                for (int i = 0; i < blocksJson.length(); i++) {
                                    JSONObject actor = blocksJson.getJSONObject(i);

                                    Gson gson = new Gson();
                                    Block u = gson.fromJson(actor.toString(), Block.class);
                                    blocks.add(u);
                                }
                            }
                            BindBlockList();
                        } else if (httpAction.toLowerCase() == "delete") {
                            if (blocks != null && blocks.size() > 0) {
                                Block b= RetrieveBlockById(blocks, delBlockId);
                                if(b!=null) {
                                    blocks.remove(b);
                                }
                                BindBlockList();
                                repoLocal.deleteBlock(b.id);
                            }
                            Toast.makeText(BlockListActivity.this, R.string.success_msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                catch(Exception ex) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BlockListActivity.this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(BlockListActivity.this);
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

    private Block RetrieveBlockById(List<Block> blocks, String id)
    {
        if(blocks != null) {
            for (int i = 0; i < blocks.size(); i++) {
                if (blocks.get(i).id == id) {
                    return blocks.get(i);
                }
            }
        }
        return null;
    }



}
