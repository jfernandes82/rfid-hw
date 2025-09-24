package com.cf.tech.ble.tag_operation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cf.tech.AppC;
import com.cf.zsdk.CfSdk;
import com.cf.zsdk.SdkC;
import com.cf.zsdk.cmd.CmdBuilder;
import com.cf.zsdk.cmd.CmdType;
import com.cf.beans.TagOperationBean;
import com.cf.zsdk.uitl.FormatUtil;
import com.cf.tech.R;
import com.cf.tech.base.BaseTagOperaitonFragment;
import com.cf.tech.ble.search.events.OperationEvent;
import com.cf.tech.databinding.FragmentLockTagBinding;
import com.cf.tech.etimpl.HexEditTextWatcher;
import com.cf.tech.utils.ToastUtil;

public class LockTagTagOperationFragment extends BaseTagOperaitonFragment {

    private com.cf.tech.databinding.FragmentLockTagBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lock_tag, container, false);
        mBinding = FragmentLockTagBinding.bind(view);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HexEditTextWatcher accpWdTw = new HexEditTextWatcher(mBinding.accPwdEt, 8);
        mBinding.accPwdEt.addTextChangedListener(accpWdTw);

        mBinding.lockBtn.setOnClickListener(v -> {
            //acc pwd
            String accPwdEtStr = mBinding.accPwdEt.getText().toString();
            byte[] accPwdBytes = FormatUtil.hexStrToByteArray(accPwdEtStr);

            //area
            int checkedRadioButtonId = mBinding.areaRg.getCheckedRadioButtonId();
            RadioButton areaRb = mBinding.areaRg.findViewById(checkedRadioButtonId);
            String areaCode = areaRb.getHint().toString();
            int area = Integer.parseInt(areaCode, 16);

            //action
            int checkedRadioButtonId1 = mBinding.actionRg.getCheckedRadioButtonId();
            RadioButton actionRb = mBinding.actionRg.findViewById(checkedRadioButtonId1);
            String actionCode = actionRb.getHint().toString();
            int action = Integer.parseInt(actionCode, 16);

            byte[] cmd = CmdBuilder.buildLockISOTagCmd(accPwdBytes, (byte) area, (byte) action);
            CfSdk.get(SdkC.BLE).writeData(AppC.SERVICE_UUID, AppC.WRITE_UUID, cmd);
        });
    }

    @Override
    public void onOperationNotify(OperationEvent pEvent) {
        super.onOperationNotify(pEvent);
        if (pEvent.mCmdType != CmdType.TYPE_LOCK_TAG) {
            return;
        }
        TagOperationBean bean = (TagOperationBean) pEvent.mCmdData.getData();
        if (bean.mStatus == 0x12) return;
        if (bean.mStatus == 0x00) {
            mBinding.errorMsgTv.setVisibility(View.GONE);
            ToastUtil.show(bean.mMsg);
        } else {
            mBinding.errorMsgTv.setVisibility(View.VISIBLE);
            mBinding.errorMsgTv.setText(bean.mMsg);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}