package com.example.sbs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.ListView;

import com.example.sbs.adapters.ReportListAdapter;
import com.example.sbs.adapters.UserListAdapter;
import com.example.sbs.data.LocalCache;
import com.example.sbs.data.QuestionRepositoryLocal;
import com.example.sbs.data.ReportRepositoryRemote;
import com.example.sbs.data.helpers.Converter;
import com.example.sbs.data.helpers.ErrorMessageHelper;
import com.example.sbs.data.model.Question;
import com.example.sbs.data.model.QuestionGroup;
import com.example.sbs.data.model.QuestionResponse;
import com.example.sbs.data.model.ResponsePercentage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONTokener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class ReportByDistrictActivity extends AppCompatActivity implements IAsyncTaskInvoker {

    ProgressDialog progress = null;
    ReportRepositoryRemote repo;
    QuestionRepositoryLocal repoQuestionLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_by_district);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        repoQuestionLocal = new QuestionRepositoryLocal(this);
        repo = new ReportRepositoryRemote(this);
        repo.getReportByDistrictAsync(LocalCache.defaultDistrictId);
    }

    private void RenderReport(List<ResponsePercentage> resultList)
    {
        if (resultList != null || resultList.size() > 0) {

            List<Question> questions = repoQuestionLocal.getQuestionsWithoutGrouping();
            HashMap<Integer, String> questionMap = Converter.convertQuestionListToHashMap(questions);
            ReportListAdapter adapter = new ReportListAdapter(this, resultList, questionMap);
            ListView list = (ListView) findViewById(R.id.listReport);
            list.setAdapter(adapter);
        }
    }

    @Override
    public void OnBeforeExecute(final String httpAction) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                progress = new ProgressDialog(ReportByDistrictActivity.this);
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setMessage(getString(R.string.fetching_data));
                progress.setIndeterminate(true);
                progress.show();
            }
        });
    }

    @Override
    public void OnAfterExecute(final String result, final String httpAction) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.dismiss();
                try {
                    if (result != null) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<ResponsePercentage>>(){}.getType();
                        // In this test code i just shove the JSON here as string.
                        List<ResponsePercentage> resultList = gson.fromJson(result, listType);

                        RenderReport(resultList);
                    }
                }
                catch(Exception ex)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ReportByDistrictActivity.this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(ReportByDistrictActivity.this);
        builder.setTitle("Error").setMessage(ErrorMessageHelper.GetMessage(errorCode, this)).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Do something

            }
        });
        builder.show();
    }

    @Override
    public Context getContext() {
        return this;
    }
}
