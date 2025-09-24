package com.cf.tech.ble.tag_operation;

import android.os.Bundle;
import android.util.Log;
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
import com.cf.beans.TagOperationBean;
import com.cf.zsdk.uitl.FormatUtil;
import com.cf.tech.R;
import com.cf.tech.base.BaseTagOperaitonFragment;
import com.cf.tech.ble.search.events.OperationEvent;
import com.cf.tech.databinding.FragmentKillTagBinding;
import com.cf.tech.etimpl.HexEditTextWatcher;
import com.cf.tech.utils.ToastUtil;

public class KillTagTagOperationFragment extends BaseTagOperaitonFragment {

    private FragmentKillTagBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kill_tag, container, false);
        mBinding = FragmentKillTagBinding.bind(view);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HexEditTextWatcher killTagPwTw = new HexEditTextWatcher(mBinding.killTagPwEt, 8);
        mBinding.killTagPwEt.addTextChangedListener(killTagPwTw);

        mBinding.killTagBtn.setOnClickListener(v -> {
            String killTagPwStr = mBinding.killTagPwEt.getText().toString();
            byte[] killTagPwBytes = FormatUtil.hexStrToByteArray(killTagPwStr);
            byte[] killTagPwCmd = CmdBuilder.buildKlenlISOTagCmd(killTagPwBytes);
            CfSdk.get(SdkC.BLE).writeData(AppC.SERVICE_UUID, AppC.WRITE_UUID, killTagPwCmd);
        });
    }

    @Override
    public void onOperationNotify(OperationEvent pEvent) {
        super.onOperationNotify(pEvent);
        if (pEvent.mCmdType != CmdType.TYPE_KILL_TAG) {
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
        Log.e("Hello", "KillTagTagOperationFragment onDestroyView == > call");
        mBinding = null;
    }
}