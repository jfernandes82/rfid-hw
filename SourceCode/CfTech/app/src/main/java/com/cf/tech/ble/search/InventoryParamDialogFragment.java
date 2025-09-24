package com.cf.tech.ble.search;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cf.tech.AppC;
import com.cf.tech.R;
import com.cf.tech.base.BaseDialogFragment;
import com.cf.tech.databinding.DialogFragmentInventoryParamBinding;
import com.cf.tech.utils.ToastUtil;
import com.tencent.mmkv.MMKV;

import java.util.Objects;

public class InventoryParamDialogFragment extends BaseDialogFragment {

    private DialogFragmentInventoryParamBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_inventory_param, container, false);
        mBinding = DialogFragmentInventoryParamBinding.bind(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int anInt = MMKV.defaultMMKV().getInt(AppC.INVENTORY_TYPE, 0);
        mBinding.inventoryTypeSp.setSelection(anInt);

        switch (anInt) {
            case 0:
                int anInt1 = MMKV.defaultMMKV().getInt(AppC.INVENTORY_BY_TIME, 0);
                mBinding.inventoryParamEt.setText(String.valueOf(anInt1));
                break;
            case 1:
                int anInt2 = MMKV.defaultMMKV().getInt(AppC.INVENTORY_BY_COUNT, 0);
                mBinding.inventoryParamEt.setText(String.valueOf(anInt2));
                break;
        }

        mBinding.dismissBtn.setOnClickListener(v -> dismiss());

        mBinding.inventoryTypeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mBinding.inventoryParamEt.clearFocus();
                switch (position) {
                    case 0:
                        //按时间盘点
                        mBinding.inventoryParamTil.setHint("请输入盘点时间（s）");
                        break;
                    case 1:
                        //按次数盘点
                        mBinding.inventoryParamTil.setHint("请输入盘点次数");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mBinding.saveConfigBtn.setOnClickListener(v -> {
            int selectedItemPosition = mBinding.inventoryTypeSp.getSelectedItemPosition();
            int inventoryType = getResources().getIntArray(R.array.inventory_type_value)[selectedItemPosition];
            MMKV.defaultMMKV().putInt(AppC.INVENTORY_TYPE, inventoryType);

            String str = Objects.requireNonNull(mBinding.inventoryParamEt.getText()).toString();
            if (TextUtils.isEmpty(str)) {
                ToastUtil.show("请输入盘点次数/时间");
                return;
            }
            int data = Integer.parseInt(str);
            switch (selectedItemPosition) {
                case 0:
                    MMKV.defaultMMKV().putInt(AppC.INVENTORY_BY_TIME, data);
                    break;
                case 1:
                    MMKV.defaultMMKV().putInt(AppC.INVENTORY_BY_COUNT, data);
                    break;
            }
            ToastUtil.show("保存成功");
            dismiss();
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}
