package com.example.sbs.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.example.sbs.data.DataBaseHelper;
import com.example.sbs.data.LocalCache;
import com.example.sbs.data.model.Question;
import com.example.sbs.data.model.QuestionGroup;
import com.example.sbs.data.model.Role;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QuestionRepositoryLocal {

    DataBaseHelper dbAccess;
    public QuestionRepositoryLocal(Context context)
    {
        dbAccess = new DataBaseHelper(context);
    }

    public List<Question> getQuestionsWithoutGrouping()
    {
        if(LocalCache.questions == null) {
            ArrayList<Question> questions = new ArrayList<Question>();

            dbAccess.openDataBase();
            Cursor cursor = dbAccess.executeCursor("select id, text from question order by `index` asc");

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Question obj = new Question();
                obj.id = cursor.getInt(0);
                obj.text = cursor.getString(1);
                questions.add(obj);

                cursor.moveToNext();
            }
            cursor.close();
            dbAccess.close();

            LocalCache.questions = questions;

        }
        return LocalCache.questions;
    }

    public void resetHeadingsData(JSONArray headings) throws IOException {
        try {
            dbAccess.openWritableDataBase();
            dbAccess.executeNonQuery("delete from headings");
            for (int i = 0; i < headings.length(); i++) {
                JSONObject u = headings.getJSONObject(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put("id", u.getString("id"));
                contentValues.put("text", u.getString("text"));

                dbAccess.insertData("headings", contentValues);
            }
        } catch (Exception ex) {
            throw new IOException(ex);
        }
        finally {
            dbAccess.close();
        }
    }

    public void resetQuestionData(JSONArray questions) throws IOException {
        try {
            dbAccess.openWritableDataBase();
            dbAccess.executeNonQuery("delete from question");
            for (int i = 0; i < questions.length(); i++) {
                JSONObject u = questions.getJSONObject(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put("id", u.getString("id"));
                contentValues.put("text", u.getString("text"));
                contentValues.put("isactive", u.getString("isactive"));
                contentValues.put("possible_answers", u.getString("possible_answers"));
                contentValues.put("heading_id", u.getString("headingId"));
                contentValues.put("`index`", u.getString("index"));


                dbAccess.insertData("question", contentValues);
            }

        } catch (Exception ex) {
            throw new IOException(ex);
        }
        finally {
            dbAccess.close();
        }
    }

    public void resetAnswersData(JSONArray answers) throws IOException {
        try {
            dbAccess.openWritableDataBase();
            dbAccess.executeNonQuery("delete from possible_answers");
            for (int i = 0; i < answers.length(); i++) {
                JSONObject u = answers.getJSONObject(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put("id", u.getString("id"));
                contentValues.put("text", u.getString("text"));

                dbAccess.insertData("possible_answers", contentValues);
            }
        } catch (Exception ex) {
            throw new IOException(ex);
        }
        finally {
            dbAccess.close();
        }
    }

}
