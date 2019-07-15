package com.example.sbs.data.model;

import java.util.ArrayList;
import java.util.List;

public class Question
{
    public int id;
    public String text;
    public String possible_answers;
    public List<Integer> possible_answers_array = new ArrayList<>();
    public Integer headingId=null;
    public int index;
}
