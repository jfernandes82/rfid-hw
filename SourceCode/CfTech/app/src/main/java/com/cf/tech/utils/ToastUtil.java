package com.cf.tech.utils;

import android.content.Context;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class ToastUtil {

    private static WeakReference<Context> sWeakReference;
    private static long sShowToastTime;


    public static void init(Context pContext) {
        sWeakReference = new WeakReference<>(pContext.getApplicationContext());
    }

    public static void show(String pMsg) {
        show(pMsg, Toast.LENGTH_SHORT);
    }

    public static void show(String pMsg, int pDuration) {
        long timeOut = 0;
        if (pDuration == Toast.LENGTH_SHORT) {
            timeOut = 2000;
        } else if (pDuration == Toast.LENGTH_LONG) {
            timeOut = 5000;
        }
        if (System.currentTimeMillis() - sShowToastTime <= timeOut) return;
        sShowToastTime = System.currentTimeMillis();
        Toast.makeText(sWeakReference.get(), pMsg, Toast.LENGTH_SHORT).show();
        com.cf.zsdk.uitl.LogUtil.d(pMsg);
    }
}
