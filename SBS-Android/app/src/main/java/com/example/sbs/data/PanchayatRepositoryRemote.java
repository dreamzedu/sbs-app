package com.example.sbs.data;

import com.example.sbs.IAsyncTaskInvoker;
import com.example.sbs.data.model.Panchayat;
import com.example.sbs.network.SbsConstants;
import com.example.sbs.network.WebApiInvoker;


public class PanchayatRepositoryRemote {

    IAsyncTaskInvoker invoker;

    public PanchayatRepositoryRemote(IAsyncTaskInvoker context)
    {
        invoker = context;
    }

    public void getAllPanchayatsAsync(String blockId) {

        WebApiInvoker<Panchayat> client = new WebApiInvoker<>();
        client.invoker = invoker;
        client.httpAction = "get";
        client.execute(SbsConstants.baseUrl + "/panchayat/"+ blockId);
    }

    public void savePanchayatAsync(Panchayat u) {
        WebApiInvoker<Panchayat> client = new WebApiInvoker<>();
        client.invoker = invoker;
        client.httpAction = "post";
        client.objToPost = u;
        client.execute(SbsConstants.baseUrl + "/panchayat");
    }

    public void updatePanchayatAsync(Panchayat u) {
        WebApiInvoker<Panchayat> client = new WebApiInvoker<>();
        client.invoker = invoker;
        client.httpAction = "put";
        client.objToPost = u;
        client.execute(SbsConstants.baseUrl + "/panchayat/update/"+ u.id);
}

    public void deletePanchayatAsync(String id) {
        WebApiInvoker<Panchayat> client = new WebApiInvoker<>();
        client.invoker = invoker;
        client.httpAction = "delete";
        client.execute(SbsConstants.baseUrl + "/panchayat/delete/"+ id);
    }
}
