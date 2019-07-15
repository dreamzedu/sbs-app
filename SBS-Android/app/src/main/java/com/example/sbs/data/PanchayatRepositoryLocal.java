package com.example.sbs.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.sbs.data.model.Block;
import com.example.sbs.data.model.Panchayat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PanchayatRepositoryLocal {

    DataBaseHelper dbAccess;
    public PanchayatRepositoryLocal(Context context)
    {
        dbAccess = new DataBaseHelper(context);
    }

    public List<Panchayat> getAllPanchayats() {
        if(LocalCache.panchayats == null) {
            List<Panchayat> list = new ArrayList<Panchayat>();
            dbAccess.openDataBase();
            Cursor cursor = dbAccess.executeCursor("select * from panchayat");
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Panchayat p = new Panchayat();
                p.id = cursor.getString(0);
                p.name = cursor.getString(1);
                list.add(p);
                cursor.moveToNext();
            }
            cursor.close();
            dbAccess.close();
            LocalCache.panchayats = list;
        }
        return LocalCache.panchayats;
    }

    public List<Panchayat> getPanchayats(String blockId) {

        List<Panchayat> list = new ArrayList<Panchayat>();
        dbAccess.openDataBase();
        Cursor cursor = dbAccess.executeCursor("select * from panchayat where block_id=" + blockId);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Panchayat p = new Panchayat();
            p.id = cursor.getString(0);
            p.name = cursor.getString(1);
            list.add(p);
            cursor.moveToNext();
        }
        cursor.close();
        dbAccess.close();
        return list;
    }

    public void resetPanchayatData(JSONArray panchayats) throws IOException {
        try {
            dbAccess.openWritableDataBase();
            dbAccess.executeNonQuery("delete from panchayat");
            for (int i = 0; i < panchayats.length(); i++) {
                JSONObject u = panchayats.getJSONObject(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put("id", u.getString("id"));
                contentValues.put("name", u.getString("name"));
                contentValues.put("block_id", u.getString("block_id"));
                dbAccess.insertData("panchayat", contentValues);
            }
        }
        catch(Exception ex)
        {
            throw new IOException(ex);
        }
        finally {
            dbAccess.close();
        }
    }
}
