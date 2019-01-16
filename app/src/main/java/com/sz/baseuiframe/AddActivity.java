package com.sz.baseuiframe;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.IDCardParams;
import com.baidu.ocr.sdk.model.IDCardResult;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.baidu.ocr.ui.camera.CameraNativeHelper;
import com.baidu.ocr.ui.camera.CameraView;
import com.cheung.android.base.baseuiframe.activity.BaseUIActivity;
import com.litesuits.common.assist.Toastor;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.sdsmdg.tastytoast.TastyToast;
import com.sz.baseuiframe.Models.BaseModel;
import com.sz.baseuiframe.Models.LoginModel;
import com.sz.baseuiframe.Models.PersonModel;
import com.sz.baseuiframe.Utils.IdCardUtil;
import com.sz.baseuiframe.Utils.RegexUtil;
import com.sz.baseuiframe.okhttp.callback.Callback;
import com.sz.baseuiframe.widget.CustomDatePicker;

import org.w3c.dom.Text;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Request;

public class AddActivity extends BaseUIActivity implements View.OnClickListener {
    @BindView(R.id.topbar)
    QMUITopBar topBar;
    private CustomDatePicker form_czrq;
    private TextView form_csrq_btn;
    private Button button_add;
    private Button button_sfzh;

    private Spinner form_zjlx;
    private EditText form_sfzh;
    private EditText form_xm;
    private Spinner form_xb;
    private EditText form_hjdz;
    private EditText form_xzz;
    private static final int REQUEST_CODE_CAMERA = 102;
    private Spinner form_mz;
    private EditText form_bz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(mActivity);
        topBar.setTitle("快速录入");
        initView();
        checkGalleryPermission();
        initIDCard();
    }

    private void initView() {
        //初始化日期选择按钮
        form_csrq_btn = (TextView) findViewById(R.id.form_csrq_btn);
        form_csrq_btn.setOnClickListener(this);

        //初始化日期控件
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        form_csrq_btn.setText("1986-11-02");
        form_czrq = new CustomDatePicker(mActivity, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                form_csrq_btn.setText(time.split(" ")[0]);
            }
        }, "1960-01-01 00:00", now); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        form_czrq.showSpecificTime(false); // 不显示时和分
        form_czrq.setIsLoop(false); // 不允许循环滚动


        button_add = (Button) findViewById(R.id.button_add);
        button_add.setOnClickListener(this);
        button_add.setEnabled(false);

        form_zjlx = (Spinner) findViewById(R.id.form_zjlx);
        form_sfzh = (EditText) findViewById(R.id.form_sfzh);
        form_xm = (EditText) findViewById(R.id.form_xm);
        form_xb = (Spinner) findViewById(R.id.form_xb);
        form_hjdz = (EditText) findViewById(R.id.form_hjdz);
        form_xzz = (EditText) findViewById(R.id.form_xzz);
        form_mz=(Spinner)findViewById(R.id.form_mz);
        form_bz=(EditText)findViewById(R.id.form_bz);
        form_sfzh.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()!=18)
                {
                    button_add.setEnabled(false);
                    return;
                }
                if(RegexUtil.isRealIDCard(s.toString()))
                {
                    button_add.setEnabled(false);
                    IdCardUtil idCardUtil=new IdCardUtil(s.toString());
                    StringBuilder sb = new StringBuilder(idCardUtil.getBirthday());//构造一个StringBuilder对象
                    sb.insert(4, "-");//在指定的位置1，插入指定的字符串
                    sb.insert(7, "-");//在指定的位置1，插入指定的字符串
                    form_csrq_btn.setText(sb.toString());
                    form_xb.setSelection( idCardUtil.getSex().endsWith("男") ? 0 : 1);

                    //验证身份证号是否能用
                    CommHttp.post("jchachong.php")
                            .addParams("ZJ", s.toString())
                            .addParams("YHID", MyApp.instance.userInfo.getYHID())
                            .build().execute(new Callback<LoginModel>() {
                        @Override
                        public void onBefore(Request request, int id) {
                            ToastUtils.show(mActivity, "正在查询身份证是否可用");
                        }

                        @Override
                        public void onAfter(int id) {

                        }

                        @Override
                        public void onError(Call call, Exception e, int id) {
                            ToastUtils.show(mActivity,"找管理员吧。。或者等会试试", TastyToast.LENGTH_SHORT,TastyToast.ERROR);
                            button_add.setEnabled(false);
                        }

                        @Override
                        public void onResponse(LoginModel response, int id) {
                            if(response.getCode()==200)
                            {
                                ToastUtils.show(mActivity, "哇塞，恭喜您又找到一个新客户！", TastyToast.LENGTH_SHORT,TastyToast.SUCCESS);
                                button_add.setEnabled(true);
                            }
                            else{
                                ToastUtils.show(mActivity, "不可用！已经登记过了！！！！", TastyToast.LENGTH_SHORT,TastyToast.WARNING);
                                button_add.setEnabled(false);
                            }
                        }
                    });
                }
                else{
                    ToastUtils.show(mActivity, "请输入正确的身份证号！", TastyToast.LENGTH_SHORT,TastyToast.WARNING);
                    button_add.setEnabled(false);
                }
            }
        });
    }

    private void initIDCard() {
        CameraNativeHelper.init(this, OCR.getInstance(this).getLicense(),
                new CameraNativeHelper.CameraNativeInitCallback() {
                    @Override
                    public void onError(int errorCode, Throwable e) {
                        String msg;
                        switch (errorCode) {
                            case CameraView.NATIVE_SOLOAD_FAIL:
                                msg = "加载so失败，请确保apk中存在ui部分的so";
                                break;
                            case CameraView.NATIVE_AUTH_FAIL:
                                msg = "授权本地质量控制token获取失败";
                                break;
                            case CameraView.NATIVE_INIT_FAIL:
                                msg = "本地质量控制";
                                break;
                            default:
                                msg = String.valueOf(errorCode);
                        }
                        ToastUtils.show(mActivity, msg);
                        button_sfzh.setEnabled(false);
                    }

                });
        button_sfzh = (Button) findViewById(R.id.button_sfzh);
        button_sfzh.setOnClickListener(this);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_add;
    }

    private boolean checkGalleryPermission() {
        int ret = ActivityCompat.checkSelfPermission(AddActivity.this, Manifest.permission
                .READ_EXTERNAL_STORAGE);
        if (ret != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1000);
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.form_csrq_btn:
                // 日期格式为yyyy-MM-dd
                form_czrq.show(form_csrq_btn.getText().toString());
                break;
            case R.id.button_add:
                SaveInfo();
                break;
            case R.id.button_sfzh:
                // 身份证正面拍照
                Intent intent = new Intent(AddActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_FRONT);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String contentType = data.getStringExtra(CameraActivity.KEY_CONTENT_TYPE);
                String filePath = FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath();
                if (!TextUtils.isEmpty(contentType)) {
                    if (CameraActivity.CONTENT_TYPE_ID_CARD_FRONT.equals(contentType)) {
                        recIDCard(IDCardParams.ID_CARD_SIDE_FRONT, filePath);
                    } else if (CameraActivity.CONTENT_TYPE_ID_CARD_BACK.equals(contentType)) {
                        recIDCard(IDCardParams.ID_CARD_SIDE_BACK, filePath);
                    }
                }
            }
        }
    }

    private void recIDCard(String idCardSide, String filePath) {
        ToastUtils.show(mActivity, "正在识别身份证，请稍后",TastyToast.LENGTH_SHORT,TastyToast.INFO);
        IDCardParams param = new IDCardParams();
        param.setImageFile(new File(filePath));
        // 设置身份证正反面
        param.setIdCardSide(idCardSide);
        // 设置方向检测
        param.setDetectDirection(true);
        // 设置图像参数压缩质量0-100, 越大图像质量越好但是请求时间越长。 不设置则默认值为20
        param.setImageQuality(20);

        OCR.getInstance(this).recognizeIDCard(param, new OnResultListener<IDCardResult>() {
            @Override
            public void onResult(IDCardResult result) {
                if (result != null && result.getDirection() != -1) {
                    //result.getEthnic()
                    form_sfzh.setText(result.getIdNumber().toString());
                    form_xm.setText(result.getName().toString());
                    form_xb.setSelection(result.getGender().toString().endsWith("男") ? 0 : 1);
                    form_hjdz.setText(result.getAddress().toString());
                    form_xzz.setText(result.getAddress().toString());
                    StringBuilder sb = new StringBuilder(result.getBirthday().toString());//构造一个StringBuilder对象
                    sb.insert(4, "-");//在指定的位置1，插入指定的字符串
                    sb.insert(7, "-");//在指定的位置1，插入指定的字符串
                    form_csrq_btn.setText(sb.toString());

                    SpinnerAdapter apsAdapter= form_mz.getAdapter();
                    int k= apsAdapter.getCount();
                    for(int i=0;i<k;i++){
                        if(result.getEthnic().toString().equals(apsAdapter.getItem(i).toString())){
                            form_mz.setSelection(i,true);
                            break;
                        }
                    }
                } else {
                    ToastUtils.show(mActivity, "错误得身份证识别，请重新拍照。",TastyToast.LENGTH_SHORT,TastyToast.WARNING);
                }
            }

            @Override
            public void onError(OCRError error) {
                ToastUtils.show(mActivity, error.getMessage());
            }
        });
    }

    private void SaveInfo() {
//        ToastUtils.show(mActivity, "保存成功。");
//        Intent intent = new Intent(mActivity, TestActivity.class);
//        startActivity(intent);
        CommHttp.post("jruku.php")
                .addParams("YHID", MyApp.instance.userInfo.getYHID())
                .addParams("YHXM", MyApp.instance.userInfo.getYHXM())
                .addParams("ZJ", form_sfzh.getText().toString())
                .addParams("XM", form_xm.getText().toString())
                .addParams("MZ", form_mz.getSelectedItem().toString())
                .addParams("XB", form_xb.getSelectedItem().toString())
                .addParams("CSRQ", form_csrq_btn.getText().toString())
                .addParams("HJD", form_hjdz.getText().toString())
                .addParams("XZZ", form_xzz.getText().toString())
                .addParams("BZ",form_bz.getText().toString())
                .build().execute(new Callback<PersonModel>() {
            @Override
            public void onBefore(Request request, int id) {
                button_add.setText("正在保存....");
                button_add.setEnabled(false);
            }

            @Override
            public void onAfter(int id) {
                button_add.setText("提交信息并打印");
                button_add.setEnabled(true);
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                button_add.setText("提交信息并打印");
                button_add.setEnabled(true);
            }

            @Override
            public void onResponse(PersonModel response, int id) {
                if(response.getCode()==200)
                {
                    ToastUtils.show(mActivity, "保存成功，准备打印。",TastyToast.LENGTH_SHORT,TastyToast.SUCCESS);
                    Intent intent = new Intent(mActivity, TestActivity.class);
                    intent.putExtra("BH",response.getData().getBH());
                    startActivity(intent);
                }
                else
                {
                    ToastUtils.show(mActivity, response.getMsg(),TastyToast.LENGTH_SHORT,TastyToast.WARNING);
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        // 释放本地质量控制模型
        CameraNativeHelper.release();
        super.onDestroy();
    }
}
