package com.sz.baseuiframe;

import com.sz.baseuiframe.okhttp.OkHttpUtils;
import com.sz.baseuiframe.okhttp.builder.PostFormBuilder;

public class CommHttp {
    public static String BaseUrl="http://182.120.32.96:88/";

    public static PostFormBuilder post(String url)
    {
        return OkHttpUtils.post().url(BaseUrl+url);
    }
}
