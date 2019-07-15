package com.example.sbs.data;

import com.example.sbs.IAsyncTaskInvoker;
import com.example.sbs.data.model.User;
import com.example.sbs.network.SbsConstants;
import com.example.sbs.network.WebApiInvoker;


public class ReportRepositoryRemote {

    IAsyncTaskInvoker invoker;

    public ReportRepositoryRemote(IAsyncTaskInvoker context)
    {
        invoker = context;
    }

    public void getReportByDistrictAsync(int districtId) {

        WebApiInvoker<User> client = new WebApiInvoker<User>();
        client.invoker = invoker;
        client.httpAction = "get";
        client.execute(SbsConstants.baseUrl + "/analytics/bydistrict/"+ districtId);
    }
}
