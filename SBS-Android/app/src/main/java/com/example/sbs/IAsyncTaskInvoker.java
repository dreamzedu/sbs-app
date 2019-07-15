package com.example.sbs;

import android.content.Context;

public interface IAsyncTaskInvoker {
    void OnBeforeExecute(String httpAction);
    void OnAfterExecute(String result, String httpAction);
    void OnError(String errorCode);
    Context getContext();
}
