package com.example.sbs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sbs.data.UserRepositoryLocal;
import com.example.sbs.data.UserRepositoryRemote;
import com.example.sbs.data.helpers.ErrorMessageHelper;
import com.example.sbs.data.model.Permission;
import com.example.sbs.data.model.Role;
import com.example.sbs.data.model.User;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class AddUserActivity extends AppCompatActivity implements IAsyncTaskInvoker {

    ProgressDialog progress = null;
    UserRepositoryLocal repoLocal;
    UserRepositoryRemote repoRemote;
    List<Role> usertypes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        repoLocal = new UserRepositoryLocal(this);

        usertypes = repoLocal.getUserTypes();


        List<String> list = new ArrayList<String>();
        for(Role obj: usertypes)
        {
            list.add(obj.name);
        }
        list.add("Admin");
        // Creating adapter for spinner
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);

        Spinner usertypeSpinner = (Spinner) findViewById(R.id.usertypeSpinner);
        usertypeSpinner.setAdapter(spinAdapter);

        String userJson = getIntent().getStringExtra("User");
        if(userJson != null)
        {
            Gson gson = new Gson();
            User user = gson.fromJson(userJson, User.class);
            if(user != null)
            {
                PopulateUserDetail(user);
            }
            ((EditText)findViewById(R.id.txtUsername)).setEnabled(false);
        }

    }

    private void PopulateUserDetail(User user) {
        ((EditText)findViewById(R.id.txtId)).setText(user.id);
        ((EditText)findViewById(R.id.txtName)).setText(user.name);
        ((EditText)findViewById(R.id.txtPhone)).setText(user.phone);
        ((EditText)findViewById(R.id.txtUsername)).setText(user.userid);
        ((EditText)findViewById(R.id.txtPassword)).setText(user.password);
        ((Spinner) findViewById(R.id.usertypeSpinner)).setSelection(RetrieveIndexById(usertypes, user.roles.get(0).id));
    }

    private User GetUserFromControls()
    {
        User  u = new User();

        u.id = ((EditText)findViewById(R.id.txtId)).getText().toString();
        u.name = ((EditText)findViewById(R.id.txtName)).getText().toString();
        u.phone = ((EditText)findViewById(R.id.txtPhone)).getText().toString();
        u.userid = ((EditText)findViewById(R.id.txtUsername)).getText().toString();
        u.password = ((EditText)findViewById(R.id.txtPassword)).getText().toString();
        u.roles = new ArrayList<>();
        Role r = new Role();
        r.permissions = new ArrayList<>();
        Permission p = new Permission();
        p.id=1;

        u.roles.add(usertypes.get(((Spinner) findViewById(R.id.usertypeSpinner)).getSelectedItemPosition()));

        return u;
    }
    public void SaveUserClicked(View view)
    {
        User u = GetUserFromControls();
        UserRepositoryRemote repoRemote = new UserRepositoryRemote(this);
        if(u.id != null && u.id.trim() != "")
        {
            repoRemote.updateUserAsync(u);
        }
        else {
            repoRemote.saveUserAsync(u);
        }
    }

    private Integer RetrieveIndexById(List<Role> usertypes, Integer id)
    {
        if(usertypes != null) {
            for (int i = 0; i < usertypes.size(); i++) {
                if (usertypes.get(i).id == id) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void OnBeforeExecute(final String httpAction) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                progress = new ProgressDialog(AddUserActivity.this);
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
                    if (result != null) {
                        obj = new JSONObject(result);
                    }

                    if (httpAction == "post") {
                        if (obj == null || obj.toString().equals("0")) {
                            throw new Exception("error");
                        }
                    }
                    Toast.makeText(AddUserActivity.this, R.string.success_msg, Toast.LENGTH_SHORT).show();
                    User u = GetUserFromControls();
                    u.id = obj.toString();

                    Intent data = new Intent();
                    Gson g = new Gson();
                    data.setData(Uri.parse(g.toJson(u)));
                    setResult(RESULT_OK, data);
                    AddUserActivity.this.finish();

                }
                catch(Exception ex)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddUserActivity.this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(AddUserActivity.this);
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
