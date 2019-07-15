package com.example.sbs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sbs.adapters.UserListAdapter;
import com.example.sbs.data.UserRepositoryLocal;
import com.example.sbs.data.UserRepositoryRemote;
import com.example.sbs.data.helpers.Converter;
import com.example.sbs.data.helpers.ErrorMessageHelper;
import com.example.sbs.data.model.User;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserListActivity extends AppCompatActivity implements IAsyncTaskInvoker {

    ProgressDialog progress = null;
    UserRepositoryRemote repoRemote;
    UserRepositoryLocal repoLocal;
    List<User> users;
    String delUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        repoRemote = new UserRepositoryRemote(this);
        repoLocal = new UserRepositoryLocal(this);

        repoRemote.getAllUsersAsync();

    }

    private void BindUserList()
    {
        if (users == null || users.size() <= 0) {
            findViewById(R.id.lblNoRec).setVisibility(View.VISIBLE);
        } else {
            UserListAdapter adapter = new UserListAdapter(this, users);
            ListView list = (ListView) findViewById(R.id.listUsers);
            list.setAdapter(adapter);
        }
    }

    public void UpdateUserClicked(View view) {
        if (users != null && users.size() > 0) {
            String userId = view.getTag().toString();
            for (User s : users) {
                if (s.id == userId) {
                    Gson g = new Gson();
                    Intent intent = new Intent(getApplicationContext(),
                            AddUserActivity.class);
                    intent.putExtra("User", g.toJson(s));
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
            User u = g.fromJson(data.getData().toString(), User.class);
            if (u != null) {
                int index = RetrieveIndexById(users, u.id);
                if (index > -1) {
                    users.set(index, u);
                }
                else
                {
                    users.add(u);
                }

                BindUserList();
            }
        }
    }

    private Integer RetrieveIndexById(List<User> list, String id)
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

    public void DeleteUserClicked(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.save_as_draft).setMessage(R.string.sure_to_delete).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                delUserId = null;
                if (users != null && users.size() > 0) {
                    String userId = view.getTag().toString();
                    for (User s : users) {
                        if (s.id == userId) {
                            delUserId = userId;
                            repoRemote.deleteUserAsync(s.id);
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

                progress = new ProgressDialog(UserListActivity.this);
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

                    JSONArray usersJson = null;
                    JSONObject obj = null;

                    if (result != null) {
                        usersJson = (JSONArray) new JSONTokener(result).nextValue();
                    }

                    if (usersJson != null && usersJson.length() == 1) {
                        obj = usersJson.getJSONObject(0);
                    }
                    if (obj != null && obj.has("error")) {
                        String error = obj.getString("error");
                        AlertDialog.Builder builder = new AlertDialog.Builder(UserListActivity.this);
                        builder.setTitle("Error").setMessage(error).setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Do something

                            }
                        });
                        builder.show();
                    } else {
                        if (httpAction.toLowerCase() == "get") {
                            //JSONArray object = (JSONArray) new JSONTokener(result).nextValue();
                            users = new ArrayList<>();
                            if (usersJson != null) {
                                for (int i = 0; i < usersJson.length(); i++) {
                                    JSONObject actor = usersJson.getJSONObject(i);

                                    Gson gson = new Gson();
                                    User u = gson.fromJson(actor.toString(), User.class);
                                    users.add(u);
                                }
                            }
                            BindUserList();
                        } else if (httpAction.toLowerCase() == "delete") {
                            if (users != null && users.size() > 0) {
                                User usr= RetrieveUserById(users, delUserId);
                                if(usr!=null) {
                                    users.remove(usr);
                                }
                                BindUserList();
                            }
                            Toast.makeText(UserListActivity.this, R.string.success_msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                catch(Exception ex) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserListActivity.this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(UserListActivity.this);
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

    private User RetrieveUserById(List<User> users, String id)
    {
        if(users != null) {
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).id == id) {
                    return users.get(i);
                }
            }
        }
        return null;
    }
}
