package com.sz.baseuiframe;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;

import com.cheung.android.base.baseuiframe.activity.BaseActivityStack;
import com.cheung.android.base.baseuiframe.activity.BaseUIActivity;
import com.cheung.android.base.baseuiframe.utils.ToastUtil;
import com.litesuits.common.assist.SilentInstaller;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.widget.QMUITabSegment;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.sdsmdg.tastytoast.TastyToast;
import com.sz.baseuiframe.Models.BanBenModel;
import com.sz.baseuiframe.Models.LoginModel;
import com.sz.baseuiframe.fragment.BasicComponentsFragment;
import com.sz.baseuiframe.fragment.FourFragment;
import com.sz.baseuiframe.fragment.ThrFragment;
import com.sz.baseuiframe.fragment.TwoFragment;
import com.sz.baseuiframe.okhttp.callback.Callback;
import com.sz.baseuiframe.okhttp.callback.FileCallBack;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Request;


public class MainActivity extends BaseUIActivity {
    @BindView(R.id.tabSegment)
    QMUITabSegment mTabSegment;
    @BindView(R.id.contentViewPager)
    ViewPager mContentViewPager;


    private MainFPagerAdaper mainFPagerAdaper;
    private long mLastClickReturnTime ;
    private String BBH="1.0.0";
    //不使用通用TITLE
    @Override
    protected boolean useTopBar() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(mActivity);//在使用的主Model中compile

        int normalColor = QMUIResHelper.getAttrColor(mActivity, R.attr.qmui_config_color_gray_6);
        int selectColor = QMUIResHelper.getAttrColor(mActivity, R.attr.qmui_config_color_blue);

        mTabSegment.setDefaultNormalColor(normalColor);
        mTabSegment.setDefaultSelectedColor(selectColor);

        //平分屏幕宽度
        mTabSegment.setMode(QMUITabSegment.MODE_FIXED);
        //设置显示底标 默认不显示
        mTabSegment.setHasIndicator(true);
        //设置底标位置 默认在下方
        mTabSegment.setIndicatorPosition(true);

        //必须调用mTabSegment.setupWithViewPager(mContentViewPager,false);和ViewPager关联起来才回展示TAB
        mTabSegment
//                .addTab(new QMUITabSegment.Tab(
////                "基础组件"
////        ))
                .addTab(new QMUITabSegment.Tab(
                "快速核录"
        )).addTab(new QMUITabSegment.Tab(
                "我的业绩"
        )).addTab(new QMUITabSegment.Tab(
                "业绩排行"
        ));

        mainFPagerAdaper = new MainFPagerAdaper(getSupportFragmentManager());
        mContentViewPager.setOffscreenPageLimit(3);
        mContentViewPager.setAdapter(mainFPagerAdaper);


        mTabSegment.setupWithViewPager(mContentViewPager,false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 5);
        }
        getNewVersion();
    }

    public void getNewVersion() {
        CommHttp.post("jshengji.php")
                .addParams("BBH", BBH)
                .build().execute(new Callback<BanBenModel>() {
            @Override
            public void onBefore(Request request, int id) {

            }

            @Override
            public void onAfter(int id) {

            }

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(final BanBenModel response, int id) {
                if (response.getCode() == 200) {
                    new QMUIDialog.MessageDialogBuilder(mActivity)
                            .setTitle("新版本")
                            .setMessage("发现有新版本,请更新")
                            .addAction("取消", new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog dialog, int index) {
                                    dialog.dismiss();
                                }
                            }).addAction("确定", new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            InstallUtils.installAPKWithBrower(mActivity,response.getData().getRTN());
                            dialog.dismiss();
                        }
                    })
                            .show();

                }
            }
        });
    }
    @Override
    public int getLayoutResId() {
        return R.layout.activity_main;
    }






    ////////////////////////////////////////页面适配器start//////////////////////////////////////////////
    public class MainFPagerAdaper extends FragmentPagerAdapter {

        public MainFPagerAdaper(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            Fragment retFragment = null;
            switch (position) {
//                case 0:
//                    retFragment = new BasicComponentsFragment();
//                    break;
                case 0:
                    retFragment = new TwoFragment();
                    break;
                case 1:
                    retFragment = new ThrFragment();
                    break;
                case 2:
                    retFragment = new FourFragment();
                    break;
            }
            return retFragment;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
    ////////////////////////////////////////页面适配器end//////////////////////////////////////////////


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                //moveTaskToBack(true);
                if(System.currentTimeMillis() - mLastClickReturnTime > 1000L) {
                    mLastClickReturnTime = System.currentTimeMillis();
                    ToastUtil.showToast("再按一次退出程序");
                    return true;
                }else {
                    BaseActivityStack.getInstance().appExit(mActivity);
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
