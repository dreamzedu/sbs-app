package com.example.sbs.data.helpers;

import android.content.Context;

import com.example.sbs.R;
import com.example.sbs.network.SbsConstants;

public class ErrorMessageHelper {
    public static String GetMessage(String errorCode, Context context)
    {
        if(errorCode.startsWith(SbsConstants.VALIDATION))
        {
            return errorCode.split(":")[1];
        }
        else if (errorCode.equals(SbsConstants.NO_INTERNET))
        {
            return context.getString(R.string.no_internet) +" "+ context.getString(R.string.check_internet);
        }
        else if (errorCode.equals(SbsConstants.SERVER_CONNECTION_ERROR))
        {
            return context.getString(R.string.server_connection_error);
        }
        else if (errorCode.equals(SbsConstants.SERVER_ERROR))
        {
            return context.getString(R.string.server_error);
        }
        else if (errorCode.equals(SbsConstants.GENERIC_ERROR))
        {
            return context.getString(R.string.generic_error);
        }
        else
            return context.getString(R.string.generic_error);
    }
}
