package com.cf.tech.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.le.ScanRecord;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cf.beans.CmdData;
import com.cf.beans.DeviceInfoBean;
import com.cf.ble.BleUtil;
import com.cf.ble.interfaces.IConnectDoneCallback;
import com.cf.ble.interfaces.IOnNotifyCallback;
import com.cf.tech.AppC;
import com.cf.tech.LoadingFragment;
import com.cf.tech.R;
import com.cf.tech.adapter.BtDeviceListAdapter;
import com.cf.tech.base.BaseActivity;
import com.cf.tech.databinding.ActivityScanBleBinding;
import com.cf.tech.utils.ToastUtil;
import com.cf.zsdk.CfSdk;
import com.cf.zsdk.SdkC;
import com.cf.zsdk.cmd.CmdBuilder;
import com.cf.zsdk.cmd.CmdType;

@SuppressLint("MissingPermission")
public class ScanBleActivity extends BaseActivity implements BtDeviceListAdapter.OnItemClickListener, IConnectDoneCallback, IOnNotifyCallback {

    private ActivityScanBleBinding mBinding;
    private BtDeviceListAdapter mBtDeviceListAdapter;
    private BluetoothGatt mBluetoothGatt;

    @Override
    public View setLayout() {
        mBinding = ActivityScanBleBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    public void init() {
        super.init();

        CfSdk.get(SdkC.BLE).init(this);

        //检查有没有蓝牙模块
        if (!CfSdk.get(SdkC.BLE).isSupportBt()) {
            ToastUtil.show("该设备不支持蓝牙");
            mBinding.getRoot().post(this::finish);
            return;
        }

        //检查有没有打开蓝牙
        if (!CfSdk.get(SdkC.BLE).isEnabled()) {
            //弹窗提示打开蓝牙
            AlertDialog alertDialog = new AlertDialog.Builder(this).setCancelable(false).setMessage("蓝牙没有打开，请先打开蓝牙").setNegativeButton("取消", null).setPositiveButton("去打开蓝牙", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                    startActivity(intent);
                }
            }).create();
            alertDialog.show();
        }

        mBinding.btDeviceRv.setLayoutManager(new LinearLayoutManager(this));
        mBtDeviceListAdapter = new BtDeviceListAdapter();
        mBinding.btDeviceRv.setAdapter(mBtDeviceListAdapter);
        mBinding.swipeRefresh.setProgressViewOffset(true, 20, 150);
        mBinding.swipeRefresh.setColorSchemeResources(R.color.purple, R.color.orange, R.color.yellow);
        mBinding.swipeRefresh.setOnRefreshListener(() -> {
            if (CfSdk.get(SdkC.BLE).isEnabled()) {
                doScanBleDevice();
            } else {
                mBinding.swipeRefresh.setRefreshing(false);
                ToastUtil.show("请先打开蓝牙");
            }
        });

        mBinding.getRoot().post(() -> {
            if (CfSdk.get(SdkC.BLE).isEnabled()) {
                mBinding.swipeRefresh.setRefreshing(true);
                doScanBleDevice();
            }
        });
    }

    private void doScanBleDevice() {
        if (CfSdk.get(SdkC.BLE).isConnect()) {
            CfSdk.get(SdkC.BLE).disconnectedDevice();
        }
        mBtDeviceListAdapter.clearData();
        tipsTvControl(View.GONE, "");

        CfSdk.get(SdkC.BLE).startScan(pResult -> {
            ScanRecord scanRecord = pResult.getScanRecord();
            if (scanRecord == null || mBtDeviceListAdapter == null) return;
            mBinding.getRoot().post(() -> {
                if (BleUtil.isCfDevice(scanRecord.getBytes())) {
                    mBtDeviceListAdapter.setDataForCf(pResult.getDevice());
                } else {
                    mBtDeviceListAdapter.setData(pResult.getDevice());
                }
            });
        });

        mBinding.getRoot().postDelayed(() -> {
            mBinding.swipeRefresh.setRefreshing(false);
            if (mBtDeviceListAdapter.getData().isEmpty()) {
                tipsTvControl(View.VISIBLE, "没有发现蓝牙信息，请重试");
            }
            CfSdk.get(SdkC.BLE).stopScan();
        }, 5000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBtDeviceListAdapter.setOnItemClickListener(this);
        CfSdk.get(SdkC.BLE).setOnNotifyCallback(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBtDeviceListAdapter.setOnItemClickListener(null);
        CfSdk.get(SdkC.BLE).setOnNotifyCallback(null);
        CfSdk.get(SdkC.BLE).setIConnectDoneCallback(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CfSdk.get(SdkC.BLE).stopScan();
        mBinding.getRoot().getHandler().removeCallbacksAndMessages(null);
    }

    public void tipsTvControl(int visible, String mes) {
        mBinding.tipsTv.setVisibility(visible);
        mBinding.tipsTv.setText(mes);
    }


    @Override
    public void OnItemClick(BluetoothDevice pDevice) {
        CfSdk.get(SdkC.BLE).setIConnectDoneCallback(this);
        mBluetoothGatt = CfSdk.get(SdkC.BLE).connectDevice(pDevice, ScanBleActivity.this, false);
        LoadingFragment.showLoading(getSupportFragmentManager(), "蓝牙连接中…");
        mBinding.getRoot().postDelayed(() -> {
            if (mBluetoothGatt != null) mBluetoothGatt.close();
            LoadingFragment.dismissLoading();
            ToastUtil.show("连接超时");
        }, 5000);
    }

    @Override
    public void onConnectDone(boolean pB) {

        //打开蓝牙通知(需在主线程调用)
        mBinding.getRoot().post(() -> CfSdk.get(SdkC.BLE).setNotifyState(AppC.SERVICE_UUID, AppC.NOTIFY_UUID, true));

        //获取设备信息
        mBinding.getRoot().postDelayed(() -> {
            byte[] bytes = CmdBuilder.buildGetDeviceInfoCmd();
            CfSdk.get(SdkC.BLE).writeData(AppC.SERVICE_UUID, AppC.WRITE_UUID, bytes);
        }, 100);
    }

    @Override
    public void onNotify(int pCmdType, CmdData pCmdData) {
        if (pCmdType == CmdType.TYPE_GET_DEVICE_INFO) {
            DeviceInfoBean deviceInfoBean = (DeviceInfoBean) pCmdData.getData();
            mBinding.getRoot().getHandler().removeCallbacksAndMessages(null);
            LoadingFragment.dismissLoading();
            Bundle bundle = new Bundle();
            bundle.putSerializable("deviceInfoBean", deviceInfoBean);
            startActivity(ScanBleActivity.this, BleOperatorActivity.class, bundle);
            finish();
        }
    }
}