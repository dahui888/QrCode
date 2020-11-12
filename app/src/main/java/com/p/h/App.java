package com.p.h;

import android.app.Application;

import com.uuzuche.lib_zxing.activity.ZXingLibrary;


public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //初始化ZXing
        ZXingLibrary.initDisplayOpinion(this);
    }
}
