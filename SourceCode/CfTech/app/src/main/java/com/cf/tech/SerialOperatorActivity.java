package com.cf.tech;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.cf.serial.CfSerialFinder;
import com.cf.tech.adapter.SerialDataAdapter;
import com.cf.tech.base.BaseActivity;
import com.cf.tech.databinding.ActivitySerialOperatorBinding;
import com.cf.tech.utils.ToastUtil;
import com.cf.uart.interfaces.ISerialDataCallback;
import com.cf.zsdk.CfSdk;
import com.cf.zsdk.SdkC;
import com.cf.zsdk.UartCore;
import com.cf.zsdk.cmd.CmdBuilder;
import com.cf.zsdk.uitl.FormatUtil;
import com.cf.zsdk.uitl.LogUtil;

import java.io.IOException;

public class SerialOperatorActivity extends BaseActivity implements View.OnClickListener, ISerialDataCallback {

    private ActivitySerialOperatorBinding mBinding;
    private UartCore mUartCore;
    private SerialDataAdapter mAdapter;

    @Override
    public View setLayout() {
        mBinding = ActivitySerialOperatorBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    public void init() {
        super.init();

        mUartCore = CfSdk.get(SdkC.UART);

        mAdapter = new SerialDataAdapter();
        mBinding.serialDataRv.setLayoutManager(new LinearLayoutManager(this));
        mBinding.serialDataRv.setAdapter(mAdapter);

        //设置串口数据集
        CfSerialFinder cfSerialFinder = new CfSerialFinder();
        String[] allDevicesPath;
        try {
            allDevicesPath = cfSerialFinder.getAllDevicesPath();
        } catch (IOException pE) {
            allDevicesPath = new String[]{getString(R.string.fail_get_serial_path)};
            ToastUtil.show(pE.getMessage());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allDevicesPath);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.serialPathSp.setAdapter(adapter);

        //设置点击事件
        mBinding.openSerialBtn.setOnClickListener(this);
        mBinding.sendDataBtn.setOnClickListener(this);
        mBinding.closeSerilaBtn.setOnClickListener(this);
        mBinding.startInventoryBtn.setOnClickListener(this);
        mBinding.stopInventoryBtn.setOnClickListener(this);
        mBinding.dataCharsetSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handleCmdCharsetWhenItemChange(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        byte[] bytes = CmdBuilder.buildInventoryISOContinueCmd((byte) 0, 0);
        LogUtil.e("Hello", "SerialOperatorActivity.init == > " + FormatUtil.bytesToHexStr(bytes));
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
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.send_data_btn) {
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
            mUartCore.sendData(bytes);
        } else if (id == R.id.start_inventory_btn) {
            byte[] bytes = CmdBuilder.buildInventoryISOContinueCmd((byte) 0, 0);
            if (mUartCore.sendData(bytes)) {
                ToastUtil.show("发送成功");
            } else {
                ToastUtil.show("发送失败");
            }
        } else if (id == R.id.stop_inventory_btn) {
            byte[] bytes = CmdBuilder.buildStopInventoryCmd();
            if (mUartCore.sendData(bytes)) {
                ToastUtil.show("发送成功");
            } else {
                ToastUtil.show("发送失败");
            }
        } else if (id == R.id.open_serial_btn) {
            String serialPath = mBinding.serialPathSp.getSelectedItem().toString();
            int baudRate = Integer.parseInt(mBinding.baudRateSp.getSelectedItem().toString());
            String msg = "";
            if (mUartCore.init(serialPath, baudRate)) {
                int childCount = mBinding.presetCmdLl.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = mBinding.presetCmdLl.getChildAt(i);
                    childAt.setEnabled(true);
                }
                mUartCore.receiverData(this);
                msg = "串口打开成功";
            } else {
                msg = "串口打开失败";
            }
            ToastUtil.show(msg);
        } else if (id == R.id.close_serila_btn) {
            mUartCore.release();
        }
    }

    @Override
    public void onDataCallback(byte[] pBytes) {
        mBinding.getRoot().post(() -> {
            if (mAdapter == null) return;
            mBinding.serialTipsTv.setVisibility(View.GONE);
            mAdapter.setData(pBytes);
            //将RecycleView的item移动到最后一项
            mBinding.serialDataRv.scrollToPosition(mAdapter.getItemCount() - 1);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUartCore != null) {
            mUartCore.release();
        }
    }
}