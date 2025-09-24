package com.cf.tech;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;

import androidx.annotation.NonNull;

import com.cf.tech.base.BaseActivity;
import com.cf.tech.ble.ScanBleActivity;
import com.cf.tech.databinding.ActivityMainBinding;
import com.cf.tech.lan.ScanLanDeviceActivity;
import com.cf.tech.utils.FileUtil;
import com.cf.zsdk.uitl.LogUtil;

import java.io.File;
import java.util.List;
import java.util.Objects;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {


    private ActivityMainBinding mBinding;
    private static final String[] perms = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int PERMISSIONS_REQUEST_CODE = 8621;


    @Override
    public View setLayout() {
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    public void init() {
        super.init();
        mBinding.appVersion.setText(String.format("App版本：%s", getAppVersion(this)));
        mBinding.bleBtn.setOnClickListener(view -> startActivity(MainActivity.this, ScanBleActivity.class));
        mBinding.serialBtn.setOnClickListener(v -> startActivity(MainActivity.this, SerialOperatorActivity.class));
        mBinding.otgBtn.setOnClickListener(v -> startActivity(MainActivity.this, UsbActivity.class));
        mBinding.netBtn.setOnClickListener(v -> startActivity(MainActivity.this, ScanLanDeviceActivity.class));

        mBinding.getRoot().post(this::requestPermission);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public String getAppVersion(Context context) {
        String versionName = "0.0.0";
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return versionName;
    }

    @AfterPermissionGranted(PERMISSIONS_REQUEST_CODE)
    private void requestPermission() {
        if (EasyPermissions.hasPermissions(this, perms)) {
            return;
        }
        EasyPermissions.requestPermissions(this, getString(R.string.request_permission_tips), 100, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
}