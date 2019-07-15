package com.example.sbs.data;

import com.example.sbs.data.model.Block;
import com.example.sbs.data.model.LoggedInUser;
import com.example.sbs.data.model.Panchayat;
import com.example.sbs.data.model.Question;
import com.example.sbs.data.model.QuestionGroup;
import com.example.sbs.data.model.Role;
import com.example.sbs.data.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class LocalCache {
    public static List<Block> blocks;
    public static List<Panchayat> panchayats;
    public static List<QuestionGroup> questionGroups;
    public static Hashtable<Integer, String> possibleAnswers;
    public static List<Role> usertypes;
    public static LoggedInUser currentUser;
    public static int defaultDistrictId = 1;
    public static ArrayList<Question> questions;

    public static void ClearCache()
    {
        blocks = null;
        panchayats = null;
        questionGroups = null;
        possibleAnswers = null;
        usertypes = null;
        currentUser = null;
        questions = null;
    }
}
