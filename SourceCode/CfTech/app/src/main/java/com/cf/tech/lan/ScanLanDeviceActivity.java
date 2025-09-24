package com.cf.tech.lan;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.cf.socket.UdpFounder;
import com.cf.socket.interfaces.IUdpDataCallback;
import com.cf.tech.R;
import com.cf.tech.adapter.ScanLanDeviceAdapter;
import com.cf.tech.base.BaseActivity;
import com.cf.tech.databinding.ActivityScanLanDeviceBinding;

import java.net.DatagramPacket;

public class ScanLanDeviceActivity extends BaseActivity implements IUdpDataCallback, ScanLanDeviceAdapter.IOnItemClickListener {

    private ActivityScanLanDeviceBinding mBinding;
    private ScanLanDeviceAdapter mScanLanDeviceAdapter;

    @Override
    public View setLayout() {
        mBinding = ActivityScanLanDeviceBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    public void init() {
        super.init();

        UdpFounder.get().receiveData(5000);
        UdpFounder.get().setIUdpDataCallback(this);

        mBinding.deviceItemRv.setLayoutManager(new LinearLayoutManager(this));
        mScanLanDeviceAdapter = new ScanLanDeviceAdapter();
        mScanLanDeviceAdapter.setIOnItemClickListener(this);
        mBinding.deviceItemRv.setAdapter(mScanLanDeviceAdapter);
        mBinding.srl.setProgressViewOffset(true, 20, 150);
        mBinding.srl.setColorSchemeResources(R.color.purple, R.color.orange, R.color.yellow);
        mBinding.srl.setOnRefreshListener(this::scanLanDevice);

        //进入界面刷新设备列表逻辑
        mBinding.getRoot().post(() -> {
            mBinding.srl.setRefreshing(true);
            scanLanDevice();
        });
    }

    private void scanLanDevice() {
        mScanLanDeviceAdapter.clearData();
        UdpFounder.get().setIUdpDataCallback(this);
        byte[] cmd = new byte[]{(byte) 0xCF, (byte) 0xFF, 0x00, 0x5F, 0x01, 0x02, 0x34, 0x3E};
        UdpFounder.get().sendBroadcast(cmd, 5000, 5000);
        mBinding.getRoot().postDelayed(() -> {
            mBinding.srl.setRefreshing(false);
            UdpFounder.get().stopSendBroadcast();
            UdpFounder.get().setIUdpDataCallback(null);
            if (mScanLanDeviceAdapter.getItemCount() <= 0) {
                mBinding.scanDeviceTipsTv.setVisibility(View.VISIBLE);
                mBinding.scanDeviceTipsTv.setText("没有发现设备，请拉刷新重试");
            }
        }, 5000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UdpFounder.get().stopReceiveData();
    }

    @Override
    public void onReceiveUdpData(DatagramPacket pPacket) {
        if (pPacket == null) return;
        if (mScanLanDeviceAdapter != null) {
            mBinding.getRoot().post(() -> {
                mBinding.scanDeviceTipsTv.setVisibility(View.GONE);
                mScanLanDeviceAdapter.setData(pPacket);
            });
        }
    }

    @Override
    public void onItemClick(String pIpAddr, int pPort) {
        Bundle bundle = new Bundle();
        bundle.putString("ip", pIpAddr);
        bundle.putInt("port", pPort);
        startActivity(this, LanDeviceActivity.class, bundle);
        finish();
    }
}