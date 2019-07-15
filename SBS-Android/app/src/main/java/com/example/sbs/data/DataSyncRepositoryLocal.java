package com.example.sbs.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.sbs.data.model.Block;
import com.example.sbs.data.model.DataVersion;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataSyncRepositoryLocal {

    DataBaseHelper dbAccess;
    public DataSyncRepositoryLocal(Context context)
    {
        dbAccess = new DataBaseHelper(context);
    }

    public List<DataVersion> getDataVersions() {


        List<DataVersion> list = new ArrayList<>();
        dbAccess.openDataBase();
        Cursor cursor = dbAccess.executeCursor("select tbl_name, version from data_version");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            DataVersion dv = new DataVersion();
            dv.tbl_name = cursor.getString(0);
            dv.version = cursor.getInt(1);
            list.add(dv);
            cursor.moveToNext();
        }
        cursor.close();
        dbAccess.close();

        return list;
    }


    public void updateDataVersion(String tbl_name, Integer version) {
        dbAccess.openWritableDataBase();

        ContentValues values = new ContentValues();
        values.put("version", version);

        dbAccess.updateData("data_version", values, "tbl_name=?", new String[]{tbl_name});

        dbAccess.close();
    }


}
