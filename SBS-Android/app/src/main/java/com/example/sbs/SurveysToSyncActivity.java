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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sbs.adapters.SurveySyncListAdapter;
import com.example.sbs.data.LocalCache;
import com.example.sbs.data.PanchayatRepositoryLocal;
import com.example.sbs.data.SurveyRepositoryLocal;
import com.example.sbs.data.SurveyRepositoryRemote;
import com.example.sbs.data.helpers.Converter;
import com.example.sbs.data.helpers.ErrorMessageHelper;
import com.example.sbs.data.model.Survey;
import com.example.sbs.data.model.User;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SurveysToSyncActivity extends AppCompatActivity implements IAsyncTaskInvoker{

    ProgressDialog progress = null;
    SurveyRepositoryLocal repoLocal;
    SurveyRepositoryRemote repoRemote;
    PanchayatRepositoryLocal repoPanchayatLocal;
    List<Survey> surveys;
    int requestedNoOfSync =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surveys_to_sync);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        repoPanchayatLocal = new PanchayatRepositoryLocal(this);
        repoLocal = new SurveyRepositoryLocal(this);
        repoRemote = new SurveyRepositoryRemote(this);

        surveys = repoLocal.getSurveysToSync();

        ListView lv = findViewById(R.id.listSurveys);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if(surveys != null && surveys.size() > 0)
                {
                    Intent intent = new Intent(getApplicationContext(),
                            SurveyActivity.class);
                    intent.putExtra("args", "{'source': 'sync_survey', 'surveyId':"+ surveys.get(position - 1).id +"}");
                    startActivityForResult(intent, 1);
                }
            }
        });

        bindSurveyList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            surveys = repoLocal.getSurveysToSync();
            bindSurveyList();
        }
    }


    private void bindSurveyList()
    {
        if(surveys == null || surveys.size() <= 0 )
        {
            findViewById(R.id.lblNoRec).setVisibility(View.VISIBLE);
        }
        else {

            HashMap<String, String> panchayatMap = Converter.ConvertPanchayatListToHashMap(repoPanchayatLocal.getAllPanchayats());
            SurveySyncListAdapter adapter = new SurveySyncListAdapter(this, surveys, panchayatMap);
            ListView list = (ListView) findViewById(R.id.listSurveys);
            list.setAdapter(adapter);
        }
    }

    public void SubmitSurveyClicked(View view)
    {
        requestedNoOfSync =0;
        if(surveys!=null && surveys.size()>0) {

            String surveyId = view.getTag().toString();
            for(Survey s : surveys) {
                if(s.id == surveyId) {
                    s.created_by= LocalCache.currentUser.getUserId();
                    requestedNoOfSync++;
                    List<Survey> surveyList = new ArrayList<>();
                    surveyList.add(s);
                    repoRemote.SubmitSurveys(surveyList);
                    break;
                }
            }
        }
    }

    public void UpdateSurveyClicked(final View view) {
        if(surveys != null && surveys.size() > 0)
        {
            Intent intent = new Intent(getApplicationContext(),
                    SurveyActivity.class);
            intent.putExtra("args", "{'source': 'sync_survey', 'surveyId':"+ view.getTag() +"}");
            startActivityForResult(intent, 1);
        }
    }

    public void DeleteSurveyClicked(final View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_draft).setMessage(R.string.sure_to_delete).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

        if(surveys!=null && surveys.size()>0) {

            String surveyId = view.getTag().toString();
            repoLocal.deleteSurvey(surveyId);
            surveys = repoLocal.getSurveysToSync();
            bindSurveyList();
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

                progress = new ProgressDialog(SurveysToSyncActivity.this);
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

                    JSONArray idsJson = null;
                    JSONObject obj = null;

                    if (result != null) {
                        idsJson = (JSONArray) new JSONTokener(result).nextValue();
                    }
                    else
                    {
                        throw new  Exception("error");
                    }

                    if (idsJson != null && idsJson.length() == 1) {
                        obj = idsJson.getJSONObject(0);
                    }
                    if (obj != null && obj.has("error")) {
                        String error = obj.getString("error");
                        AlertDialog.Builder builder = new AlertDialog.Builder(SurveysToSyncActivity.this);
                        builder.setTitle("Error").setMessage(error).setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Do something

                            }
                        });
                        builder.show();
                    } else {
                        if(idsJson.length() == 0)
                        {
                            throw new Exception("error");
                        }
                        if(idsJson.length() < requestedNoOfSync)
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SurveysToSyncActivity.this);
                            builder.setTitle("Error").setMessage(R.string.failed_save_all).setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // Do something

                                }
                            });
                            builder.show();
                        }
                         else {
                            Toast.makeText(SurveysToSyncActivity.this, R.string.success_msg, Toast.LENGTH_SHORT).show();
                        }
                        removeSyncdSurveys(idsJson);
                    }
                }
                catch(Exception ex) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SurveysToSyncActivity.this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(SurveysToSyncActivity.this);
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

    private void removeSyncdSurveys(JSONArray idsJson) {
        try {
            List<Survey> list = new ArrayList<>();
            if(surveys != null && surveys.size() >0) {
                HashMap<String, Survey> map = Converter.ConvertSurveyListToHashMap(surveys);
                for (int i = 0; i < idsJson.length(); i++) {
                    String id = idsJson.get(i).toString();

                    if(map.containsKey(id))
                    {
                        list.add(map.get(id));
                    }
                }
                surveys = list;
                bindSurveyList();
            }
        }
        catch(Exception ex)
        {

        }
    }

}
