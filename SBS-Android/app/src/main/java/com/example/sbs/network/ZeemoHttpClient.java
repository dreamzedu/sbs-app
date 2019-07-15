package com.example.sbs.network;

import android.widget.Toast;

import com.google.gson.GsonBuilder;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Created by Manoj_Dwivedi on 1/22/2016.
 */
public class ZeemoHttpClient {


    private String convertStreamToString(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }

        return stringBuilder.toString();
    }

    public String Get(String url) throws IOException {

        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();

        HttpGet httpGet = new HttpGet(url);
        try {

            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Accept-Encoding", "gzip");

            HttpResponse httpResponse = defaultHttpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                InputStream inputStream = httpEntity.getContent();
                Header contentEncoding = httpResponse.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    inputStream = new GZIPInputStream(inputStream);
                }

                String resultString = convertStreamToString(inputStream);
                inputStream.close();

                return resultString;

            }

        } catch (UnsupportedEncodingException e) {
            String s = e.getMessage();

            return SbsConstants.GENERIC_ERROR;

        } catch (ClientProtocolException e) {
            //Toast.makeText(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
            String s = e.getMessage();
            return SbsConstants.SERVER_CONNECTION_ERROR;

        } catch (IOException e) {
            String s = e.getMessage();
            return SbsConstants.SERVER_CONNECTION_ERROR;

        }
        catch (Exception e)
        {
            String s = e.getMessage();
            return SbsConstants.GENERIC_ERROR ;
        }

        return null;
    }

    public String Post(final String url, final StringEntity objToPost) {
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        try {

            //StringEntity stringEntity = new StringEntity(new GsonBuilder().create().toJson(object));
            if(objToPost != null) {
                httpPost.setEntity(objToPost);//stringEntity);
            }
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Accept-Encoding", "gzip");

            HttpResponse httpResponse = defaultHttpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                InputStream inputStream = httpEntity.getContent();
                Header contentEncoding = httpResponse.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    inputStream = new GZIPInputStream(inputStream);
                }

                String resultString = convertStreamToString(inputStream);
                inputStream.close();
                //return new GsonBuilder().create().fromJson(resultString, objectClass);
                return resultString;
            }

        }
        catch (UnsupportedEncodingException e)
        {
            System.out.println(e.getMessage());
        }
        catch (ClientProtocolException e)
        {
            System.out.println(e.getMessage());
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    public String Put(final String url, final StringEntity objToPost) {
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        //HttpPut httpPut = new HttpPut(url);
        HttpPost httpPut = new HttpPost(url); // Using post instead of put as the put verb is not supported by the hosting server
        try {

            //StringEntity stringEntity = new StringEntity(new GsonBuilder().create().toJson(object));
            if(objToPost != null) {
                httpPut.setEntity(objToPost);//stringEntity);
            }
            httpPut.setHeader("Accept", "application/json");
            httpPut.setHeader("Content-type", "application/json");
            httpPut.setHeader("Accept-Encoding", "gzip");

            HttpResponse httpResponse = defaultHttpClient.execute(httpPut);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                InputStream inputStream = httpEntity.getContent();
                Header contentEncoding = httpResponse.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    inputStream = new GZIPInputStream(inputStream);
                }

                String resultString = convertStreamToString(inputStream);
                inputStream.close();
                //return new GsonBuilder().create().fromJson(resultString, objectClass);
                return resultString;
            }

        } catch (UnsupportedEncodingException e)
        {

        } catch (ClientProtocolException e)
        {

        }
        catch (IOException e)
        {

        }
        catch(Exception ex)
        {

        }
        return null;
    }

    public String Delete(String url) {

        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();

        //HttpDelete httpDelete = new HttpDelete(url);
        HttpPost httpDelete = new HttpPost(url); // Using post instead of put as the delete verb is not supported by the hosting server
        try {

            httpDelete.setHeader("Accept", "application/json");
            httpDelete.setHeader("Accept-Encoding", "gzip");

            HttpResponse httpResponse = defaultHttpClient.execute(httpDelete);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                InputStream inputStream = httpEntity.getContent();
                Header contentEncoding = httpResponse.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    inputStream = new GZIPInputStream(inputStream);
                }

                String resultString = convertStreamToString(inputStream);
                inputStream.close();

                return resultString;

            }

        } catch (UnsupportedEncodingException e) {
            String s = e.getMessage();

        } catch (ClientProtocolException e) {
            String s = e.getMessage();

        } catch (IOException e) {
            String s = e.getMessage();

        }
        catch (Exception e)
        {
            String s = e.getMessage();
        }

        return null;
    }
}
