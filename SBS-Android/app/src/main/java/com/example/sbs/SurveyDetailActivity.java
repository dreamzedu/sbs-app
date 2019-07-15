package com.example.sbs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sbs.data.BlockRepositoryLocal;
import com.example.sbs.data.PanchayatRepositoryLocal;
import com.example.sbs.data.SurveyRepositoryLocal;
import com.example.sbs.data.SurveyRepositoryRemote;
import com.example.sbs.data.helpers.Converter;
import com.example.sbs.data.helpers.ErrorMessageHelper;
import com.example.sbs.data.model.Block;
import com.example.sbs.data.model.Panchayat;
import com.example.sbs.data.model.Question;
import com.example.sbs.data.model.QuestionGroup;
import com.example.sbs.data.model.QuestionResponse;
import com.example.sbs.data.model.Survey;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class SurveyDetailActivity extends AppCompatActivity implements IAsyncTaskInvoker{

    ProgressDialog progress = null;
    SurveyRepositoryLocal repo;
    SurveyRepositoryRemote repoSurveyRemote;

    HashMap<Integer, Integer> questionAnswers = new HashMap<>();
    List<QuestionGroup> questionGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        repo = new SurveyRepositoryLocal(this);
        repoSurveyRemote = new SurveyRepositoryRemote(this);

        String args = this.getIntent().getStringExtra("args");

        String source="";
        int surveyId= 0;
        if(args != null)
        {
            try {
                JSONObject obj = new JSONObject(args);
                source = obj.getString("source");
                surveyId = obj.getInt("surveyId");
            }
            catch(Exception ex)
            {

            }
        }

        if(source.equals("manage_survey") && surveyId > 0)
        {
            repoSurveyRemote.getSurvey(surveyId);
        }
    }


    private void populateControlsFromObject(Survey obj) {
        ((TextView)findViewById(R.id.lblName_sd)).setText(obj.beneficiary.name);
        ((TextView)findViewById(R.id.lblAdhar_sd)).setText(obj.beneficiary.adhar);
        ((TextView)findViewById(R.id.lblFather_sd)).setText(obj.beneficiary.fatherOrHusbandName);
        ((TextView)findViewById(R.id.lblCount_sd)).setText(obj.beneficiary.memberCount+"");
        ((TextView)findViewById(R.id.lblBlock_sd)).setText(obj.block);
        ((TextView)findViewById(R.id.lblPanchayat_sd)).setText(obj.panchayat);

        HashMap<Integer,Integer> questionnaire = Converter.convertQuestionnaireListToHashMap(obj.questionnaries);
        /*for(QuestionGroup qg : questionGroups)
        {
            for(Question q: qg.questions)
            {
                for(Integer pa: q.possible_answers_array) {
                    CheckBox chk = (CheckBox) findViewById(q.id * 10 + pa);
                    if(chk != null)
                    {
                        Pair<Integer, Integer> p1 = (Pair<Integer, Integer>)chk.getTag();
                        if(p1.second == questionnaire.get(q.id))
                        {
                            chk.setChecked(true);
                        }
                        chk.setEnabled(false);
                    }
                }
            }
        }*/
        RenderAllQuestions(questionnaire);
    }

    Hashtable<Integer, String> answers;
    private void RenderAllQuestions(HashMap<Integer,Integer> questionnaire) {
        answers = repo.getPossibleAnswers();
        questionGroups = repo.getQuestions();
        LinearLayout ll = (LinearLayout) findViewById(R.id.pnlQuestions);

        for (int i = 0; i < questionGroups.size(); i++) {
            QuestionGroup qg = questionGroups.get(i);
            if (qg.text != null && qg.text.trim() != "") {
                TextView headingText = new TextView(this);
                headingText.setText(qg.text);
                ll.addView(headingText);

            }
            for (int j = 0; j < qg.questions.size(); j++) {
                Question q = qg.questions.get(j);

                TextView questionText = new TextView(this);
                questionText.setPadding(0, 20, 0, 2);
                questionText.setText(q.text);
                ll.addView(questionText);

                TextView ansView = new TextView(this);
                if(questionnaire.containsKey(q.id) && answers.containsKey(questionnaire.get(q.id))) {
                    ansView.setText(answers.get(questionnaire.get(q.id)));
                }
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                // left, top, right, bottom
                params.setMargins(5, 2, 40, 2);
                params.gravity = Gravity.NO_GRAVITY;
                ansView.setLayoutParams(params);

                ll.addView(ansView);

            }
        }
    }

    public void OnBeforeExecute(final String httpAction) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                progress = new ProgressDialog(SurveyDetailActivity.this);
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

                        Gson g = new Gson();
                        Survey obj = null;
                        if(result != null) {
                            obj = g.fromJson(result, Survey.class);
                        }

                        if(obj != null)
                        {
                            populateControlsFromObject(obj);
                        }
                }
                catch(Exception ex)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SurveyDetailActivity.this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(SurveyDetailActivity.this);
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

