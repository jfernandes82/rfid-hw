package com.cf.tech.ble.setting;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.cf.beans.AllParamBean;
import com.cf.beans.BatteryCapacityBean;
import com.cf.beans.CmdData;
import com.cf.beans.DeviceInfoBean;
import com.cf.beans.DeviceNameBean;
import com.cf.beans.GeneralBean;
import com.cf.beans.OutputModeBean;
import com.cf.ble.interfaces.IOnNotifyCallback;
import com.cf.tech.AppC;
import com.cf.tech.CfApplication;
import com.cf.tech.R;
import com.cf.tech.databinding.FragmentSettingBinding;
import com.cf.tech.utils.ActivityUtil;
import com.cf.tech.utils.ToastUtil;
import com.cf.zsdk.CfSdk;
import com.cf.zsdk.SdkC;
import com.cf.zsdk.cmd.CmdBuilder;
import com.cf.zsdk.cmd.CmdType;
import com.cf.zsdk.uitl.FormatUtil;
import com.cf.zsdk.uitl.LogUtil;

@SuppressLint("MissingPermission")
public class SettingFragment extends Fragment implements IOnNotifyCallback {

    private FragmentSettingBinding mBinding;

    private AllParamBean mAllParamBean;
    //这个标志位，用于控制获取信息流程，每次只能获取一次，串行获取信息
    private boolean mGetInfoTag = false;
    //这个标志，用于休眠时判读是否正常获取电池信息，要是正常获取就说明
    //设备已经从休眠状态切换到工作状态，可以继续获取其他信息
    private volatile boolean mGetBatteryInfoFlag;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SettingViewModel settingViewModel = new ViewModelProvider(this).get(SettingViewModel.class);

        mBinding = FragmentSettingBinding.inflate(inflater, container, false);

        final TextView textView = mBinding.textNotifications;
        settingViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //设置设备名称
        mBinding.setDeviceNameBtn.setOnClickListener(v -> {
            String str = mBinding.deviceNameEt.getText().toString();
            if (TextUtils.isEmpty(str)) {
                ToastUtil.show("请输入设备名称");
                return;
            }
            byte[] bytes = CmdBuilder.buildSetOrGetBtNameCmd((byte) 0x01, str);
            CfSdk.get(SdkC.BLE).writeData(AppC.SERVICE_UUID, AppC.WRITE_UUID, bytes);
        });

        //输出模式
        mBinding.setOutputModelBtn.setOnClickListener(v -> {
            long selectedItemId = mBinding.outputModelSp.getSelectedItemId();
            byte mode = (byte) (selectedItemId == 0 ? 0x00 : 0x01);
            byte[] bytes = CmdBuilder.buildSetOutputModeCmd(mode);
            CfSdk.get(SdkC.BLE).writeData(AppC.SERVICE_UUID, AppC.WRITE_UUID, bytes);
        });


        //设置所有配置
        mBinding.setAllParamBtn.setOnClickListener(v -> {
            if (mAllParamBean != null) {
                String workFreq = (String) mBinding.operatingFrequencySp.getSelectedItem();
                String[] stringArray = getResources().getStringArray(R.array.operating_frequency_value);
                if (stringArray[0].equals(workFreq)) {
                    //美标
                    mAllParamBean.mRfidFreq.mREGION = 0x01;

                    mAllParamBean.mRfidFreq.mSTRATFREI[0] = 0x03;
                    mAllParamBean.mRfidFreq.mSTRATFREI[1] = (byte) 0x86;

                    mAllParamBean.mRfidFreq.mSTRATFRED[0] = 0x02;
                    mAllParamBean.mRfidFreq.mSTRATFRED[1] = (byte) 0xee;

                    mAllParamBean.mRfidFreq.mSTEPFRE[0] = 0x01;
                    mAllParamBean.mRfidFreq.mSTEPFRE[1] = (byte) 0xF4;

                    mAllParamBean.mRfidFreq.mCN = 0x32;
                } else if (stringArray[1].equals(workFreq)) {
                    //欧标
                    mAllParamBean.mRfidFreq.mREGION = 0x03;

                    mAllParamBean.mRfidFreq.mSTRATFREI[0] = 0x03;
                    mAllParamBean.mRfidFreq.mSTRATFREI[1] = (byte) 0x61;

                    mAllParamBean.mRfidFreq.mSTRATFRED[0] = 0x00;
                    mAllParamBean.mRfidFreq.mSTRATFRED[1] = (byte) 0x64;

                    mAllParamBean.mRfidFreq.mSTEPFRE[0] = 0x00;
                    mAllParamBean.mRfidFreq.mSTEPFRE[1] = (byte) 0xc8;

                    mAllParamBean.mRfidFreq.mCN = 0x0f;
                }

                String outputPower = (String) mBinding.outputPowerSp.getSelectedItem();
                mAllParamBean.mRfidPower = Integer.valueOf(outputPower).byteValue();

                String qValueStr = (String) mBinding.qValueSp.getSelectedItem();
                mAllParamBean.mQValue = Integer.valueOf(qValueStr).byteValue();

                int selectedItemPosition = mBinding.sessionSp.getSelectedItemPosition();
                int sessionInt = getSessionValue(selectedItemPosition);
                mAllParamBean.mSession = (byte) sessionInt;
                byte[] bytes = CmdBuilder.buildSetAllParamCmd(mAllParamBean);
                CfSdk.get(SdkC.BLE).writeData(AppC.SERVICE_UUID, AppC.WRITE_UUID, bytes);
            }
        });

