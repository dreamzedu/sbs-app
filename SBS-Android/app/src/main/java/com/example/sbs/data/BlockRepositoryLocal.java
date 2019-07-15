package com.example.sbs.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.sbs.data.model.Block;
import com.example.sbs.data.model.Role;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlockRepositoryLocal {

    DataBaseHelper dbAccess;
    public BlockRepositoryLocal(Context context)
    {
        dbAccess = new DataBaseHelper(context);
    }

    public List<Block> getAllBlocks() {

        if(LocalCache.blocks == null)
        {
            List<Block> list = new ArrayList<Block>();
            dbAccess.openDataBase();
            Cursor cursor = dbAccess.executeCursor("select * from block");
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Block b = new Block();
                b.id = cursor.getString(0);
                b.name = cursor.getString(1);
                list.add(b);
                cursor.moveToNext();
            }
            cursor.close();
            dbAccess.close();

            LocalCache.blocks = list;
        }
        return LocalCache.blocks;
    }
    public List<Block> getBlocks(int districtId) {

        List<Block> list = new ArrayList<Block>();
        dbAccess.openDataBase();
        Cursor cursor = dbAccess.executeCursor("select * from block where district_id=" + districtId);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Block b = new Block();
            b.id = cursor.getString(0);
            b.name = cursor.getString(1);
            list.add(b);
            cursor.moveToNext();
        }
        cursor.close();
        dbAccess.close();
        return list;
    }


    public void deleteBlock(String id) {
        dbAccess.openWritableDataBase();

        dbAccess.deleteData("block", "id=?", new String[]{id});

        dbAccess.close();
    }

    public void insertBlock(Block b) {
        dbAccess.openWritableDataBase();

        ContentValues values =new ContentValues();
        values.put("id", b.id);
        values.put("name", b.name);
        values.put("district_id", 1);

        dbAccess.insertData("block", values);

        dbAccess.close();
    }

    public void updateBlock(Block b) {
        dbAccess.openWritableDataBase();

        ContentValues values =new ContentValues();
        values.put("name", b.name);
        values.put("district_id", 1);

        dbAccess.updateData("block", values, "id=?", new String[]{b.id});

        dbAccess.close();
    }

    public void resetBlockData(JSONArray blocks) throws IOException {
        try {
            dbAccess.openWritableDataBase();
            dbAccess.executeNonQuery("delete from block");
            for (int i = 0; i < blocks.length(); i++) {
                JSONObject u = blocks.getJSONObject(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put("id", u.getString("id"));
                contentValues.put("name", u.getString("name"));
                contentValues.put("district_id", 1);
                dbAccess.insertData("block", contentValues);
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
