package com.cf.tech;

import android.app.Application;

import com.cf.tech.utils.ToastUtil;
import com.cf.zsdk.CfSdk;
import com.cf.zsdk.uitl.LogUtil;
import com.tencent.mmkv.MMKV;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CfApplication extends Application {

    public static ExecutorService sCtp;
    public static int sCurrentUiMode;

    @Override
    public void onCreate() {
        super.onCreate();
        CfSdk.load(sCtp = Executors.newCachedThreadPool());
        MMKV.initialize(this);
        sCurrentUiMode = getResources().getConfiguration().uiMode;
        //调试版本打开日志输出，正式版本关闭日志输出
        LogUtil.setLogSwitch(true);
        //Toast初始化
        ToastUtil.init(this);
    }
}
