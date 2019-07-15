package com.example.sbs.data;

import android.content.Context;
import android.database.Cursor;

import com.example.sbs.data.model.LoggedInUser;
import com.example.sbs.data.model.Role;
import com.example.sbs.data.model.User;

import java.io.IOException;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private DataBaseHelper dbAccess;


    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    // private constructor : singleton access
    private LoginRepository(DataBaseHelper dbAccess) {
        this.dbAccess = dbAccess;
    }

    public static LoginRepository getInstance(Context context) {
        if (instance == null) {
            instance = new LoginRepository(new DataBaseHelper(context));
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public Result<LoggedInUser> login(String username, String password) {

        try {
            LoggedInUser u = null;
            String pwd = "";
            dbAccess.openDataBase();
            Cursor cursor = dbAccess.executeCursor("select id, name, role_id, password from user u inner join user_roles ur on u.id=ur.user_id where userid='"+ username +"' ");

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                u =  new LoggedInUser(
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getInt(2)
                        );
                pwd = cursor.getString(3);
                cursor.moveToNext();
            }
            cursor.close();
            dbAccess.close();

            if(u != null)
            {
                if(!password.equals(pwd))
                {
                    return new Result.Error(new IOException("Invalid credentials"));
                }
                setLoggedInUser(u);
                LocalCache.currentUser = u;
                return new Result.Success<>(u);
            }
            else
            {
                return new Result.Error(new IOException("User with this username does not exist. Please check if your account is setup and you synced your app with server."));
            }

        } catch (Exception e) {
            return new Result.Error(new IOException("Login failed", e));
        }

    }
}
