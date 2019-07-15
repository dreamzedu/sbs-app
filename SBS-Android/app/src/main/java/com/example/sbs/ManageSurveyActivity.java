package com.example.sbs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sbs.adapters.SurveyListAdapter;
import com.example.sbs.data.SurveyRepositoryRemote;
import com.example.sbs.data.helpers.ErrorMessageHelper;
import com.example.sbs.data.model.SurveyBasicDetail;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class ManageSurveyActivity extends AppCompatActivity implements IAsyncTaskInvoker{

    SurveyRepositoryRemote repoSurveyRemote;
    ProgressDialog progress = null;
    int startIndex = 1;
    int fetch_count = 5;
    List<SurveyBasicDetail> surveys;
    private TextView mTextMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_survey);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);

        ListView lv = findViewById(R.id.listSurveys);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if(surveys != null && surveys.size() > 0)
                {
                    Intent intent = new Intent(getApplicationContext(),
                            SurveyDetailActivity.class);
                    intent.putExtra("args", "{'source': 'manage_survey', 'surveyId':"+ surveys.get(position - 1).id +"}");
                    startActivity(intent);
                }
            }
        });

        repoSurveyRemote = new SurveyRepositoryRemote(this);

        surveys = new ArrayList<>();
        // fetch initial records
        repoSurveyRemote.getSurveysAsync(startIndex, fetch_count);

    }

    private void BindSurveyList()
    {
        if (surveys == null || surveys.size() <= 0) {
            findViewById(R.id.lblNoRec).setVisibility(View.VISIBLE);
        } else {
            SurveyListAdapter adapter = new SurveyListAdapter(this, surveys);
            ListView list = (ListView) findViewById(R.id.listSurveys);
            list.setAdapter(adapter);
        }
    }

    private void DisableViewMoreButton()
    {
        findViewById(R.id.btnViewMore).setEnabled(false);
    }

    public void ViewMoreClicked(View view)
    {
        startIndex = startIndex + fetch_count;
        repoSurveyRemote.getSurveysAsync(startIndex, fetch_count);
    }

    @Override
    public void OnBeforeExecute(String httpAction) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                progress = new ProgressDialog(ManageSurveyActivity.this);
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setMessage(getString(R.string.fetching_data));
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

                    JSONArray surveysJson = null;
                    JSONObject obj = null;

                    if (result != null && !result.equals("")) {
                        surveysJson = (JSONArray) new JSONTokener(result).nextValue();
                    }

                    if(surveysJson == null || surveysJson.length() < fetch_count)
                    {
                        DisableViewMoreButton();
                    }
                    if (httpAction.toLowerCase() == "get") {
                        //JSONArray object = (JSONArray) new JSONTokener(result).nextValue();

                        if (surveysJson != null) {
                            for (int i = 0; i < surveysJson.length(); i++) {
                                JSONObject actor = surveysJson.getJSONObject(i);

                                Gson gson = new Gson();
                                SurveyBasicDetail u = gson.fromJson(actor.toString(), SurveyBasicDetail.class);
                                surveys.add(u);
                            }
                        }
                        BindSurveyList();
                    } else if (httpAction.toLowerCase() == "delete") {
                            /*if (surveys != null && surveys.size() > 0) {
                                User usr= RetrieveUserById(surveys, delUserId);
                                if(usr!=null) {
                                    surveys.remove(usr);
                                }
                                BindSurveyList();
                            }
                            Toast.makeText(SurveyListActivity.this, R.string.success_msg, Toast.LENGTH_SHORT).show();
                            */
                    }
                }
                catch(Exception ex) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ManageSurveyActivity.this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(ManageSurveyActivity.this);
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

