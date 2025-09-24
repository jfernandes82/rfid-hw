package com.cf.tech.base;

import android.app.Dialog;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;

import com.cf.tech.R;
import com.cf.tech.utils.ScreenUtil;

public class BaseDialogFragment extends DialogFragment {

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog == null) return;
        Window window = dialog.getWindow();
        if (window == null) return;
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = (int) (ScreenUtil.getScreenWidth(requireContext()) * 0.93);
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(attributes);
        window.setBackgroundDrawableResource(R.drawable.radius_bg);
        setCancelable(false);
    }
}
