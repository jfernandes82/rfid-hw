package com.cf.tech;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cf.tech.adapter.UsbDataAdapter;
import com.cf.tech.base.BaseActivity;
import com.cf.tech.databinding.ActivityUsbBinding;
import com.cf.tech.utils.ToastUtil;
import com.cf.usb.interfaces.IReadDataCallback;
import com.cf.usb.interfaces.IUsbConnectDone;
import com.cf.zsdk.CfSdk;
import com.cf.zsdk.SdkC;
import com.cf.zsdk.UsbCore;
import com.cf.zsdk.cmd.CmdBuilder;
import com.cf.zsdk.uitl.FormatUtil;

import java.util.ArrayList;

public class UsbActivity extends BaseActivity implements IUsbConnectDone, IReadDataCallback {

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                loadAllUsbDevice();
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null && mTargetDevice != null && device.getProductId() == mTargetDevice.getProductId() && device.getVendorId() == mTargetDevice.getVendorId()) {
                    ToastUtil.show("the target device detached,please attached it and reinitialize");
                    mUsbCore.release();
                }
            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                loadAllUsbDevice();

            }
        }
    };

    private ActivityUsbBinding mBinding;
    private UsbCore mUsbCore;
    private UsbDevice mTargetDevice;
    private UsbDataAdapter mUsbDataAdapter;
    private ArrayList<Pair<Integer, Integer>> mAllDevicePidAndVid;

    @Override
    public View setLayout() {
        mBinding = ActivityUsbBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    public void init() {
        super.init();

        mUsbCore = CfSdk.get(SdkC.USB);
        mUsbCore.init(this);

        //初始化usb设备列表
        mAllDevicePidAndVid = loadAllUsbDevice();

        mUsbDataAdapter = new UsbDataAdapter();
        mBinding.usbDataRv.setLayoutManager(new LinearLayoutManager(this));
        mBinding.usbDataRv.setAdapter(mUsbDataAdapter);
        mBinding.usbDataRv.setItemAnimator(null);

        mBinding.connectDeviceBtn.setOnClickListener(v -> {
            int selectedItemPosition = mBinding.pidAndVidSp.getSelectedItemPosition();
            if (mAllDevicePidAndVid.isEmpty()) {
                ToastUtil.show("没有可连接的USB设备");
                return;
            }
            Pair<Integer, Integer> pair = mAllDevicePidAndVid.get(selectedItemPosition);
            mTargetDevice = mUsbCore.findTargetDevice(pair.first, pair.second);
            if (mTargetDevice != null) {
                mUsbCore.connectDevice(UsbActivity.this, mTargetDevice, UsbActivity.this);
            }
        });

        mBinding.dataCharsetSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] charSet = getResources().getStringArray(R.array.serial_data_charset_value);
                String string = charSet[position];
                if (charSet[0].equals(string)) {
                    String string1 = mBinding.dataEt.getText().toString();
                    byte[] bytes = FormatUtil.hexStrToByteArray(string1);
                    String string2 = new String(bytes);
                    mBinding.dataEt.setText(string2);
                } else {
                    byte[] bytes = mBinding.dataEt.getText().toString().getBytes();
                    String string1 = FormatUtil.bytesToHexStr(bytes);
                    mBinding.dataEt.setText(string1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mBinding.sendDataBtn.setOnClickListener(v -> {
            String data = mBinding.dataEt.getText().toString();
            String charset = (String) mBinding.dataCharsetSp.getSelectedItem();
            byte[] bytes;
            if (getResources().getStringArray(R.array.serial_data_charset_value)[0].equals(charset)) {
                bytes = data.getBytes();
            } else {
                bytes = FormatUtil.hexStrToByteArray(data);
            }
            mUsbCore.writeData(bytes, 50);
        });

        mBinding.startInventoryBtn.setOnClickListener(v -> {
            byte[] bytes = CmdBuilder.buildInventoryISOContinueCmd((byte) 0, 0);
            if (mUsbCore.writeData(bytes, 50)) {
                ToastUtil.show("发送成功");
            } else {
                ToastUtil.show("发送失败");
            }
        });

        mBinding.stopInventoryBtn.setOnClickListener(v -> {
            byte[] bytes = CmdBuilder.buildStopInventoryCmd();
            if (mUsbCore.writeData(bytes, 50)) {
                ToastUtil.show("发送成功");
            } else {
                ToastUtil.show("发送失败");
            }
        });
    }

    private @NonNull ArrayList<Pair<Integer, Integer>> loadAllUsbDevice() {
        if (mUsbCore == null) {
            mUsbCore = CfSdk.get(SdkC.USB);
            mUsbCore.init(this);
        }
        ArrayList<Pair<Integer, Integer>> allDevicePidAndVid = mUsbCore.getAllDevicePidAndVid();
        String[] pidAndVidStr;
        if (!allDevicePidAndVid.isEmpty()) {
            pidAndVidStr = new String[allDevicePidAndVid.size()];
            for (int i = 0; i < allDevicePidAndVid.size(); i++) {
                Pair<Integer, Integer> integerIntegerPair = allDevicePidAndVid.get(i);
                pidAndVidStr[i] = "Pid:" + integerIntegerPair.first + " | " + "Vid:" + integerIntegerPair.second;
            }
        } else {
            pidAndVidStr = new String[]{getString(R.string.without_usb_device)};
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pidAndVidStr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.pidAndVidSp.setAdapter(adapter);
        return allDevicePidAndVid;
    }


    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUsbCore.setIReadDataCallback(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mUsbCore.setIReadDataCallback(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUsbCore.release();
    }

    @Override
    public void onUsbConnectDone(boolean pB) {
        String msg;
        if (pB) {
            int childCount = mBinding.presetCmdLl.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = mBinding.presetCmdLl.getChildAt(i);
                childAt.setEnabled(true);
            }
            msg = "连接USB设备成功";
            //设备连接成功，开始读数据
            mUsbCore.readDataAsync(50);
        } else {
            msg = "连接USB设备失败";
        }
        ToastUtil.show(msg);
    }

    @Override
    public void onDataBack(byte[] pBytes) {
        mBinding.getRoot().post(() -> {
            mBinding.usbDataTv.setVisibility(View.GONE);
            mUsbDataAdapter.setData(pBytes);
            mBinding.usbDataRv.scrollToPosition(mUsbDataAdapter.getItemCount() - 1);
        });
    }
}