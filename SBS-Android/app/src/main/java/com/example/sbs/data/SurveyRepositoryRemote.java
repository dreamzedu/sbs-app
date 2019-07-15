package com.example.sbs.data;

import com.example.sbs.IAsyncTaskInvoker;
import com.example.sbs.data.model.Survey;
import com.example.sbs.network.SbsConstants;
import com.example.sbs.network.WebApiInvoker;

import java.util.List;


public class SurveyRepositoryRemote {

    IAsyncTaskInvoker invoker;

    public SurveyRepositoryRemote(IAsyncTaskInvoker context)
    {
        invoker = context;
    }

    public void SubmitSurveys(List<Survey> surveys) {
        WebApiInvoker<List<Survey>> client = new WebApiInvoker<>();
        client.invoker = invoker;
        client.httpAction = "post";
        client.objToPost = surveys;
        client.execute(SbsConstants.baseUrl + "/survey/list");
    }

    public void SubmitSurvey(Survey survey) {
        WebApiInvoker<Survey> client = new WebApiInvoker<>();
        client.invoker = invoker;
        client.httpAction = "post";
        client.objToPost = survey;
        client.execute(SbsConstants.baseUrl + "/survey");
    }

    public void getSurveysAsync(int startIndex, int fetch_count) {
        WebApiInvoker<Survey> client = new WebApiInvoker<>();
        client.invoker = invoker;
        client.httpAction = "get";
        client.execute(SbsConstants.baseUrl + "/survey/"+ startIndex + "/" + fetch_count);
    }

    public void getSurvey(int surveyId) {
        WebApiInvoker<Survey> client = new WebApiInvoker<>();
        client.invoker = invoker;
        client.httpAction = "get";
        client.execute(SbsConstants.baseUrl + "/survey/"+ surveyId);
    }
}
