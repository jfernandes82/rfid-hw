package com.cf.tech.utils;

import android.content.Context;
import android.content.Intent;

import com.cf.tech.MainActivity;

public class ActivityUtil {

    public static void restartToMainActivity(Context pContext) {
        Intent intent = new Intent(pContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pContext.startActivity(intent);
    }

}
