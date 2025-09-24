package com.cf.tech.ble.tag_operation;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cf.beans.TagOperationBean;
import com.cf.tech.AppC;
import com.cf.tech.R;
import com.cf.tech.base.BaseTagOperaitonFragment;
import com.cf.tech.ble.search.events.OperationEvent;
import com.cf.tech.databinding.FragmentReadTagBinding;
import com.cf.tech.etimpl.DataLenTextWatcher;
import com.cf.tech.etimpl.HexEditTextWatcher;
import com.cf.tech.utils.ToastUtil;
import com.cf.zsdk.CfSdk;
import com.cf.zsdk.SdkC;
import com.cf.zsdk.cmd.CmdBuilder;
import com.cf.zsdk.cmd.CmdType;
import com.cf.zsdk.uitl.FormatUtil;


public class ReadTagTagOperationFragment extends BaseTagOperaitonFragment {

    private FragmentReadTagBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read_tag, container, false);
        mBinding = FragmentReadTagBinding.bind(view);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //标签密码
        HexEditTextWatcher tagPwTw = new HexEditTextWatcher(mBinding.accPwdEt, 8);
        mBinding.accPwdEt.addTextChangedListener(tagPwTw);

        //标签数据
        HexEditTextWatcher tagDataTw = new HexEditTextWatcher(mBinding.tagDataEt);
        mBinding.tagDataEt.addTextChangedListener(tagDataTw);

        //初始化标签长度默认值
        int wordCountInt = Integer.parseInt(mBinding.wordCountEt.getText().toString());
        tagDataTw.setLimitLen(wordCountInt * 4);

        //标签输入框获取焦点处理
        mBinding.tagDataEt.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) return;
            String string = mBinding.wordCountEt.getText().toString();
            if (TextUtils.isEmpty(string)) {
                mBinding.getRoot().postDelayed(() -> ToastUtil.show("请输入数据长度后，再输入数据"), 200);
            } else if (Integer.parseInt(string) == 0) {
                mBinding.getRoot().postDelayed(() -> ToastUtil.show("数据长度为0，不可输入数据"), 200);
            }
        });

        //标签数据长度
        DataLenTextWatcher wordCountTw = new DataLenTextWatcher(mBinding.wordCountEt, 0, 120, 4) {
            @Override
            public void lenChangeCall(int pLen) {
                tagDataTw.setLimitLen(pLen);
                if (pLen <= 0) {
                    mBinding.tagDataEt.setText("");
                }
            }
        };
        mBinding.wordCountEt.addTextChangedListener(wordCountTw);

        mBinding.readBtn.setOnClickListener(v -> {

            String accPwdStr = mBinding.accPwdEt.getText().toString();
            byte[] AccPwd;
            if (TextUtils.isEmpty(accPwdStr)) {
                AccPwd = new byte[]{0x00, 0x00, 0x00, 0x00};
            } else {
                AccPwd = FormatUtil.hexStrToByteArray(accPwdStr);
            }

            byte memBank = getMemBank();

            String wordPtrStr = mBinding.wordPtrEt.getText().toString();
            if (TextUtils.isEmpty(wordPtrStr)) {
                wordPtrStr = "0";
            }
            int wordPtrInt = Integer.parseInt(wordPtrStr);
            byte[] WordPtr = new byte[2];
            WordPtr[0] = (byte) ((wordPtrInt >> 8) & 0xff);
            WordPtr[1] = (byte) ((byte) wordPtrInt & 0xff);

            String wordCountStr = mBinding.wordCountEt.getText().toString();
            if (TextUtils.isEmpty(wordCountStr)) {
                wordCountStr = "0";
            }
            byte WordCount = (byte) Integer.parseInt(wordCountStr);

            byte[] bytes = CmdBuilder.buildReadISOTagCmd(AccPwd, memBank, WordPtr, WordCount);
            CfSdk.get(SdkC.BLE).writeData(AppC.SERVICE_UUID, AppC.WRITE_UUID, bytes);
        });

        mBinding.writeBtn.setOnClickListener(v -> {
            String accPwdStr = mBinding.accPwdEt.getText().toString();
            byte[] AccPwd;
            if (TextUtils.isEmpty(accPwdStr)) {
                AccPwd = new byte[]{0x00, 0x00, 0x00, 0x00};
            } else {
                AccPwd = FormatUtil.hexStrToByteArray(accPwdStr);
            }

            byte memBank = getMemBank();

            String wordPtrStr = mBinding.wordPtrEt.getText().toString();
            if (TextUtils.isEmpty(wordPtrStr)) {
                wordPtrStr = "0";
            }
            int wordPtrInt = Integer.parseInt(wordPtrStr);
            byte[] WordPtr = new byte[2];
            WordPtr[0] = (byte) ((wordPtrInt >> 8) & 0xff);
            WordPtr[1] = (byte) ((byte) wordPtrInt & 0xff);

            String wordCountStr = mBinding.wordCountEt.getText().toString();
            if (TextUtils.isEmpty(wordCountStr)) {
                wordCountStr = "0";
            }
            byte wordCount = (byte) Integer.parseInt(wordCountStr);

            String tagDataStr = mBinding.tagDataEt.getText().toString();
            if (TextUtils.isEmpty(tagDataStr)) {
                ToastUtil.show("请输入标签数据");
                return;
            }

            byte[] realData = new byte[wordCount * 2];
            byte[] data = FormatUtil.hexStrToByteArray(tagDataStr);
            for (int i = 0; i < realData.length; i++) {
                try {
                    realData[i] = data[i];
                } catch (Exception pE) {
                    break;
                }
            }

            byte[] bytes = CmdBuilder.buildWriteISOTagCmd(AccPwd, memBank, WordPtr, wordCount, realData);
            CfSdk.get(SdkC.BLE).writeData(AppC.SERVICE_UUID, AppC.WRITE_UUID, bytes);
        });
    }

    private byte getMemBank() {
        int checkedRadioButtonId = mBinding.memBankRg.getCheckedRadioButtonId();
        byte memBank = 0x01;
        if (checkedRadioButtonId == R.id.reserve_rb) {
            memBank = 0x00;
        } else if (checkedRadioButtonId == R.id.epc_rb) {
            //memBank = 0x01;
        } else if (checkedRadioButtonId == R.id.tid_rb) {
            memBank = 0x02;
        } else if (checkedRadioButtonId == R.id.user_rb) {
            memBank = 0x03;
        }
        return memBank;
    }

    @Override
    public void onOperationNotify(OperationEvent pEvent) {
        super.onOperationNotify(pEvent);
        TagOperationBean bean = (TagOperationBean) pEvent.mCmdData.getData();
        if (bean == null) return;
        switch (pEvent.mCmdType) {
            case CmdType.TYPE_READ_TAG:
                if (bean.mStatus == 0x12) return;

                if (bean.mStatus == 0x00) {
                    mBinding.errorMsgTv.setVisibility(View.GONE);
                    String builder = "PC：" + FormatUtil.bytesToHexStr(bean.mPC) + "\n" + "CRC: " + FormatUtil.bytesToHexStr(bean.mCRC) + "\n" + "EPC: " + FormatUtil.bytesToHexStr(bean.mEPCNum) + "\n" + "EPC长度: " + bean.mEPCLen + "\n" + "Data: " + FormatUtil.bytesToHexStr(bean.mData) + "\n" + "Data Len: " + bean.mWordCount * 2;
                    mBinding.tagDataTv.setText(builder);
                    mBinding.tagDataEt.setText(FormatUtil.bytesToHexStr(bean.mData).replace(" ", ""));
                    ToastUtil.show(bean.mMsg);
                } else {
                    mBinding.errorMsgTv.setVisibility(View.VISIBLE);
                    mBinding.errorMsgTv.setText(bean.mMsg);
                }

                break;
            case CmdType.TYPE_WRITE_TAG:
                if (bean.mStatus == 0x12) return;
                if (bean.mStatus == 0x00) {
                    mBinding.errorMsgTv.setVisibility(View.GONE);
                    ToastUtil.show(bean.mMsg);
                } else {
                    mBinding.errorMsgTv.setVisibility(View.VISIBLE);
                    mBinding.errorMsgTv.setText(bean.mMsg);
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

}