package com.cf.sdk_demo;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {


    private static Toast sToast;

    public static void init(Context pContext) {
        if (sToast == null) {
            sToast = new Toast(pContext.getApplicationContext());
            sToast.setDuration(Toast.LENGTH_SHORT);
        }
    }

    public static void show(String msg) {
        sToast.setText(msg);
        sToast.show();
    }

    public void release() {
        sToast.cancel();
        sToast = null;
    }

}
