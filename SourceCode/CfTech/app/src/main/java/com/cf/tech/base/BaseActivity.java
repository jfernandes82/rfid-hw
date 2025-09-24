package com.cf.tech.base;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cf.tech.CfApplication;
import com.cf.tech.R;
import com.cf.tech.utils.ActivityUtil;


public abstract class BaseActivity extends AppCompatActivity {

    public static void startActivity(Context context, Class<? extends AppCompatActivity> cla) {
        Intent starter = new Intent(context, cla);
        context.startActivity(starter);
    }

    public static void startActivity(Context context, Class<? extends AppCompatActivity> cla, Bundle pBundle) {
        Intent starter = new Intent(context, cla);
        starter.putExtra(cla.getName(), pBundle);
        context.startActivity(starter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(setLayout());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("Hello", "onConfigurationChanged == >" + CfApplication.sCurrentUiMode);
        Log.e("Hello", "onConfigurationChanged == >" + newConfig.uiMode);
        if (CfApplication.sCurrentUiMode != newConfig.uiMode) {
            ActivityUtil.restartToMainActivity(this);
            CfApplication.sCurrentUiMode = newConfig.uiMode;
        }
    }

    public abstract View setLayout();

    public void init() {
        //空实现，有需要的时候子类再重写该方法
    }
}