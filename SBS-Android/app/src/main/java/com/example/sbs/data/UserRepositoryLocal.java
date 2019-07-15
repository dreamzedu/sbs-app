package com.example.sbs.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.sbs.data.model.Block;
import com.example.sbs.data.model.Role;
import com.example.sbs.data.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserRepositoryLocal {

    DataBaseHelper dbAccess;
    public UserRepositoryLocal(Context context)
    {
        dbAccess = new DataBaseHelper(context);
    }

    public List<Role> getUserTypes() {

        if(LocalCache.usertypes == null)
        {
            List<Role> list = new ArrayList<>();
            dbAccess.openDataBase();
            Cursor cursor = dbAccess.executeCursor("select * from role");
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Role r = new Role();
                r.id = cursor.getInt(0);
                r.name = cursor.getString(1);
                list.add(r);
                cursor.moveToNext();
            }
            cursor.close();
            dbAccess.close();

            LocalCache.usertypes = list;
        }
        return LocalCache.usertypes;
    }


    public void resetUserData(JSONArray users) throws IOException {
        try {
            dbAccess.openWritableDataBase();
            dbAccess.executeNonQuery("delete from user");
            for (int i = 0; i < users.length(); i++) {
                JSONObject u = users.getJSONObject(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put("id", u.getString("id"));
                contentValues.put("name", u.getString("name"));
                contentValues.put("userid", u.getString("userid"));
                contentValues.put("password", u.getString("password"));
                contentValues.put("phone", u.getString("phone"));
                contentValues.put("email", u.getString("email"));

                dbAccess.insertData("user", contentValues);
            }
        } catch (Exception ex) {
            throw new IOException(ex);
        }
        finally {
            dbAccess.close();
        }
    }

    public void resetRoleData(JSONArray roles) throws IOException {
        try {
            dbAccess.openWritableDataBase();
            dbAccess.executeNonQuery("delete from role");
            for (int i = 0; i < roles.length(); i++) {
                JSONObject u = roles.getJSONObject(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put("id", u.getString("id"));
                contentValues.put("name", u.getString("name"));

                dbAccess.insertData("role", contentValues);
            }
        } catch (Exception ex) {
            throw new IOException(ex);
        }
        finally {
            dbAccess.close();
        }
    }

    public void resetUserRoleData(JSONArray user_roles) throws IOException {
        try {
            dbAccess.openWritableDataBase();
            dbAccess.executeNonQuery("delete from user_roles");
            for (int i = 0; i < user_roles.length(); i++) {
                JSONObject u = user_roles.getJSONObject(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put("user_id", u.getString("user_id"));
                contentValues.put("role_id", u.getString("role_id"));

                dbAccess.insertData("user_roles", contentValues);
            }
        } catch (Exception ex) {
            throw new IOException(ex);
        }
        finally {
            dbAccess.close();
        }
    }
}
