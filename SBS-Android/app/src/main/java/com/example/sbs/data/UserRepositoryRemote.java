package com.example.sbs.data;

import com.example.sbs.IAsyncTaskInvoker;
import com.example.sbs.data.model.User;
import com.example.sbs.network.SbsConstants;
import com.example.sbs.network.WebApiInvoker;


public class UserRepositoryRemote {

    IAsyncTaskInvoker invoker;

    public UserRepositoryRemote(IAsyncTaskInvoker context)
    {
        invoker = context;
    }

    public void getAllUsersAsync() {

        WebApiInvoker<User> client = new WebApiInvoker<User>();
        client.invoker = invoker;
        client.httpAction = "get";
        client.execute(SbsConstants.baseUrl + "/user");
    }

    public void saveUserAsync(User u) {
        WebApiInvoker<User> client = new WebApiInvoker<User>();
        client.invoker = invoker;
        client.httpAction = "post";
        client.objToPost = u;
        client.execute(SbsConstants.baseUrl + "/user");
    }

    public void updateUserAsync(User u) {
        WebApiInvoker<User> client = new WebApiInvoker<User>();
        client.invoker = invoker;
        client.httpAction = "put";
        client.objToPost = u;
        client.execute(SbsConstants.baseUrl + "/user/update/"+ u.id);
}

    public void deleteUserAsync(String id) {
        WebApiInvoker<User> client = new WebApiInvoker<User>();
        client.invoker = invoker;
        client.httpAction = "delete";
        client.execute(SbsConstants.baseUrl + "/user/delete/"+ id);
    }
}
