package com.cf.tech.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cf.tech.ble.search.events.OperationEvent;
import com.cf.tech.ble.search.events.SelectTagEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class BaseTagOperaitonFragment extends Fragment {


    private View mRoot;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRoot = view;
        enableAllView(mRoot, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().removeStickyEvent(this);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onSelectTagDone(SelectTagEvent pEvent) {
        //保证页面加载完再操作界面
        new Handler(Looper.getMainLooper()).post(() -> enableAllView(mRoot, true));
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOperationNotify(OperationEvent pEvent) {

    }

    protected void enableAllView(View pView, boolean pEnable) {
        if (pView instanceof ViewGroup) {
            int childCount = ((ViewGroup) pView).getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = ((ViewGroup) pView).getChildAt(i);
                if (childAt instanceof ViewGroup) {
                    enableAllView(childAt, pEnable);
                } else {
                    childAt.setEnabled(pEnable);
                }
            }
        } else {
            pView.setEnabled(pEnable);
        }
    }

}
