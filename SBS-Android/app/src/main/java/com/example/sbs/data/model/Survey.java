package com.example.sbs.data.model;

import java.util.ArrayList;
import java.util.List;

public class Survey
{
    public String id;

    public String district;

    public String block;

    public String panchayat;

    public String created_by;

    public String created_location;

    public String latitude;

    public String longitude;

    public Beneficiary beneficiary;

    public List<QuestionResponse> questionnaries = new ArrayList<QuestionResponse>();
}
