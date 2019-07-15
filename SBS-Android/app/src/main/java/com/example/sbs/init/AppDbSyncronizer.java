package com.example.sbs.init;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sbs.IAsyncTaskInvoker;
import com.example.sbs.R;
import com.example.sbs.SyncDbActivity;
import com.example.sbs.data.BlockRepositoryLocal;
import com.example.sbs.data.DataSyncRepositoryLocal;
import com.example.sbs.data.PanchayatRepositoryLocal;
import com.example.sbs.data.QuestionRepositoryLocal;
import com.example.sbs.data.UserRepositoryLocal;
import com.example.sbs.data.helpers.ErrorMessageHelper;
import com.example.sbs.data.model.Block;
import com.example.sbs.data.model.DataVersion;
import com.example.sbs.network.SbsConstants;
import com.example.sbs.network.VolleyController;
import com.example.sbs.network.WebApiInvoker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppDbSyncronizer implements IAsyncTaskInvoker {

    private SyncDbActivity context;
    private HashMap<String, Integer> dvMapServer;
    List<DataVersion> dvListLocal;
    DataSyncRepositoryLocal localRepo;
    Integer step=0;

    public void SyncAppDbWithServer(SyncDbActivity context)
    {
        this.context = context;
        step =0;

        getDataVersionFromLocal(); // Local data versions should be fetched before syncing with server.
        getDataVersionFromServer();
    }


    private void getDataVersionFromLocal() {
        try {
            localRepo = new DataSyncRepositoryLocal(context);
            dvListLocal = localRepo.getDataVersions();
        } catch(Exception ex)
        {
            context.dbSyncCompleteWithError(context.getString(R.string.generic_error));
        }
    }

    private void getDataVersionFromServer() {
        WebApiInvoker<Block> client = new WebApiInvoker<>();
        client.invoker = this;
        client.httpAction = "get";
        client.execute(SbsConstants.baseUrl + "/dataversion");
    }

    @Override
    public void OnBeforeExecute(String httpAction) {
        // do nothing
    }

    @Override
    public void OnAfterExecute(String result, String httpAction) {
        try {
            if (result != null) {
                JSONArray jsonArray = (JSONArray) new JSONTokener(result).nextValue();

                if (step == 0) {
                    validateDataVersion(jsonArray);
                } else {
                    switch (dvListLocal.get(step-1).tbl_name) {
                        case "block":
                            BlockRepositoryLocal repoBLocal = new BlockRepositoryLocal(context);
                            repoBLocal.resetBlockData(jsonArray);
                            break;
                        case "headings":
                            QuestionRepositoryLocal repoHLocal = new QuestionRepositoryLocal(context);
                            repoHLocal.resetHeadingsData(jsonArray);
                            break;
                        case "question":
                            QuestionRepositoryLocal repoQLocal = new QuestionRepositoryLocal(context);
                            repoQLocal.resetQuestionData(jsonArray);
                            break;
                        case "possible_answers":
                            QuestionRepositoryLocal repoPALocal = new QuestionRepositoryLocal(context);
                            repoPALocal.resetAnswersData(jsonArray);
                            break;
                        case "panchayat":
                            PanchayatRepositoryLocal repoPLocal = new PanchayatRepositoryLocal(context);
                            repoPLocal.resetPanchayatData(jsonArray);
                            break;
                        case "role":
                            UserRepositoryLocal repoRLocal = new UserRepositoryLocal(context);
                            repoRLocal.resetRoleData(jsonArray);
                            break;
                        case "user":
                            UserRepositoryLocal repoULocal = new UserRepositoryLocal(context);
                            repoULocal.resetUserData(jsonArray);
                            break;
                        case "user_roles":
                            UserRepositoryLocal repoURLocal = new UserRepositoryLocal(context);
                            repoURLocal.resetUserRoleData(jsonArray);
                            break;
                    }
                    localRepo.updateDataVersion(dvListLocal.get(step-1).tbl_name, dvMapServer.get(dvListLocal.get(step-1).tbl_name));
                }
                step++;
                processNextStep();
            }
        }
        catch(Exception ex)
        {
            String msg = ex.getMessage();
            context.dbSyncCompleteWithError(context.getString(R.string.generic_error));
        }
    }

    @Override
    public void OnError(String errorCode) {
        context.dbSyncCompleteWithError(ErrorMessageHelper.GetMessage(errorCode, context));
    }

    @Override
    public Context getContext()
    {
        return this.context;
    }

    private void validateDataVersion(JSONArray dataVersions) throws JSONException {

        try {
            dvMapServer = new HashMap<>();
            for (int i = 0; i < dataVersions.length(); i++) {
                JSONObject obj = dataVersions.getJSONObject(i);

                dvMapServer.put(obj.getString("tbl_name"), obj.getInt("version"));
            }

        }
        catch(JSONException ex) {
            throw ex;
        }

    }

    private void processNextStep()
    {
        if(step <= dvListLocal.size())
        {
            for(int i=step-1; i< dvListLocal.size(); i++)
            {
                if(Integer.parseInt(dvMapServer.get(dvListLocal.get(i).tbl_name).toString()) != dvListLocal.get(i).version)
                {
                    WebApiInvoker<Block> client = new WebApiInvoker<>();
                    client.invoker = this;
                    client.httpAction = "get";
                    client.execute(SbsConstants.baseUrl + getApiUrl(dvListLocal.get(i).tbl_name));
                    break;
                }
                else
                {
                    step++;
                }
            }

            if(step >= dvListLocal.size())
            {
                context.dbSyncCompleteSuccessfully(context.getString(R.string.sync_success));
                return;
            }
        }
        else
        {
            step = 0;
            context.dbSyncCompleteSuccessfully(context.getString(R.string.sync_success));
        }
    }

    private String getApiUrl(String name) {
        String result = "";
        switch (name) {
            case "block":
                result = "/block/1";
                break;
            case "headings":
                result = "/question/headings";
                break;
            case "question":
                result = "/question";
                break;
            case "possible_answers":
                result = "/question/answers";
                break;
            case "panchayat":
                result = "/panchayat/all";
                break;
            case "role":
                result = "/user/roles";
                break;
            case "user":
                result = "/user";
                break;
            case "user_roles":
                result = "/user/user-roles";
                break;

        }
        return result;
    }


}