package com.sz.baseuiframe;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.cheung.android.base.baseuiframe.activity.BaseUIActivity;
import com.litesuits.common.assist.Check;
import com.litesuits.common.assist.TimeCounter;
import com.litesuits.common.assist.Toastor;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.sdsmdg.tastytoast.TastyToast;
import com.sz.baseuiframe.Models.LoginModel;
import com.sz.baseuiframe.okhttp.callback.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Request;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseUIActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    // UI references.
    private AutoCompleteTextView form_userName;
    private EditText form_password;
    private Button mEmailSignInButton;
    private CheckBox autoLogin;
    private TimerTask timerTask;
    private Timer timer1;
    private  Button BtnAutoLogin;
    @BindView(R.id.topbar)
    QMUITopBar topBar;

    String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(mActivity);
        title = getIntent().getStringExtra(MyApp.INTENT_VALUE_TITLE_STR);
        if (title != null) {
            topBar.setTitle(title);
        }
        // Set up the login form.
        form_userName = (AutoCompleteTextView) findViewById(R.id.form_userName);
        timer1 = new Timer();
        form_password = (EditText) findViewById(R.id.form_password);
        form_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        autoLogin=(CheckBox)findViewById(R.id.checkBox);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        BtnAutoLogin=(Button) findViewById(R.id.BtnAutoLogin);
        BtnAutoLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!timerTask.cancel()){
                    timerTask.cancel();
                    timer1.cancel();
                }
                BtnAutoLogin.setVisibility(View.GONE);
                mEmailSignInButton.setVisibility(View.VISIBLE);
            }
        });
        initAutoLogin();
    }
private  void  initAutoLogin() {
    if (MyApp.instance.dataKeeper.get("autoLogin", false)) {
        mEmailSignInButton.setVisibility(View.GONE);
        BtnAutoLogin.setVisibility(View.VISIBLE);
        form_userName.setText(MyApp.instance.dataKeeper.get("userName", ""));
        form_password.setText(MyApp.instance.dataKeeper.get("password", ""));
        timerTask = new TimerTask() {
            int cnt = 3;
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BtnAutoLogin.setText("取消（"+cnt--+"秒后登录）");
                        if(cnt<=0)
                        {
                            if (!timerTask.cancel()){
                                timerTask.cancel();
                                timer1.cancel();
                            }
                            BtnAutoLogin.setText("登录中..");
                            attemptLogin();
                        }
                    }
                });
            }
        };
        timer1.schedule(timerTask,0,1000);
    }
    else{
        mEmailSignInButton.setVisibility(View.VISIBLE);
        BtnAutoLogin.setVisibility(View.GONE);
    }
}
    private void attemptLogin() {
        //

//        //设置三秒延迟模仿延时获取数据
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //加载数据
//                Toastor toastor = new Toastor(mActivity);
//                toastor.showToast("登录成功！");
//                Intent intent = new Intent(mActivity, MainActivity.class);
//                startActivity(intent);
//            }
//        },3000);
                final String UserName = form_userName.getText().toString();
        final String Password = form_password.getText().toString();

        if(UserName.equals("")) {
            ToastUtils.show(mActivity, "请填写用户名");
            return;
        }
        if(Password.equals("")) {
            ToastUtils.show(mActivity, "请填写密码");
            return;
        }

        //String uniqueId = DeviceUtils.GetDeviceId(this);
        CommHttp.post("jyanzheng.php")
                .addParams("USER", UserName)
                .addParams("PWD", Password)
                .build().execute(new Callback<LoginModel>() {
            @Override
            public void onBefore(Request request, int id) {
                mEmailSignInButton.setText("正在登陆....");
                mEmailSignInButton.setEnabled(false);
            }

            @Override
            public void onAfter(int id) {

            }

            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.show(mActivity, e.getMessage());
                mEmailSignInButton.setText("登陆");
                mEmailSignInButton.setEnabled(true);
            }

            @Override
            public void onResponse(LoginModel response, int id) {
                mEmailSignInButton.setText("登陆");
                mEmailSignInButton.setEnabled(true);
                if(response.getCode()==200)
                {
                    MyApp.instance.userInfo=response.getData();
                    ToastUtils.show(mActivity, "登陆成功", TastyToast.LENGTH_SHORT,TastyToast.SUCCESS);
                    if (autoLogin.isChecked()) {
                        MyApp.instance.dataKeeper.put("autoLogin", true);
                        MyApp.instance.dataKeeper.put("userName", UserName);
                        MyApp.instance.dataKeeper.put("password", Password);
                    }
                    Intent intent = new Intent(mActivity, MainActivity.class);
                    startActivity(intent);
                }
                else
                {
                    ToastUtils.show(mActivity, response.getMsg(), TastyToast.LENGTH_SHORT,TastyToast.ERROR);
                }

            }
        });
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_login;
    }
}
