package com.sz.baseuiframe.okhttp.builder;

import com.sz.baseuiframe.okhttp.OkHttpUtils;
import com.sz.baseuiframe.okhttp.request.OtherRequest;
import com.sz.baseuiframe.okhttp.request.RequestCall;

public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers,id).build();
    }
}
