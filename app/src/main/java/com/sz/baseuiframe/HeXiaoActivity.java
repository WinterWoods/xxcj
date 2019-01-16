package com.sz.baseuiframe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cheung.android.base.baseuiframe.activity.BaseUIActivity;
import com.qmuiteam.qmui.widget.QMUITopBar;

import butterknife.BindView;

public class HeXiaoActivity extends BaseUIActivity {
    @BindView(R.id.topbar)
    QMUITopBar topBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topBar.setTitle("快速核销");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_he_xiao;
    }
}
