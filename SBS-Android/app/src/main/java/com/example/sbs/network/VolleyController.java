package com.example.sbs.network;

import android.app.Application;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;

import org.apache.http.util.TextUtils;

// Not using this class in the project
// This is used for making async network calls and this uses volley library
public class VolleyController {

    Context context;

    public static final String TAG = "SyncRequests";


    private RequestQueue mRequestQueue;


    private static VolleyController sInstance;

   private  VolleyController(Context context)
   {
       this.context = context;
       mRequestQueue = Volley.newRequestQueue(context);
   }

    /**
     * @return ApplicationController singleton instance
     */
    public static synchronized VolleyController getInstance(Context context) {
        if(sInstance == null)
            sInstance = new VolleyController(context);
        return sInstance;
    }


    /**
     * Adds the specified request to the global queue, if tag is specified
     * then it is used else Default TAG is used.
     *
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        VolleyLog.d("Adding request to queue: %s", req.getUrl());

        mRequestQueue.add(req);
    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     *
     * @param req
     */
    public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(TAG);
        mRequestQueue.add(req);
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important
     * to specify a TAG so that the pending/ongoing requests can be cancelled.
     *
     * @param tag
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void startQueue() {
        if (mRequestQueue != null) {
            mRequestQueue.start();
        }
    }

    public void stopQueue() {
        if (mRequestQueue != null) {
            mRequestQueue.stop();
        }
    }
}
