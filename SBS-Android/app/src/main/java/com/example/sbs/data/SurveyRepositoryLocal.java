package com.example.sbs.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.JsonReader;

import com.example.sbs.data.model.Block;
import com.example.sbs.data.model.Panchayat;
import com.example.sbs.data.model.PossibleAnswers;
import com.example.sbs.data.model.Question;
import com.example.sbs.data.model.QuestionGroup;
import com.example.sbs.data.model.Survey;
import com.example.sbs.data.model.SurveyBasicDetail;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import static java.lang.System.in;


public class SurveyRepositoryLocal {


    DataBaseHelper dbAccess;
    public SurveyRepositoryLocal(Context context)
    {
        dbAccess = new DataBaseHelper(context);
    }
    /**
     * Read all quotes from the database.
     *
     * @return a List of quotes
     */
    private SQLiteDatabase database;

    public List<QuestionGroup> getQuestions()
    {
        if(LocalCache.questionGroups == null) {
            List<QuestionGroup> questionGroups = new ArrayList<QuestionGroup>();
            questionGroups.add(new QuestionGroup());
            ArrayList<Question> questions = new ArrayList<Question>();
            Dictionary<Integer, String> groups = new Hashtable<Integer, String>();

            dbAccess.openDataBase();
            Cursor cursor = dbAccess.executeCursor("select * from headings;");

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                groups.put(cursor.getInt(0), cursor.getString(1));
                cursor.moveToNext();
            }
            cursor.close();



            //cursor = dbAccess.executeCursor("select q.id, q.text, q.possible_answers, h.id heading_id from question q left outer join headings h on h.id = q.heading_id order by q.[index] asc");
            cursor = dbAccess.executeCursor("select id, text, possible_answers, heading_id from question order by `index` asc");

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Question obj = new Question();
                obj.id = cursor.getInt(0);
                obj.text = cursor.getString(1);
                obj.possible_answers = cursor.getString(2);
                if (obj.possible_answers != null) {
                    obj.possible_answers_array = new ArrayList<Integer>();
                    String[] answers = cursor.getString(2).split(",");
                    for (int i = 0; i < answers.length; i++) {
                        obj.possible_answers_array.add(Integer.parseInt(answers[i]));
                    }
                }
                if (cursor.getString(3) == null || cursor.getString(3).equals("null") || TextUtils.isEmpty(cursor.getString(3)))
                    obj.headingId = null;
                else
                    obj.headingId = Integer.parseInt(cursor.getString(3));

                questions.add(obj);

                cursor.moveToNext();
            }
            cursor.close();
            dbAccess.close();

            if(questions.size() > 0) {
                Integer lastHeading = questions.get(0).headingId;
                if (questions.get(0).headingId != null) {
                    questionGroups.get(0).text = groups.get(questions.get(0).headingId);
                }
                for (int i = 0; i < questions.size(); i++) {
                    if (questions.get(i).headingId != lastHeading) {
                        lastHeading = questions.get(i).headingId;
                        if (questions.get(i).headingId != null) {
                            QuestionGroup qg = new QuestionGroup();
                            qg.text = groups.get(questions.get(i).headingId);
                            questionGroups.add(qg);
                        } else
                            questionGroups.add(new QuestionGroup());
                    }

                    questionGroups.get(questionGroups.size() - 1).questions.add(questions.get(i));
                }

                LocalCache.questionGroups = questionGroups;
            }
        }
        return LocalCache.questionGroups;
    }

    public Hashtable<Integer, String> getPossibleAnswers()
    {
        if(LocalCache.possibleAnswers == null) {
            Hashtable<Integer, String> list = new Hashtable<Integer, String>();

            dbAccess.openDataBase();
            Cursor cursor = dbAccess.executeCursor("select * from possible_answers;");

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                PossibleAnswers p = new PossibleAnswers();
                list.put(cursor.getInt(0), cursor.getString(1));
                cursor.moveToNext();
            }
            cursor.close();
            dbAccess.close();
            LocalCache.possibleAnswers = list;
        }
         return LocalCache.possibleAnswers;
    }

    public void SaveSurveyAsDraft(String surveyJsonObj) {
        dbAccess.openWritableDataBase();
        int id = 1;
        Cursor cursor = dbAccess.executeCursor("select max(id) from surveyrecordstosync");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            id = cursor.getInt(0);
            cursor.moveToNext();
        }
        cursor.close();

        ContentValues values =new ContentValues();
        values.put("id", id+1);
        values.put("record", surveyJsonObj);
        values.put("is_syncd", 0);

        dbAccess.insertData("surveyrecordstosync", values);

        dbAccess.close();
    }

    public List<Survey> getSurveysToSync() {

        List<Survey>  surveys = new ArrayList<>();
        Gson gson = new Gson();

        dbAccess.openDataBase();
        Cursor cursor = dbAccess.executeCursor("select id, record from surveyrecordstosync where is_syncd=0");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Survey s = gson.fromJson(cursor.getString(1), Survey.class);

            s.id = cursor.getString(0);

            surveys.add(s);
            cursor.moveToNext();
        }
        cursor.close();

        return surveys;
    }

    public void deleteSurvey(String surveyId) {
        dbAccess.openWritableDataBase();
        dbAccess.executeNonQuery("delete from surveyrecordstosync where id="+ surveyId);
        dbAccess.close();
    }

    public Survey getSurvey(int surveyId) {
        Survey  survey = null;
        Gson gson = new Gson();

        dbAccess.openDataBase();
        Cursor cursor = dbAccess.executeCursor("select id, record from surveyrecordstosync where is_syncd=0 and id="+surveyId);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            survey = gson.fromJson(cursor.getString(1), Survey.class);

            survey.id = cursor.getString(0);

            cursor.moveToNext();
            break;
        }
        cursor.close();

        return survey;
    }

    public void updateDraftSurvey(int surveyId, String data) {
        dbAccess.openWritableDataBase();
        dbAccess.executeNonQuery("update surveyrecordstosync set record='"+data+"' where id="+ surveyId);
        dbAccess.close();
    }
}
