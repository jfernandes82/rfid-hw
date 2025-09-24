package com.cf.tech.ble;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.cf.beans.DeviceInfoBean;
import com.cf.ble.interfaces.IBleDisConnectCallback;
import com.cf.tech.AppC;
import com.cf.zsdk.CfSdk;
import com.cf.zsdk.SdkC;
import com.cf.zsdk.uitl.LogUtil;
import com.cf.tech.CfApplication;
import com.cf.tech.R;
import com.cf.tech.base.BaseActivity;
import com.cf.tech.databinding.ActivityBleOperatorBinding;
import com.cf.tech.utils.ActivityUtil;
import com.cf.tech.utils.ToastUtil;
import com.google.android.material.navigation.NavigationBarView;

@SuppressLint("MissingPermission")
public class BleOperatorActivity extends BaseActivity implements IBleDisConnectCallback {

    private ActivityBleOperatorBinding mBinding;
    private DeviceInfoBean mDeviceInfoBean;

    public DeviceInfoBean getDeviceInfoBean() {
        return mDeviceInfoBean;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //适配三大金刚导航栏下，tab栏过高问题
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
        CfSdk.get(SdkC.BLE).setIBleDisConnectCallback(this);
    }

    @Override
    public View setLayout() {
        mBinding = ActivityBleOperatorBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void init() {
        super.init();

        Bundle bundleExtra = getIntent().getBundleExtra(getClass().getName());
        if (bundleExtra != null) {
            mDeviceInfoBean = (DeviceInfoBean) bundleExtra.getSerializable("deviceInfoBean");
            if (mDeviceInfoBean != null && !needHideScanItemDevice(mDeviceInfoBean.mFirmVer)) {
                mBinding.navView.getMenu().findItem(R.id.navigation_scan).setVisible(false);
                mBinding.navView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_AUTO);
            }
        }

        setSupportActionBar(mBinding.toolBar);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_search, R.id.navigation_scan, R.id.navigation_setting).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_ble_operator);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(mBinding.navView, navController);
    }

    private boolean needHideScanItemDevice(String pString) {
        return pString.contains("103") || pString.contains("104");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CfSdk.get(SdkC.BLE).setIBleDisConnectCallback(null);
        CfSdk.get(SdkC.BLE).setNotifyState(AppC.SERVICE_UUID, AppC.NOTIFY_UUID, false, null);
        CfSdk.get(SdkC.BLE).disconnectedDevice();
    }

    @Override
    public void onBleDisconnect() {
        mBinding.getRoot().post(() -> ToastUtil.show("蓝牙连接已断开"));
        LogUtil.d("蓝牙连接已断开");
        ActivityUtil.restartToMainActivity(this);
    }
}