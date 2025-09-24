package com.cf.tech.etimpl;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.cf.zsdk.uitl.FormatUtil;

public class HexEditTextWatcher implements TextWatcher {

    private final EditText mEditText;

    private int mLimitLen = 0;

    public HexEditTextWatcher(EditText pEditText) {
        mEditText = pEditText;
    }

    public HexEditTextWatcher(EditText pEditText, int pLimitLen) {
        mEditText = pEditText;
        mLimitLen = pLimitLen;
    }

    public void setLimitLen(int pLimitLen) {
        mLimitLen = pLimitLen;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String replace = s.toString().replace(" ", "");
        int length = replace.length();
        mEditText.removeTextChangedListener(this);
        if (length <= mLimitLen) {
            String string = FormatUtil.hexStrAddSpaceChar(replace);
            mEditText.setText(string);
            mEditText.setSelection(string.length());
        } else {
            String substring = replace.substring(0, mLimitLen);
            String string = FormatUtil.hexStrAddSpaceChar(substring);
            mEditText.setText(string);
            mEditText.setSelection(string.length());
        }
        mEditText.addTextChangedListener(this);
    }


    @Override
    public void afterTextChanged(Editable s) {

    }

}
