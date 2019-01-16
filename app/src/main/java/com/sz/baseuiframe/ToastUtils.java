package com.sz.baseuiframe;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import com.sdsmdg.tastytoast.TastyToast;

public class ToastUtils {

    public static void show(Context context, String text) {
        try {
            TastyToast.makeText(context, text, TastyToast.LENGTH_SHORT, TastyToast.INFO);
//            if(toast!=null){
//                toast.setText(text);
//            }else{
//                toast= Toast.makeText(context, text, Toast.LENGTH_SHORT);
//            }
//            toast.show();
        } catch (Exception e) {
            //解决在子线程中调用Toast的异常情况处理
            Looper.prepare();
            TastyToast.makeText(context, text, TastyToast.LENGTH_SHORT, TastyToast.INFO);
            Looper.loop();
        }
    }
    public static void show(Context context, String msg, int length, int type) {
        try {
            TastyToast.makeText(context, msg, length, type);
//            if(toast!=null){
//                toast.setText(text);
//            }else{
//                toast= Toast.makeText(context, text, Toast.LENGTH_SHORT);
//            }
//            toast.show();
        } catch (Exception e) {
            //解决在子线程中调用Toast的异常情况处理
            Looper.prepare();
            TastyToast.makeText(context, msg, length, type);
            Looper.loop();
        }
    }
}
