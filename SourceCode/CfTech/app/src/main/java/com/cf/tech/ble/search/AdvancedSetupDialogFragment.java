package com.cf.tech.ble.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cf.tech.AppC;
import com.cf.zsdk.CfSdk;
import com.cf.zsdk.cmd.CmdBuilder;
import com.cf.zsdk.cmd.CmdType;
import com.cf.zsdk.SdkC;
import com.cf.beans.CmdData;
import com.cf.beans.PermissionParamBean;
import com.cf.ble.interfaces.IOnNotifyCallback;
import com.cf.zsdk.uitl.FormatUtil;
import com.cf.tech.R;
import com.cf.tech.base.BaseDialogFragment;
import com.cf.tech.ble.search.interfaces.IDialogDismissCallback;
import com.cf.tech.databinding.DialogFragmentAdvancedSetupBinding;
import com.cf.tech.etimpl.DataLenFocusChangeListener;
import com.cf.tech.etimpl.DataLenTextWatcher;
import com.cf.tech.etimpl.HexEditTextWatcher;
import com.cf.tech.utils.ToastUtil;

import java.util.Arrays;

public class AdvancedSetupDialogFragment extends BaseDialogFragment implements IOnNotifyCallback {

    private DialogFragmentAdvancedSetupBinding mBinding;
    private PermissionParamBean mPermissionParamBean;
    private IDialogDismissCallback mIDialogDismissCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.dialog_fragment_advanced_setup, container, false);
        mBinding = DialogFragmentAdvancedSetupBinding.bind(inflate);

        mBinding.dismissBtn.setOnClickListener(v -> dismiss());

        return mBinding.getRoot();
    }

    /**
     * 根据argument的数据填充UI
     */
    private void initUiByArgument() {
        Bundle arguments = getArguments();
        if (arguments == null) return;
        mPermissionParamBean = (PermissionParamBean) arguments.getSerializable(getClass().getName());
        if (mPermissionParamBean == null) return;
        mBinding.startAddressEt.setText(String.format("%s", mPermissionParamBean.mStartAdd));
        mBinding.maskLenEt.setText(String.format("%s", mPermissionParamBean.mMaskLen));
        byte maskLen = mPermissionParamBean.mMaskLen;
        if (maskLen == 0) {
            mBinding.maskDataEt.setText("");
        } else {
            byte[] bytes = Arrays.copyOf(mPermissionParamBean.mMaskData, maskLen);
            mBinding.maskDataEt.setText(FormatUtil.bytesToHexStr(bytes).replace(" ", ""));
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //取消按钮
        mBinding.dismissBtn.setOnClickListener(v -> dismiss());

        //掩码
        HexEditTextWatcher maskDataTw = new HexEditTextWatcher(mBinding.maskDataEt);
        mBinding.maskDataEt.addTextChangedListener(maskDataTw);

        //掩码长度
        DataLenFocusChangeListener maskLenFocusChangeListener = new DataLenFocusChangeListener(mBinding.maskLenEt);
        mBinding.maskLenEt.setOnFocusChangeListener(maskLenFocusChangeListener);
        DataLenTextWatcher maskLenTw = new DataLenTextWatcher(mBinding.maskLenEt, 0,31, 2) {
            @Override
            public void lenChangeCall(int pLen) {
                maskDataTw.setLimitLen(pLen);
                if (pLen == 0) mBinding.maskDataEt.setText("");
            }
        };
        mBinding.maskLenEt.addTextChangedListener(maskLenTw);

        //掩码输入框获得焦点
        mBinding.maskDataEt.setOnFocusChangeListener((v1, hasFocus) -> {
            Editable text = mBinding.maskLenEt.getText();
            if (text == null) return;
            if (!hasFocus) return;
            if (TextUtils.isEmpty(text.toString())) {
                ToastUtil.show("请先输入掩码长度");
                return;
            }
            if (Integer.parseInt(text.toString()) <= 0) {
                ToastUtil.show("掩码长度为零");
            }
        });

        //掩码起始位置
        DataLenFocusChangeListener startAddrFocusChangeListener = new DataLenFocusChangeListener(mBinding.startAddressEt);
        mBinding.startAddressEt.setOnFocusChangeListener(startAddrFocusChangeListener);


        //设置参数
        mBinding.setPermissionParamBtn.setOnClickListener(v -> {
            if (mPermissionParamBean == null) {
                ToastUtil.show("参数错误");
                return;
            }
            Editable startAddressEtText = mBinding.startAddressEt.getText();
            String startAddrStr = !TextUtils.isEmpty(startAddressEtText) ? startAddressEtText.toString() : "0";
            mPermissionParamBean.mStartAdd = (byte) Integer.parseInt(startAddrStr);

            Editable maskLenEtText = mBinding.maskLenEt.getText();
            String maskLenStr = !TextUtils.isEmpty(maskLenEtText) ? maskLenEtText.toString() : "0";
            int parseInt = Integer.parseInt(maskLenStr);
            mPermissionParamBean.mMaskLen = (byte) (Math.min(parseInt, 31));

            Editable maskDataEtText = mBinding.maskDataEt.getText();
            String maskDataEtStr = !TextUtils.isEmpty(maskDataEtText) ? maskDataEtText.toString() : "00000000000000000000000000000000000000000000000000000000000000";
            if (maskDataEtStr.length() / 2 != 31) {
                StringBuilder builder = new StringBuilder(maskDataEtStr);
                for (int i = 0; i < 62; i++) {
                    builder.append("0");
                    if (builder.length() == 62) break;
                }
            }
            mPermissionParamBean.mMaskData = FormatUtil.hexStrToByteArray(maskDataEtStr);

            byte[] bytes = CmdBuilder.buildSetPermissionParamCmd(mPermissionParamBean);
            CfSdk.get(SdkC.BLE).writeData(AppC.SERVICE_UUID, AppC.WRITE_UUID, bytes);
        });

        //初始化界面
        initUiByArgument();
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
        if (mIDialogDismissCallback != null) {
            mIDialogDismissCallback.onDialogDismiss();
            mIDialogDismissCallback = null;
        }
    }

    @Override
    public void onNotify(int pCmdType, CmdData pCmdData) {
        if (pCmdType != CmdType.TYPE_GET_OR_SET_PERMISSION) return;
        PermissionParamBean bean = (PermissionParamBean) pCmdData.getData();
        if (bean.mStatus == SdkC.STATUS_CODE_SUCCEED) {
            mBinding.getRoot().post(() -> {
                ToastUtil.show(bean.mMsg);
                dismiss();
            });
        } else {
            mBinding.getRoot().post(() -> ToastUtil.show(bean.mMsg));
        }
    }

    public void setIFragmentStateChangeCallback(IDialogDismissCallback pDismissCallback) {
        mIDialogDismissCallback = pDismissCallback;
    }
}
