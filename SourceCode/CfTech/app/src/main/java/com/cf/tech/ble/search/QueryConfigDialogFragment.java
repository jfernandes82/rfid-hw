package com.cf.tech.ble.search;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cf.tech.AppC;
import com.cf.zsdk.CfSdk;
import com.cf.zsdk.SdkC;
import com.cf.zsdk.cmd.CmdBuilder;
import com.cf.zsdk.cmd.CmdType;
import com.cf.beans.AllParamBean;
import com.cf.beans.CmdData;
import com.cf.beans.GeneralBean;
import com.cf.ble.interfaces.IOnNotifyCallback;
import com.cf.zsdk.uitl.LogUtil;
import com.cf.tech.R;
import com.cf.tech.base.BaseDialogFragment;
import com.cf.tech.ble.search.interfaces.IDialogDismissCallback;
import com.cf.tech.databinding.DialogFragmentQueryConfigBinding;
import com.cf.tech.etimpl.DataLenFocusChangeListener;
import com.cf.tech.utils.ToastUtil;

import java.util.Objects;

public class QueryConfigDialogFragment extends BaseDialogFragment implements IOnNotifyCallback {

    private DialogFragmentQueryConfigBinding mBinding;
    private AllParamBean mAllParamBean;
    private IDialogDismissCallback pCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DialogFragmentQueryConfigBinding.inflate(inflater);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mAllParamBean = (AllParamBean) arguments.getSerializable(getClass().getName());
            if (mAllParamBean != null) {
                //存储区域
                byte inquiryArea = mAllParamBean.mInquiryArea;
                switch (inquiryArea) {
                    case 0x00:
                        // TODO: 2024-7-23 保留字段
                        break;
                    case 0x01:
                        //EPC存储区
                        mBinding.epcRb.setChecked(true);
                        break;
                    case 0x02:
                        //TID存储区
                        mBinding.tidRb.setChecked(true);
                        break;
                    case 0x03:
                        //USER存储区
                        mBinding.userRb.setChecked(true);
                        break;
                    case 0x04:
                        //EPC+TID
                        break;
                    case 0x05:
                        //EPC+USER
                        break;
                    case 0x06:
                        //EPC+TID+USER
                        break;
                    default:
                        LogUtil.d("未知的Inquiry Area");
                        break;
                }

                //查询间隔时间
                mBinding.pollingIntervalEt.setText(String.valueOf(mAllParamBean.mPollingInterval & 0xff));
                DataLenFocusChangeListener pollingIntervalFocusChangeListener = new DataLenFocusChangeListener(mBinding.pollingIntervalEt);
                mBinding.pollingIntervalEt.setOnFocusChangeListener(pollingIntervalFocusChangeListener);

                //起始地址
                mBinding.acsAddrEt.setText(String.valueOf(mAllParamBean.mAcsAddr & 0xff));
                DataLenFocusChangeListener acsAddrFocusChangeListener = new DataLenFocusChangeListener(mBinding.acsAddrEt);
                mBinding.acsAddrEt.setOnFocusChangeListener(acsAddrFocusChangeListener);

                //字节长度
                mBinding.acsDataLenEt.setText(String.valueOf(mAllParamBean.mAcsDataLen & 0xff));
                DataLenFocusChangeListener acsDataFocusChangeListener = new DataLenFocusChangeListener(mBinding.acsDataLenEt);
                mBinding.acsDataLenEt.setOnFocusChangeListener(acsDataFocusChangeListener);

                //过滤时间
                mBinding.filterTimeEt.setText(String.valueOf(mAllParamBean.mFilterTime & 0xff));
                DataLenFocusChangeListener filterTimeFocusChangeListener = new DataLenFocusChangeListener(mBinding.filterTimeEt);
                mBinding.filterTimeEt.setOnFocusChangeListener(filterTimeFocusChangeListener);

            }
        }


        //设置配置
        mBinding.setConfig.setOnClickListener(v -> {

            int checkedRadioButtonId = mBinding.storageRegionRg.getCheckedRadioButtonId();
            if (checkedRadioButtonId == R.id.epc_rb) {
                mAllParamBean.mInquiryArea = 0x001;
            } else if (checkedRadioButtonId == R.id.tid_rb) {
                mAllParamBean.mInquiryArea = 0x002;
            } else if (checkedRadioButtonId == R.id.user_rb) {
                mAllParamBean.mInquiryArea = 0x003;
            }

            String pollingIntervalStr = Objects.requireNonNull(mBinding.pollingIntervalEt.getText()).toString();
            if (!TextUtils.isEmpty(pollingIntervalStr)) {
                int parseInt = Integer.parseInt(pollingIntervalStr);
                mAllParamBean.mPollingInterval = (byte) (Math.min(parseInt, 25));
            } else {
                mAllParamBean.mPollingInterval = 0;
            }

            String acsAddrStr = Objects.requireNonNull(mBinding.acsAddrEt.getText()).toString();
            if (!TextUtils.isEmpty(acsAddrStr)) {
                int parseInt = Integer.parseInt(acsAddrStr);
                mAllParamBean.mAcsAddr = (byte) (Math.min(parseInt, 63));
            } else {
                mAllParamBean.mAcsAddr = 0;
            }

            String acsDataLenStr = Objects.requireNonNull(mBinding.acsDataLenEt.getText()).toString();
            if (!TextUtils.isEmpty(acsDataLenStr)) {
                mAllParamBean.mAcsDataLen = (byte) Integer.parseInt(acsDataLenStr);
            } else {
                mAllParamBean.mAcsDataLen = 0;
            }

            String filterTimeStr = Objects.requireNonNull(mBinding.filterTimeEt.getText()).toString();
            if (!TextUtils.isEmpty(filterTimeStr)) {
                int parseInt = Integer.parseInt(filterTimeStr);
                mAllParamBean.mFilterTime = (byte) (Math.min(parseInt, 255));
            } else {
                mAllParamBean.mFilterTime = 0;
            }

            byte[] bytes = CmdBuilder.buildSetAllParamCmd(mAllParamBean);
            CfSdk.get(SdkC.BLE).writeData(AppC.SERVICE_UUID, AppC.WRITE_UUID, bytes);
        });

        mBinding.dismissBtn.setOnClickListener(v -> dismiss());
    }

    public void setIFragmentStateChangeCallback(IDialogDismissCallback pPCallback) {
        pCallback = pPCallback;
    }

    @Override
    public void onResume() {
        super.onResume();
        CfSdk.get(SdkC.BLE).setOnNotifyCallback(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        CfSdk.get(SdkC.BLE).setOnNotifyCallback(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pCallback != null) {
            pCallback.onDialogDismiss();
            pCallback = null;
        }
    }

    @Override
    public void onNotify(int pCmdType, CmdData pCmdData) {
        if (pCmdType == CmdType.TYPE_SET_ALL_PARAM) {
            GeneralBean generalBean = (GeneralBean) pCmdData.getData();
            mBinding.getRoot().post(() -> ToastUtil.show(generalBean.mMsg));
            if (generalBean.mStatus == SdkC.STATUS_CODE_SUCCEED) {
                dismiss();
            }
        }
    }

}