        //恢复出厂设置
        mBinding.restoreBtn.setOnClickListener(v -> {
            byte[] bytes = CmdBuilder.buildRebootCmd();
            CfSdk.get(SdkC.BLE).writeData(AppC.SERVICE_UUID, AppC.WRITE_UUID, bytes);
        });

        //刷新信息
        mBinding.refreshBtn.setOnClickListener(v -> getInfo());
    }

    private static int getSessionValue(int selectedItemPosition) {
        int sessionInt;
        switch (selectedItemPosition) {
            case 1:
                sessionInt = 1;
                break;
            case 2:
                sessionInt = 2;
                break;
            case 3:
                sessionInt = 3;
                break;
            case 4:
                sessionInt = 0xff;
                break;
            case 5:
                sessionInt = 0xfe;
                break;
            case 6:
                sessionInt = 0xfd;
                break;
            default:
                sessionInt = 0;
                break;
        }
        return sessionInt;
    }

    private static int getSessionIndex(byte pSessionValue) {
        int index;
        switch (pSessionValue) {
            case 1:
                index = 1;
                break;
            case 2:
                index = 2;
                break;
            case 3:
                index = 3;
                break;
            case (byte) 0xff:
                index = 4;
                break;
            case (byte) 0xfe:
                index = 5;
                break;
            case (byte) 0xfd:
                index = 6;
                break;
            default:
                index = 0;
                break;
        }
        return index;
    }

    @Override
    public void onResume() {
        super.onResume();
        CfSdk.get(SdkC.BLE).setOnNotifyCallback(this);
        getInfo();
    }

    private void getInfo() {
        if (mGetInfoTag) {
            return;
        }
        mGetInfoTag = true;
        CfApplication.sCtp.submit(() -> {

            mGetBatteryInfoFlag = false;

            LogUtil.d("SettingFragment.getInfo == > 获取电池信息");
            byte[] bytes = CmdBuilder.buildGetBatteryCapacityCmd();
            for (int i = 0; i < 25; i++) {
                if (mGetBatteryInfoFlag) break;
                SystemClock.sleep(80);
                if (mGetBatteryInfoFlag) break;
                CfSdk.get(SdkC.BLE).writeData(AppC.SERVICE_UUID, AppC.WRITE_UUID, bytes);
            }
            if (!mGetBatteryInfoFlag) {
                mBinding.getRoot().post(() -> ToastUtil.show("获取信息失败，请按”刷新按钮“重试"));
                return;
            }

            LogUtil.d("SettingFragment.getInfo == > 获取所有参数");
            bytes = CmdBuilder.buildGetAllParamCmd();
            for (int i = 0; i < 5; i++) {
                SystemClock.sleep(100);
                if (CfSdk.get(SdkC.BLE).writeData(AppC.SERVICE_UUID, AppC.WRITE_UUID, bytes)) break;
            }

            LogUtil.d("SettingFragment.getInfo == > 获取设备名称");
            bytes = CmdBuilder.buildSetOrGetBtNameCmd((byte) 0x02, null);
            for (int i = 0; i < 5; i++) {
                SystemClock.sleep(100);
                if (CfSdk.get(SdkC.BLE).writeData(AppC.SERVICE_UUID, AppC.WRITE_UUID, bytes)) break;
            }

            LogUtil.d("SettingFragment.getInfo == > 获取固件信息");
            bytes = CmdBuilder.buildGetDeviceInfoCmd();
            for (int i = 0; i < 5; i++) {
                SystemClock.sleep(100);
                if (CfSdk.get(SdkC.BLE).writeData(AppC.SERVICE_UUID, AppC.WRITE_UUID, bytes)) break;
            }

            LogUtil.d("SettingFragment.getInfo == > 获取输出模式");
            bytes = CmdBuilder.buildGetOutputModeCmd();
            for (int i = 0; i < 5; i++) {
                SystemClock.sleep(100);
                if (CfSdk.get(SdkC.BLE).writeData(AppC.SERVICE_UUID, AppC.WRITE_UUID, bytes)) break;
            }

            mGetInfoTag = false;
        });
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
    public void onNotify(int pCmdType, CmdData pCmdData) {
        switch (pCmdType) {
            case CmdType.TYPE_GET_DEVICE_INFO:
                DeviceInfoBean deviceInfoBean = (DeviceInfoBean) pCmdData.getData();
                mBinding.getRoot().post(() -> {
                    if (deviceInfoBean.mStatus == 0) {
                        mBinding.hwVersionTv.setText(deviceInfoBean.mHwVer);
                        mBinding.firmwareVersionTv.setText(deviceInfoBean.mFirmVer);
                    } else {
                        ToastUtil.show(deviceInfoBean.mMsg);
                    }
                });
                break;
            case CmdType.TYPE_REBOOT:
                GeneralBean rebootBean = (GeneralBean) pCmdData.getData();
                mBinding.getRoot().post(() -> ToastUtil.show(rebootBean.mMsg));
                ActivityUtil.restartToMainActivity(getContext());
                break;
            case CmdType.TYPE_SET_ALL_PARAM:
                GeneralBean generalBean = (GeneralBean) pCmdData.getData();
                mBinding.getRoot().post(() -> ToastUtil.show(generalBean.mMsg));
                break;
            case CmdType.TYPE_GET_ALL_PARAM:
                AllParamBean allParamBean = (AllParamBean) pCmdData.getData();
                if (allParamBean.mStatus == 0) {
                    mAllParamBean = allParamBean;
                    mBinding.getRoot().post(() -> {
                        byte region = allParamBean.mRfidFreq.mREGION;
                        if (region == 1) {
                            mBinding.operatingFrequencySp.setSelection(0);
                        } else if (region == 3) {
                            mBinding.operatingFrequencySp.setSelection(1);
                        }

                        byte rfidPower = allParamBean.mRfidPower;
                        if (rfidPower > 33) {
                            rfidPower = 33;
                        }
                        mBinding.outputPowerSp.setSelection(rfidPower);

                        byte qValue = allParamBean.mQValue;
                        if (qValue > 15) {
                            qValue = 15;
                        }
                        mBinding.qValueSp.setSelection(qValue);

                        byte session = allParamBean.mSession;
                        int sessionIndex = getSessionIndex(session);
                        mBinding.sessionSp.setSelection(sessionIndex);
                    });
                }
                break;
            case CmdType.TYPE_GET_BATTERY_CAPACITY://获取电池电量
                BatteryCapacityBean batteryCapacityBean = (BatteryCapacityBean) pCmdData.getData();
                LogUtil.d("SettingFragment.onNotify == > " + batteryCapacityBean);
                //获取电池信息成功，表示休眠状态已唤醒
                mGetBatteryInfoFlag = batteryCapacityBean.mStatus == SdkC.STATUS_CODE_SUCCEED;
                mBinding.getRoot().post(() -> {
                    if (batteryCapacityBean.mStatus == SdkC.STATUS_CODE_SUCCEED) {
                        mBinding.batteryCapacityTv.setText(String.format("%s%%", batteryCapacityBean.mBatteryCapacity));
                    } else {
                        ToastUtil.show(batteryCapacityBean.mMsg);
                    }
                });
                break;
            case CmdType.TYPE_SET_OR_GET_BT_NAME:
                DeviceNameBean deviceNameBean = (DeviceNameBean) pCmdData.getData();
                mBinding.getRoot().post(() -> {
                    if (deviceNameBean == null) {
                        return;
                    }
                    if (deviceNameBean.mOption == 0x01) {
                        //设置操作
                        ToastUtil.show(deviceNameBean.mMsg);

                        if (deviceNameBean.mStatus == SdkC.STATUS_CODE_SUCCEED) {
                            //修改名称后，下发刷新缓存指令操作
                            byte[] bytes = FormatUtil.hexStrToByteArray("CFFF0086010336A6");
                            CfSdk.get(SdkC.BLE).writeData(AppC.SERVICE_UUID, AppC.WRITE_UUID, bytes);

                            //返回主页面
                            ActivityUtil.restartToMainActivity(getContext());
                        }
                    } else if (deviceNameBean.mOption == 0x02) {
                        //读取操作
                        if (deviceNameBean.mStatus == 0) {
                            mBinding.deviceNameEt.setText(deviceNameBean.mDeviceName);
                        } else {
                            ToastUtil.show(deviceNameBean.mMsg);
                        }
                    }
                });
                break;
            case CmdType.TYPE_OUT_MODE:
                OutputModeBean outputModeBean = (OutputModeBean) pCmdData.getData();
                if (outputModeBean.mOption == 0x01) {
                    //设置
                    mBinding.getRoot().post(() -> ToastUtil.show(outputModeBean.mMsg));
                } else {
                    //获取
                    if (outputModeBean.mStatus == SdkC.STATUS_CODE_SUCCEED) {
                        mBinding.getRoot().post(() -> mBinding.outputModelSp.setSelection(outputModeBean.mMode));
                    } else {
                        mBinding.getRoot().post(() -> ToastUtil.show(outputModeBean.mMsg));
                    }
                }
                break;
            default:
                break;
        }
    }
}