package com.cf.tech.etimpl;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

public abstract class DataLenTextWatcher implements TextWatcher {

    /**
     * 最大长度
     */
    private final int mMaxLen;
    private final int mMinLen;
    private final EditText mEditText;
    /**
     * 实际输出数据长度倍数
     */
    private final int mMultiple;

    public DataLenTextWatcher(EditText pEditText, int pMinLen, int pMaxLen, int pMultiple) {
        mEditText = pEditText;
        mMaxLen = pMaxLen;
        mMinLen = pMinLen;
        mMultiple = pMultiple;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String string = s.toString();
        String string1 = !TextUtils.isEmpty(string) ? string : String.valueOf(mMinLen);
        int len = Integer.parseInt(string1);
        if (len > mMaxLen) {
            mEditText.removeTextChangedListener(this);
            mEditText.setText(String.valueOf(mMaxLen));
            len = mMaxLen * mMultiple;
            mEditText.setSelection(mEditText.getText().toString().length());
            mEditText.addTextChangedListener(this);
        } else if (len < mMinLen) {
            mEditText.removeTextChangedListener(this);
            mEditText.setText(String.valueOf(mMinLen));
            len = mMaxLen * mMultiple;
            mEditText.setSelection(mEditText.getText().toString().length());
            mEditText.addTextChangedListener(this);
        } else {
            len = len * mMultiple;
        }
        lenChangeCall(len);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    public abstract void lenChangeCall(int pLen);
}
