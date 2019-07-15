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
import android.text.TextUtils;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sbs.data.BlockRepositoryLocal;
import com.example.sbs.data.LocalCache;
import com.example.sbs.data.PanchayatRepositoryLocal;
import com.example.sbs.data.SurveyRepositoryLocal;
import com.example.sbs.data.SurveyRepositoryRemote;
import com.example.sbs.data.helpers.Converter;
import com.example.sbs.data.helpers.ErrorMessageHelper;
import com.example.sbs.data.model.Beneficiary;
import com.example.sbs.data.model.Block;
import com.example.sbs.data.model.Panchayat;
import com.example.sbs.data.model.Question;
import com.example.sbs.data.model.QuestionGroup;
import com.example.sbs.data.model.QuestionResponse;
import com.example.sbs.data.model.Role;
import com.example.sbs.data.model.Survey;
import com.example.sbs.data.model.User;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class SurveyActivity extends AppCompatActivity implements IAsyncTaskInvoker{

    ProgressDialog progress = null;
    SurveyRepositoryLocal repo;
    SurveyRepositoryRemote repoSurveyRemote;
    BlockRepositoryLocal repoBlockLocal;
    PanchayatRepositoryLocal repoPanchayatLocal;

    List<Block> blocks = new ArrayList<Block>();
    List<Panchayat> panchayats = new ArrayList<Panchayat>();
    HashMap<Integer, Integer> questionAnswers = new HashMap<>();
    List<QuestionGroup> questionGroups;
    int surveyId= 0;
    int selectedPanchayatId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        repo = new SurveyRepositoryLocal(this);
        repoSurveyRemote = new SurveyRepositoryRemote(this);
        repoBlockLocal = new BlockRepositoryLocal(this);
        repoPanchayatLocal = new PanchayatRepositoryLocal(this);

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
                    InitializeEmptyPanchayatSpinner();
                } else {
                    Block selecteditem = blocks.get(position - 1);
                    //Toast.makeText(getApplicationContext(), selecteditem.service_id, Toast.LENGTH_SHORT).show();
                    PopulatePanchayats(selecteditem.id);
                    if(selectedPanchayatId > -1)
                    {
                        ((Spinner)findViewById(R.id.panchayatSpinner)).setSelection(selectedPanchayatId + 1);
                        selectedPanchayatId = -1;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        //InitializeEmptyPanchayatSpinner();
        RenderAllQuestions();

        String args = this.getIntent().getStringExtra("args");

        String source="";
        surveyId= 0;
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

        if(source.equals("sync_survey") && surveyId > 0)
        {
            Survey s = repo.getSurvey(surveyId);
            if(s != null) {
                //PopulatePanchayats(s.block);
                selectedPanchayatId = RetrievePanchayatIndexById(repoPanchayatLocal.getPanchayats(s.block), s.panchayat);
                populateControlsFromObject(s);
            }
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

    private List<String> GetPanchayatNames() {
        List<String> pNames = new ArrayList<String>();
        pNames.add("Select Panchayat");
        for (int i = 0; i < panchayats.size(); i++) {
            pNames.add(panchayats.get(i).name);
        }
        return pNames;
    }

    private void InitializeEmptyPanchayatSpinner()
    {
        List<String> pNames = new ArrayList<String>();
        pNames.add("Select Panchayat");
        ArrayAdapter<String> pAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pNames);

        Spinner pSpinner = (Spinner) findViewById(R.id.panchayatSpinner);
        pSpinner.setAdapter(pAdapter);
    }
    private void PopulatePanchayats(String blockId)
    {
        panchayats = repoPanchayatLocal.getPanchayats(blockId);
        List<String> pNames = GetPanchayatNames();
        Spinner pSpinner = (Spinner) findViewById(R.id.panchayatSpinner);
        ArrayAdapter<String> pAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pNames);

        pSpinner.setAdapter(pAdapter);
    }

    Hashtable<Integer, String> answers;
    private void RenderAllQuestions()
    {
        answers = repo.getPossibleAnswers();
        questionGroups = repo.getQuestions();
        LinearLayout ll = (LinearLayout) findViewById(R.id.pnlQuestions);

        for ( int i=0; i< questionGroups.size(); i++  )
        {
            QuestionGroup qg = questionGroups.get(i);
            if(qg.text != null && qg.text.trim() != "")
            {
                TextView headingText = new TextView(this);
                headingText.setText(qg.text);
                ll.addView(headingText);

            }
            for ( int j=0; j< qg.questions.size(); j++  )
            {
                Question q = qg.questions.get(j);
                // Add to the questionAnswer array for further use
                questionAnswers.put(q.id, 0);

                TextView questionText = new TextView(this);
                questionText.setPadding(0,20,0,2);
                questionText.setText(q.text);
                ll.addView(questionText);

                LinearLayout llAnswers = new LinearLayout(this);
                llAnswers.setOrientation(LinearLayout.HORIZONTAL);
                ll.addView(llAnswers);

                /*List<Integer> possible_answers = new ArrayList<>();
                for (String id: q.possible_answers.split(","))
                {
                    possible_answers.add(Integer.parseInt(id));
                }*/

                for ( int k=0; k< q.possible_answers_array.size(); k++  )
                {
                    CheckBox chk = new CheckBox(this);
                    chk.setText(answers.get(q.possible_answers_array.get(k)));
                    chk.setId(q.id*10 + q.possible_answers_array.get(k));

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    // left, top, right, bottom
                    params.setMargins(5, 2 , 40, 2);
                    params.gravity = Gravity.NO_GRAVITY;
                    chk.setLayoutParams(params);

                    Pair<Integer, Integer> p = new Pair(q.id, q.possible_answers_array.get(k));
                    chk.setTag(p);

                    chk.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            onCheckboxClicked(v);
                        }
                    });
                    /*chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            Pair<Integer, Integer>  p1 = (Pair<Integer, Integer>)buttonView.getTag();
                            if(isChecked) {
                                questionAnswers.put(p1.first, p1.second);
                            }
                            else
                            {
                                questionAnswers.put(p1.first, 0);
                            }
                        }
                    });*/

                    llAnswers.addView(chk);
                }
            }
        }


    }

    private void onCheckboxClicked(View view) {
        Pair<Integer, Integer>  p1 = (Pair<Integer, Integer>)view.getTag();
        if(((CheckBox)view).isChecked()) {
            questionAnswers.put(p1.first, p1.second);
            LinearLayout parent = (LinearLayout) view.getParent();

            int childCount = parent.getChildCount();

            for(int i=0; i< childCount; i++)
            {
                if(parent.getChildAt(i) instanceof CheckBox)
                {
                    CheckBox chk = (CheckBox)parent.getChildAt(i);
                    if(chk.getId() != p1.first*10 + p1.second)
                    {
                        chk.setChecked(false);
                    }
                }
            }
        }
        else
        {
            questionAnswers.put(p1.first, 0);
        }
    }

    public void onSaveAsDraftClick(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.save_as_draft).setMessage(R.string.sure_to_save).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Survey obj = populateObjectFromUIControls();
                Gson gson = new Gson();
                String s = gson.toJson(obj);
                if(surveyId > 0)
                {
                    repo.updateDraftSurvey(surveyId, s);
                    setResult(RESULT_OK);
                    SurveyActivity.this.finish();
                }
                else {
                    repo.SaveSurveyAsDraft(s);
                }
                ShowSuccessMessage();
                ResetAllFields();
            }
        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Do Nothing
            }
        });

        builder.show();
    }

    private void ResetAllFields() {
        ((EditText) findViewById(R.id.txtName)).setText("");
        ((EditText) findViewById(R.id.txtFatherName)).setText("");
        ((EditText) findViewById(R.id.txtAdhar)).setText("");
        ((EditText) findViewById(R.id.txtFamilyCount)).setText("");

        for(QuestionGroup qg : questionGroups)
        {
            for(Question q: qg.questions)
            {
                for(Integer pa: q.possible_answers_array) {
                    CheckBox chk = (CheckBox) findViewById(q.id * 10 + pa);
                    if(chk != null)
                    {
                        chk.setChecked(false);
                    }
                }
            }
        }
    }

    private void ShowSuccessMessage() {
        String success = getString(R.string.survey_draft_saved);
        Toast.makeText(getApplicationContext(), success, Toast.LENGTH_LONG).show();
    }

    public void onSubmitClick(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.save_as_draft).setMessage(R.string.sure_to_save).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Survey obj = populateObjectFromUIControls();

                obj.created_by = LocalCache.currentUser.getUserId();
                repoSurveyRemote.SubmitSurvey(obj);

            }
        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Do Nothing
            }
        });

        builder.show();


    }

    private Survey populateObjectFromUIControls()
    {
        Survey obj = new Survey();
        obj.beneficiary = new Beneficiary();
        obj.beneficiary.name = ((EditText)findViewById(R.id.txtName)).getText().toString();
        obj.beneficiary.adhar = ((EditText)findViewById(R.id.txtAdhar)).getText().toString();
        obj.beneficiary.fatherOrHusbandName = ((EditText)findViewById(R.id.txtFatherName)).getText().toString();
        obj.beneficiary.memberCount = Integer.parseInt(((EditText)findViewById(R.id.txtFamilyCount)).getText().toString());
        obj.block = blocks.get(((Spinner)findViewById(R.id.blockSpinner)).getSelectedItemPosition() - 1).id;
        obj.panchayat = panchayats.get(((Spinner)findViewById(R.id.panchayatSpinner)).getSelectedItemPosition() - 1).id ;

        obj.questionnaries = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : questionAnswers.entrySet()) {
            QuestionResponse qr = new QuestionResponse();
            qr.questionId = entry.getKey();
            qr.answerId = entry.getValue();
            obj.questionnaries.add(qr);
        }
        return obj;
    }

    private void populateControlsFromObject(Survey obj) {

        ((TextView)findViewById(R.id.txtName)).setText(obj.beneficiary.name);
        ((TextView)findViewById(R.id.txtAdhar)).setText(obj.beneficiary.adhar);
        ((TextView)findViewById(R.id.txtFatherName)).setText(obj.beneficiary.fatherOrHusbandName);
        ((TextView)findViewById(R.id.txtFamilyCount)).setText(obj.beneficiary.memberCount +"");
        ((Spinner)findViewById(R.id.blockSpinner)).setSelection(RetrieveBlockIndexById(blocks, obj.block)+1);
        //((Spinner)findViewById(R.id.panchayatSpinner)).setSelection(RetrievePanchayatIndexById(panchayats, obj.panchayat)+1);

        findViewById(R.id.btnSubmit).setVisibility(View.GONE);
        //findViewById(R.id.btnSaveDraft).setVisibility(View.GONE);

        HashMap<Integer,Integer> questionnaire = Converter.convertQuestionnaireListToHashMap(obj.questionnaries);
        for(QuestionGroup qg : questionGroups)
        {
            for(Question q: qg.questions)
            {
                for(Integer pa: q.possible_answers_array) {
                    CheckBox chk = (CheckBox) findViewById(q.id * 10 + pa);
                    if(chk != null)
                    {
                        Pair<Integer, Integer>  p1 = (Pair<Integer, Integer>)chk.getTag();
                        if(p1.second == questionnaire.get(q.id))
                        {
                            chk.setChecked(true);
                        }
                    }
                }
            }
        }
    }

    private Integer RetrieveBlockIndexById(List<Block> list, String id)
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

    private Integer RetrievePanchayatIndexById(List<Panchayat> list, String id)
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

    public void OnBeforeExecute(final String httpAction) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                progress = new ProgressDialog(SurveyActivity.this);
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                if(httpAction.equals("get"))
                {
                    progress.setMessage(getString(R.string.fetching_data));
                }
                else {
                    progress.setMessage(getString(R.string.processing_request));
                }
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
                        ShowSuccessMessage();
                        ResetAllFields();
                }
                catch(Exception ex)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SurveyActivity.this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(SurveyActivity.this);
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
