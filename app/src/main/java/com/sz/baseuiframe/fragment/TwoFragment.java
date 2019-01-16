package com.sz.baseuiframe.fragment;

import android.content.Intent;
import android.telecom.Call;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.cheung.android.base.baseuiframe.activity.BaseFragment;
import com.cheung.android.base.baseuiframe.utils.ToastUtil;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.sdsmdg.tastytoast.TastyToast;
import com.sz.baseuiframe.AddActivity;
import com.sz.baseuiframe.CommHttp;
import com.sz.baseuiframe.IDCardActivity;
import com.sz.baseuiframe.Models.WanChengModel;
import com.sz.baseuiframe.MyApp;
import com.sz.baseuiframe.R;
import com.sz.baseuiframe.TestActivity;
import com.sz.baseuiframe.ToastUtils;
import com.sz.baseuiframe.okhttp.callback.Callback;

import okhttp3.Request;

import static com.cheung.android.base.baseuiframe.utils.ToastUtil.context;


/**
 * author: C_CHEUNG
 * created on: 2017/12/11
 * description:
 */
public class TwoFragment extends BaseFragment {
    private ImageView imageView;
    private TextView text_info;
    private TextView text_sl;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_two;
    }

    @Override
    protected void initUI(View parentView) {
        Button btn = (Button)parentView.findViewById(R.id.button_add);
//        imageView = (ImageView) parentView.findViewById(R.id.iv_image);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity,AddActivity.class);
                startActivity(intent);
            }
        });

        text_info=(TextView)parentView.findViewById(R.id.text_info);
        text_sl=(TextView)parentView.findViewById(R.id.text_sl);
        text_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadText();
            }
        });
        Button btn3 = (Button)parentView.findViewById(R.id.button_hexiao);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity,IDCardActivity.class);
                startActivity(intent);
            }
        });
        initAccessTokenWithAkSk();

        reloadText();

    }
   private void reloadText(){

       CommHttp.post("jrenwu.php")
               .addParams("YHID", MyApp.instance.userInfo.getYHID())
               .build().execute(new Callback<WanChengModel>() {
           @Override
           public void onBefore(Request request, int id) {
           }

           @Override
           public void onAfter(int id) {

           }

           @Override
           public void onError(okhttp3.Call call, Exception e, int id) {
               ToastUtils.show(mActivity, "“完成情况”加载错误，点击重新加载", TastyToast.LENGTH_SHORT,TastyToast.SUCCESS);
           }

           @Override
           public void onResponse(WanChengModel response, int id) {
               text_info.setText(response.getData().getDWMC());
               text_sl.setText("完成:"+response.getData().getDQRW()+",总:"+response.getData().getRWSL());
           }
       });
    }
    /**
     * 用明文ak，sk初始化
     */
    private void initAccessTokenWithAkSk() {
        OCR.getInstance(mActivity).initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                String token = result.getAccessToken();
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                //alertText("AK，SK方式获取token失败", error.getMessage());
            }
        },mActivity.getApplicationContext(),  "126bzXzr5Qg4rGXwCtuWXxv8", "AdaqPZPU8chQuDZt4pfyRqCPXkX51zc2");
    }
    private void alertText(String msg)
    {
        QMUITipDialog.Builder builder = new QMUITipDialog.Builder(mActivity);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // 释放内存资源
        OCR.getInstance(mActivity).release();
    }
}
