package com.example.sbs.data;

import com.example.sbs.IAsyncTaskInvoker;
import com.example.sbs.data.model.Block;
import com.example.sbs.network.SbsConstants;
import com.example.sbs.network.WebApiInvoker;


public class BlockRepositoryRemote {

    IAsyncTaskInvoker invoker;

    public BlockRepositoryRemote(IAsyncTaskInvoker context)
    {
        invoker = context;
    }

    public void getAllBlocksAsync(int distId) {

        WebApiInvoker<Block> client = new WebApiInvoker<>();
        client.invoker = invoker;
        client.httpAction = "get";
        client.execute(SbsConstants.baseUrl + "/block/"+ distId);
    }

    public void saveBlockAsync(Block u) {
        WebApiInvoker<Block> client = new WebApiInvoker<>();
        client.invoker = invoker;
        client.httpAction = "post";
        client.objToPost = u;
        client.execute(SbsConstants.baseUrl + "/block");
    }

    public void updateBlockAsync(Block u) {
        WebApiInvoker<Block> client = new WebApiInvoker<>();
        client.invoker = invoker;
        client.httpAction = "put";
        client.objToPost = u;
        client.execute(SbsConstants.baseUrl + "/block/update/"+ u.id);
}

    public void deleteBlockAsync(String id) {
        WebApiInvoker<Block> client = new WebApiInvoker<>();
        client.invoker = invoker;
        client.httpAction = "delete";
        client.execute(SbsConstants.baseUrl + "/block/delete/"+ id);
    }
}
