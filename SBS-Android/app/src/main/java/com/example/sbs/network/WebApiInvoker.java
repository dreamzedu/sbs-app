package com.example.sbs.network;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceActivity;

import com.example.sbs.IAsyncTaskInvoker;
import com.google.gson.GsonBuilder;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

/**
 * Created by Manoj_Dwivedi on 1/21/2016.
 */
public class WebApiInvoker<T> extends AsyncTask<String, Void, String>
{
    public IAsyncTaskInvoker invoker = null;
    public T objToPost = null;
    public String httpAction = "get";
    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        if(invoker != null)
            invoker.OnBeforeExecute(httpAction);
    }

    @Override
    protected String doInBackground(String... params)
    {
        String result = null;
        if(invoker != null && !NetworkChecker.isNetworkAvailable(invoker.getContext()))
        {
            return SbsConstants.NO_INTERNET;
        }

        try {
            ZeemoHttpClient c = new ZeemoHttpClient();

            if (httpAction.equalsIgnoreCase("POST")) {
                StringEntity stringEntity = null;
                if(objToPost != null) {
                    stringEntity = new StringEntity(new GsonBuilder().create().toJson(objToPost));
                }
                result = c.Post(params[0], stringEntity);
            }
            else if (httpAction.equalsIgnoreCase("PUT")) {
                StringEntity stringEntity = null;
                if(objToPost != null) {
                    stringEntity = new StringEntity(new GsonBuilder().create().toJson(objToPost));
                }
                result = c.Put(params[0], stringEntity);
            }
            else if (httpAction.equalsIgnoreCase("Delete")) {

                result = c.Delete(params[0]);
            }
            else if (httpAction.equalsIgnoreCase("GET"))
            {
                result = c.Get(params[0]);
            }

        }
        catch(Exception ex)
        {
            String msg = ex.getMessage();
            result = SbsConstants.ERROR;
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result)  {
        super.onPostExecute(result);

        if (invoker != null) {
            if(result.startsWith(SbsConstants.ERROR) )
            {
                invoker.OnError(result);

            }else if(result.startsWith(SbsConstants.MESSAGE) )
            {
                invoker.OnError(result);
            }
            else {
                invoker.OnAfterExecute(result, httpAction);
            }
        }
    }


}
