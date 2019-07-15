package com.example.sbs.data.helpers;

import com.example.sbs.data.model.Block;
import com.example.sbs.data.model.Panchayat;
import com.example.sbs.data.model.Question;
import com.example.sbs.data.model.QuestionResponse;
import com.example.sbs.data.model.Role;
import com.example.sbs.data.model.Survey;

import java.util.HashMap;
import java.util.List;

public class Converter {

    public static HashMap<String, String> ConvertBlockListToHashMap(List<Block> list)
    {
        if(list == null)return  null;

        HashMap<String, String> objHashMap = new HashMap<>();

        for(Block b: list)
        {
            objHashMap.put(b.id, b.name);
        }
        return objHashMap;
    }

    public static HashMap<String, String> ConvertPanchayatListToHashMap(List<Panchayat> list)
    {
        if(list == null)return  null;

        HashMap<String, String> objHashMap = new HashMap<>();

        for(Panchayat b: list)
        {
            objHashMap.put(b.id, b.name);
        }
        return objHashMap;
    }

    public static HashMap<Integer, String> ConvertUsertypeListToHashMap(List<Role> list) {
        if(list == null)return  null;

        HashMap<Integer, String> objHashMap = new HashMap<>();

        for(Role obj: list)
        {
            objHashMap.put(obj.id, obj.name);
        }
        return objHashMap;
    }

    public static HashMap<String, Survey>  ConvertSurveyListToHashMap(List<Survey> list) {
        if(list == null)return  null;

        HashMap<String, Survey> objHashMap = new HashMap<>();

        for(Survey obj: list)
        {
            objHashMap.put(obj.id, obj);
        }
        return objHashMap;
    }

    public static HashMap<Integer, Integer> convertQuestionnaireListToHashMap(List<QuestionResponse> list) {
        if(list == null)return  null;

        HashMap<Integer, Integer> objHashMap = new HashMap<>();

        for(QuestionResponse obj: list)
        {
            objHashMap.put(obj.questionId, obj.answerId);
        }
        return objHashMap;
    }

    public static HashMap<Integer, String> convertQuestionListToHashMap(List<Question> list) {
        if(list == null)return  null;

        HashMap<Integer, String> objHashMap = new HashMap<>();

        for(Question obj: list)
        {
            objHashMap.put(obj.id, obj.text);
        }
        return objHashMap;
    }
}
