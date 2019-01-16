package com.sz.baseuiframe;

import android.app.Application;
import android.content.Context;

import com.cheung.android.base.baseuiframe.BasicConfig;
import com.cheung.android.base.baseuiframe.log.Print;
import com.litesuits.common.assist.Toastor;
import com.litesuits.common.data.DataKeeper;
import com.litesuits.orm.LiteOrm;
import com.qmuiteam.qmui.util.QMUIColorHelper;
import com.qmuiteam.qmui.util.QMUIDeviceHelper;
import com.qmuiteam.qmui.util.QMUIDrawableHelper;
import com.sz.baseuiframe.Models.LoginModel;
import com.sz.baseuiframe.okhttp.OkHttpUtils;

import okhttp3.OkHttpClient;

/**
 * author: C_CHEUNG
 * created on: 2017/12/8
 * description:
 */
public class MyApp extends Application {
    public static final String INTENT_VALUE_TITLE_STR = MyApp.class.getSimpleName() + ".intent.string.title";
    public static Context sAppContext;
    public LiteOrm liteOrm;
    public DataKeeper dataKeeper;
    public static MyApp instance;

    public LoginModel.UserInfo userInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = this;
        BasicConfig.getInstance(this)
                .initDir("BaseUIFrame") // or initDir(rootDirName)
                .initExceptionHandler()
                .initLog(true);
        if (instance == null) {
            instance = this;
        }
        if (liteOrm == null) {
            liteOrm = LiteOrm.newSingleInstance(this, "safetymsg.db");
        }
        liteOrm.setDebugged(true); // open the log
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);


        dataKeeper = new DataKeeper(this, "config");
        Print.d("判断是否是平板设备:" + QMUIDeviceHelper.isTablet(this));
        Print.d("判断是否Flyme系统:" + QMUIDeviceHelper.isFlyme());
        Print.d("判断是否MIUI系统:" + QMUIDeviceHelper.isMIUI());
        Print.d("判断是否魅族手机:" + QMUIDeviceHelper.isMeizu());
        Print.d("判断是否小米手机:" + QMUIDeviceHelper.isXiaomi());
    }
}
