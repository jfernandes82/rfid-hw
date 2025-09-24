package com.cf.tech.lan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.cf.socket.interfaces.ISocketDataCallback;
import com.cf.tech.CfApplication;
import com.cf.tech.adapter.LanDeviceDataAdapter;
import com.cf.tech.base.BaseActivity;
import com.cf.tech.databinding.ActivityLanDeviceBinding;
import com.cf.tech.utils.ToastUtil;
import com.cf.zsdk.CfSdk;
import com.cf.zsdk.IpCore;
import com.cf.zsdk.SdkC;
import com.cf.zsdk.cmd.CmdBuilder;
import com.cf.zsdk.uitl.FormatUtil;

public class LanDeviceActivity extends BaseActivity implements ISocketDataCallback {


    private ActivityLanDeviceBinding mBinding;
    private LanDeviceDataAdapter mLanDeviceDataAdapter;
    private IpCore mIpCore;

    @Override
    public View setLayout() {
        mBinding = ActivityLanDeviceBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    public void init() {
        super.init();

        mIpCore = CfSdk.get(SdkC.IP_CONN);

        Intent intent = getIntent();
        Bundle bundleExtra = intent.getBundleExtra(LanDeviceActivity.class.getName());
        if (bundleExtra == null) return;
        String ipAddr = bundleExtra.getString("ip");
        int port = bundleExtra.getInt("port");
        CfApplication.sCtp.submit(() -> {
            String msg;
            if (mIpCore.connect(ipAddr, port)) {
                mIpCore.receiveData(LanDeviceActivity.this);
                msg = "设备连接成功";
            } else {
                msg = "设备连接失败";
            }
            mBinding.getRoot().post(() -> ToastUtil.show(msg));
        });

        //设置适配器
        mLanDeviceDataAdapter = new LanDeviceDataAdapter();
        mBinding.lanDeviceDataRv.setLayoutManager(new LinearLayoutManager(this));
        mBinding.lanDeviceDataRv.setAdapter(mLanDeviceDataAdapter);
        mBinding.lanDeviceDataRv.setItemAnimator(null);

        mBinding.startInventoryBtn.setOnClickListener(v -> {
            CfApplication.sCtp.submit(() -> {
                byte[] bytes = CmdBuilder.buildInventoryISOContinueCmd((byte) 0, 0);
                if (mIpCore.writeData(bytes)) {
                    ToastUtil.show("数据发送成功");
                } else {
                    ToastUtil.show("数据发送失败");
                }
            });
        });

        mBinding.stopInventoryBtn.setOnClickListener(v -> {
            CfApplication.sCtp.submit(() -> {
                byte[] bytes = CmdBuilder.buildStopInventoryCmd();
                if (mIpCore.writeData(bytes)) {
                    ToastUtil.show("数据发送成功");
                } else {
                    ToastUtil.show("数据发送失败");
                }
            });
        });

        mBinding.clearDataBtn.setOnClickListener(v -> {
            mBinding.lanDeviceDataTipsTv.setVisibility(View.VISIBLE);
            mLanDeviceDataAdapter.clearData();
        });

        mBinding.dataCharsetSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handleCmdCharsetWhenItemChange(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mBinding.sendDataBtn.setOnClickListener(v -> {
            String cmdData = mBinding.cmdDataEt.getText().toString();
            if (TextUtils.isEmpty(cmdData)) {
                ToastUtil.show("请输入数据");
                return;
            }
            String charset = mBinding.dataCharsetSp.getSelectedItem().toString();
            byte[] bytes;
            if (charset.equals("HEX")) {
                cmdData = cmdData.replace(" ", "").trim();
                bytes = FormatUtil.hexStrToByteArray(cmdData);
            } else {
                bytes = cmdData.getBytes();
            }
            CfApplication.sCtp.submit(() -> {
                mIpCore.writeData(bytes);
            });
        });
    }

    /**
     * 当字符集改变，处理字符集的内容
     *
     * @param position
     */
    private void handleCmdCharsetWhenItemChange(int position) {
        String string = mBinding.cmdDataEt.getText().toString();
        if (TextUtils.isEmpty(string)) return;
        String item = (String) mBinding.dataCharsetSp.getItemAtPosition(position);
        if (item.equals("HEX")) {
            byte[] bytes = string.getBytes();
            StringBuilder builder = new StringBuilder();
            for (byte pByte : bytes) {
                String format = String.format("%02X", pByte);
                builder.append(format).append(" ");
            }
            String trim = builder.toString().trim();
            handleEtStation(trim);
        } else {
            String replace = string.replace(" ", "");
            byte[] bytes = FormatUtil.hexStrToByteArray(replace);
            string = new String(bytes);
            handleEtStation(string);
        }
    }

    /**
     * 处理一下光标位置
     *
     * @param string
     */
    private void handleEtStation(String string) {
        mBinding.cmdDataEt.setText(string);
        mBinding.cmdDataEt.setSelection(string.length());
        mBinding.cmdDataEt.clearFocus();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIpCore != null) {
            mIpCore.disConnect();
        }
    }

    @Override
    public void onDataCallback(byte[] pBytes) {
        if (pBytes == null || mLanDeviceDataAdapter == null) return;
        mBinding.getRoot().post(() -> {
            mBinding.lanDeviceDataTipsTv.setVisibility(View.GONE);
            mLanDeviceDataAdapter.setData(pBytes);
            mBinding.lanDeviceDataRv.scrollToPosition(mLanDeviceDataAdapter.getItemCount() - 1);
        });
    }
}