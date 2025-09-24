package com.cf.sdk_demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.le.ScanRecord;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cf.beans.CmdData;
import com.cf.beans.TagInfoBean;
import com.cf.ble.BleUtil;
import com.cf.ble.interfaces.IOnNotifyCallback;
import com.cf.sdk_demo.databinding.ActivityMainBinding;
import com.cf.uart.interfaces.ISerialDataCallback;
import com.cf.zsdk.BleCore;
import com.cf.zsdk.CfSdk;
import com.cf.zsdk.SdkC;
import com.cf.zsdk.UartCore;
import com.cf.zsdk.cmd.CmdBuilder;
import com.cf.zsdk.uitl.FormatUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@SuppressLint("MissingPermission")
public class MainActivity extends AppCompatActivity implements IOnNotifyCallback, ISerialDataCallback {

    public static final UUID mServiceUuid = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    public static final UUID mWriteUuid = UUID.fromString("0000ffe3-0000-1000-8000-00805f9b34fb");
    public static final UUID mNotifyUuid = UUID.fromString("0000ffe4-0000-1000-8000-00805f9b34fb");

    private ActivityMainBinding mBinding;

    private final BleCore mBleCore = CfSdk.get(SdkC.BLE);

    public Set<BluetoothDevice> mDeviceSet = new HashSet<>();
    UartCore uartCore = CfSdk.get(SdkC.UART);
    private BluetoothGatt mGatt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());


        ToastUtil.init(this);

        mBleCore.init(this);

        mBinding.scanDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show("设备扫描中，请稍等……");
                mBleCore.startScan(pResult -> {
                    BluetoothDevice device = pResult.getDevice();
                    ScanRecord scanRecord = pResult.getScanRecord();
                    if (scanRecord != null) {
                        byte[] bytes = BleUtil.getCompanyId(scanRecord.getBytes());
                        String companyIdStr = FormatUtil.bytesToHexStr(bytes);
                        if ("2795".equals(companyIdStr)) {
                            Log.e("Hello", "onScanResult == >" + scanRecord);
                            mDeviceSet.add(device);
                        }
                    }
                });
                mBinding.getRoot().postDelayed(() -> {
                    ToastUtil.show("扫描完成");
                    mBleCore.stopScan();
                    Log.e("Hello", "onClick == >" + mDeviceSet.size());
                }, 5000);
            }
        });

        mBinding.connectDevice.setOnClickListener(v -> {
            for (BluetoothDevice bluetoothDevice : mDeviceSet) {

                if ("50:54:7B:3C:15:F1".equals(bluetoothDevice.getAddress())) {
                    mBleCore.stopScan();
//                    mGatt = mBleCore.connectDevice(bluetoothDevice, MainActivity.this, true, pB -> {
//                        Log.d("Hello", "onConnectDone == > 连接完成");
//                        ToastUtil.show("连接完成");
//                    });
                    mGatt = mBleCore.connectDevice(bluetoothDevice, MainActivity.this, true);
                }
            }
        });

        mBinding.openNotify.setOnClickListener(v -> mBleCore.setNotifyState(mServiceUuid, mNotifyUuid, true, this));

        mBinding.closeNotify.setOnClickListener(v -> mBleCore.setNotifyState(mServiceUuid, mNotifyUuid, false, null));

        mBinding.sendCmd.setOnClickListener(v -> {
            //CF FF 0001 05 00 00000000 f5b5
            byte[] bytes = FormatUtil.hexStrToByteArray("CFFF0001050000000000f5b5");
            byte[] bytes1 = CmdBuilder.buildInventoryISOContinueCmd((byte) 0, 0);
            mBleCore.writeData(mServiceUuid, mWriteUuid, bytes1);
        });

        mBinding.stopInventoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] bytes = FormatUtil.hexStrToByteArray("CFFF000200E761");
                mBleCore.writeData(mServiceUuid, mWriteUuid, bytes);
            }
        });

        mBinding.setMtuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGatt != null) {
                    mGatt.requestMtu(512);
                }
            }
        });

        mBinding.disconnectDevice.setOnClickListener(v -> mBleCore.disconnectedDevice());


        mBinding.initSerial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uartCore.init("/dev/ttyS7", 115200);
            }
        });

        mBinding.receiverData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uartCore.receiverData(MainActivity.this);
            }
        });

        mBinding.releaseSerial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uartCore.release();
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBleCore.disconnectedDevice();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 12321) return;
        if (resultCode == Activity.RESULT_OK) {
            Log.e("Hello", "onActivityResult == > 打开蓝牙成功");
            ToastUtil.show("蓝牙打开成功");
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Hello", "onActivityResult == > 用户拒绝授权");
            ToastUtil.show("用户拒绝授权");
        } else {
            Log.e("Hello", "onActivityResult == > 未知结果码");
        }
    }

    @Override
    public void onNotify(int pCmdType, CmdData pCmdData) {
        switch (pCmdType) {
            case 1:
                Class<?> dataType = pCmdData.getDataType();
                TagInfoBean data = (TagInfoBean) pCmdData.getData();
                Log.e("Hello", "onNotify == >" + data.toString());
                break;
            default:
                break;
        }
    }

    @Override
    public void onDataCallback(byte[] pBytes) {
        Log.e("Hello", "onDataCallback == >" + FormatUtil.bytesToHexStr(pBytes));
    }

}