package com.cf.tech.ble.tag_operation;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.cf.tech.AppC;
import com.cf.zsdk.CfSdk;
import com.cf.zsdk.SdkC;
import com.cf.zsdk.cmd.CmdBuilder;
import com.cf.zsdk.cmd.CmdType;
import com.cf.beans.CmdData;
import com.cf.beans.GeneralBean;
import com.cf.beans.TagInfoBean;
import com.cf.ble.interfaces.IOnNotifyCallback;
import com.cf.zsdk.uitl.FormatUtil;
import com.cf.tech.LoadingFragment;
import com.cf.tech.R;
import com.cf.tech.adapter.TagOperationAdapter;
import com.cf.tech.base.BaseActivity;
import com.cf.tech.ble.search.events.OperationEvent;
import com.cf.tech.ble.search.events.SelectTagEvent;
import com.cf.tech.databinding.ActivityTagOperationBinding;
import com.cf.tech.utils.ToastUtil;
import com.google.android.material.tabs.TabLayoutMediator;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class TagOperationActivity extends BaseActivity implements IOnNotifyCallback {

    private ActivityTagOperationBinding mBinding;
    private byte[] mCurrentMask;

    @Override
    public View setLayout() {
        mBinding = ActivityTagOperationBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    public void init() {
        super.init();

        Bundle bundleExtra = getIntent().getBundleExtra(getClass().getName());
        if (bundleExtra != null) {
            TagInfoBean tagInfoBean = (TagInfoBean) bundleExtra.getSerializable("data");
            if (tagInfoBean != null) {
                mCurrentMask = tagInfoBean.mEPCNum;
                String mCurrentMaskHexStr = FormatUtil.bytesToHexStr(mCurrentMask);
                mBinding.currentTagTv.setText(String.format("%s", mCurrentMaskHexStr));
            }
        }

        if (mCurrentMask != null) {
            //发送选定标签指令,通过post的方式发送指令，以免过快发送
            mBinding.getRoot().post(() -> {
                byte[] bytes = CmdBuilder.buildSelectMaskCmd(mCurrentMask);
                CfSdk.get(SdkC.BLE).writeData(AppC.SERVICE_UUID, AppC.WRITE_UUID, bytes);
                CfSdk.get(SdkC.BLE).setOnNotifyCallback(TagOperationActivity.this);
                LoadingFragment.showLoading(getSupportFragmentManager(), "选定标签中…");
                //超时动作
                LoadingFragment.dismissLoadingTimeOut(5000, "选定标签超时");
            });
        }

        //取消vp2的滑动手势
        mBinding.vp2.setUserInputEnabled(false);
        //将状态保存设置为1，即同时创建1个fragment
        mBinding.vp2.setOffscreenPageLimit(1);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new ReadTagTagOperationFragment());
        fragments.add(new LockTagTagOperationFragment());
        fragments.add(new KillTagTagOperationFragment());

        String[] tagOperationLabels = getResources().getStringArray(R.array.tag_operation_label_value);
        int[] tagOperationIcons = new int[]{R.drawable.ic_read_tag, R.drawable.ic_lock_tag, R.drawable.ic_kill_tag};

        TagOperationAdapter tagOperationAdapter = new TagOperationAdapter(this, fragments);
        mBinding.vp2.setAdapter(tagOperationAdapter);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(mBinding.tabLayout, mBinding.vp2, (tab, position) -> {
            tab.setId(position);
            tab.setText(tagOperationLabels[position]);
            tab.setIcon(tagOperationIcons[position]);
        });
        tabLayoutMediator.attach();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CfSdk.get(SdkC.BLE).setOnNotifyCallback(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CfSdk.get(SdkC.BLE).setOnNotifyCallback(null);
    }

    @Override
    public void onNotify(int pCmdType, CmdData pCmdData) {
        switch (pCmdType) {
            case CmdType.TYPE_READ_TAG:
            case CmdType.TYPE_WRITE_TAG:
            case CmdType.TYPE_LOCK_TAG:
            case CmdType.TYPE_KILL_TAG:
            case CmdType.TYPE_GET_ALL_PARAM:
                OperationEvent operationEvent1 = new OperationEvent(pCmdType, pCmdData);
                EventBus.getDefault().post(operationEvent1);
                break;
            case CmdType.TYPE_SELECT_MASK:
                GeneralBean bean = (GeneralBean) pCmdData.getData();
                if (bean.mStatus != 0) {
                    ToastUtil.show(bean.mMsg);
                    return;
                }
                EventBus.getDefault().postSticky(new SelectTagEvent());
                LoadingFragment.dismissLoading();
                break;
            default:
                break;
        }
    }
}