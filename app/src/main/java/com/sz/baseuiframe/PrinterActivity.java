package com.sz.baseuiframe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cheung.android.base.baseuiframe.activity.BaseUIActivity;

public class PrinterActivity extends BaseUIActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_printer;
    }
}
