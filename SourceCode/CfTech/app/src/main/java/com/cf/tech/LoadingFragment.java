package com.cf.tech;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.cf.zsdk.uitl.LogUtil;
import com.cf.tech.utils.ToastUtil;

public class LoadingFragment extends DialogFragment {

    private static LoadingFragment sLoadingFragment;

    private static Handler sHandler;

    private static TimeOutRunnable sRunnable;

    public static void showLoading(FragmentManager pFragmentManager) {
        showLoading(pFragmentManager, "");
    }

    public static void showLoading(FragmentManager pFragmentManager, String pMsg) {
        sLoadingFragment = new LoadingFragment();
        sRunnable = new TimeOutRunnable();
        sHandler = new Handler(Looper.getMainLooper());
        Bundle bundle = new Bundle();
        bundle.putString(LoadingFragment.class.getName(), pMsg);
        sLoadingFragment.setArguments(bundle);
        sLoadingFragment.show(pFragmentManager, LoadingFragment.class.getName());
    }

    public static void dismissLoading() {
        dismissLoadingTimeOut(0, "");
    }

    public static void dismissLoadingTimeOut(long pDelay, String pTimeOutMsg) {
        LogUtil.e("Hello", "LoadingFragment.dismissLoadingTimeOut == > " + (sRunnable == null));
        if (sRunnable != null) {
            sRunnable.setMsg(pTimeOutMsg);
            sHandler.postDelayed(sRunnable, pDelay);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_loading, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView loadingTipsTv = view.findViewById(R.id.loading_tips_tv);
        Bundle arguments = getArguments();
        if (arguments != null) {
            String msg = arguments.getString(LoadingFragment.class.getName());
            loadingTipsTv.setText(msg);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog == null) return;
        Window window = dialog.getWindow();
        if (window == null) return;
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = WindowManager.LayoutParams.WRAP_CONTENT;
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(attributes);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sLoadingFragment = null;
        if (sHandler != null) {
            sHandler.removeCallbacksAndMessages(null);
            sHandler = null;
        }
        sRunnable = null;
    }

    private static class TimeOutRunnable implements Runnable {

        private String mMsg;

        public void setMsg(String pMsg) {
            mMsg = pMsg;
        }

        @Override
        public void run() {
            if (!TextUtils.isEmpty(mMsg)) {
                ToastUtil.show(mMsg);
            }
            if (sLoadingFragment != null) {
                sLoadingFragment.dismissAllowingStateLoss();
            }
        }
    }
}
