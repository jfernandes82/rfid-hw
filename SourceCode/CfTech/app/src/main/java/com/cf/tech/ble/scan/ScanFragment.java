package com.cf.tech.ble.scan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cf.beans.CmdData;
import com.cf.tech.AppC;
import com.cf.zsdk.CfSdk;
import com.cf.zsdk.SdkC;
import com.cf.zsdk.cmd.CmdBuilder;
import com.cf.zsdk.cmd.CmdType;
import com.cf.beans.TagInfoBean;
import com.cf.ble.interfaces.IOnNotifyCallback;
import com.cf.zsdk.uitl.FormatUtil;
import com.cf.tech.R;
import com.cf.tech.adapter.ScanDataAdapter;
import com.cf.tech.databinding.FragmentScanBinding;

import java.util.Locale;


public class ScanFragment extends Fragment implements IOnNotifyCallback {

    private FragmentScanBinding mBinding;
    private ScanDataAdapter mScanDataAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ScanViewModel scanViewModel = new ViewModelProvider(this).get(ScanViewModel.class);

        mBinding = FragmentScanBinding.inflate(inflater, container, false);
        View root = mBinding.getRoot();

        final TextView textView = mBinding.textDashboard;
        scanViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.scanDataRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mScanDataAdapter = new ScanDataAdapter();
        mBinding.scanDataRv.setAdapter(mScanDataAdapter);

        mBinding.startScanBtn.setOnClickListener(v -> {
            byte[] bytes = CmdBuilder.buildInventoryISOContinueCmd((byte) 0x01, 0x01);
            CfSdk.get(SdkC.BLE).writeData(AppC.SERVICE_UUID, AppC.WRITE_UUID, bytes);
        });

        mBinding.clearBtn.setOnClickListener(v -> {
            mBinding.tagTotalCountTv.setText("0");
            mBinding.queryTotalCountTv.setText("0");
            if (mScanDataAdapter != null) {
                mScanDataAdapter.clearData();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //注册数据回调
        CfSdk.get(SdkC.BLE).setOnNotifyCallback(this);
        //将模式切换为扫描模式
        byte[] bytes = CmdBuilder.buildSetReadModeCmd((byte) 0x01, new byte[7]);
        CfSdk.get(SdkC.BLE).writeData(AppC.SERVICE_UUID, AppC.WRITE_UUID, bytes);
    }

    @Override
    public void onPause() {
        super.onPause();
        //将设备切换为RFID模式
        byte[] bytes = CmdBuilder.buildSetReadModeCmd((byte) 0x00, new byte[7]);
        CfSdk.get(SdkC.BLE).writeData(AppC.SERVICE_UUID, AppC.WRITE_UUID, bytes);
        CfSdk.get(SdkC.BLE).setOnNotifyCallback(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }



    @Override
    public void onNotify(int pCmdType, CmdData pCmdData) {
        if (pCmdType == CmdType.TYPE_INVENTORY) {
            TagInfoBean tagInfoBean = (TagInfoBean) pCmdData.getData();
            if (mScanDataAdapter != null && tagInfoBean != null) {
                byte[] epcNum = tagInfoBean.mEPCNum;
                int checkedRadioButtonId = mBinding.charsetRg.getCheckedRadioButtonId();
                String scanData;
                if (checkedRadioButtonId == R.id.hex_rb) {
                    scanData = FormatUtil.bytesToHexStr(epcNum);
                } else {
                    scanData = new String(epcNum);
                }
                mBinding.getRoot().post(() -> {
                    mBinding.scanDataTv.setVisibility(View.GONE);
                    mScanDataAdapter.setData(scanData);
                    //标签总数
                    mBinding.tagTotalCountTv.setText(String.format(Locale.getDefault(), "%d", mScanDataAdapter.getItemCount()));
                    //查询总数
                    int count = Integer.parseInt((String) mBinding.queryTotalCountTv.getText()) + 1;
                    mBinding.queryTotalCountTv.setText(String.format(Locale.getDefault(), "%d", count));
                    mBinding.scanDataRv.scrollToPosition(mScanDataAdapter.getItemCount() - 1);
                });
            }
        }
    }
}