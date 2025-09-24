package com.cf.tech.etimpl;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

public class DataLenFocusChangeListener implements View.OnFocusChangeListener {

    private EditText mEditText;

    public DataLenFocusChangeListener(EditText pEditText) {
        mEditText = pEditText;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        String string = mEditText.getText().toString();
        int parseInt = TextUtils.isEmpty(string) ? 0 : Integer.parseInt(string);
        if (parseInt == 0 && hasFocus) {
            mEditText.setText("");
            return;
        }
        if (!hasFocus) {
            parseInt = TextUtils.isEmpty(string) ? 0 : Integer.parseInt(string);
            mEditText.setText(String.valueOf(parseInt));
        }
    }
}
