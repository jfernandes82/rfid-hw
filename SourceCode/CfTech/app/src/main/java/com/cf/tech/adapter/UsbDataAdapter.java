package com.cf.tech.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cf.zsdk.uitl.FormatUtil;
import com.cf.tech.R;

import java.util.ArrayList;
import java.util.Locale;

public class UsbDataAdapter extends RecyclerView.Adapter<UsbDataAdapter.VH> {

    private final ArrayList<byte[]> mUsbDataList = new ArrayList<>();

    @NonNull
    @Override
    public UsbDataAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usb_data, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsbDataAdapter.VH holder, int position) {
        holder.mNoTv.setText(String.format(Locale.getDefault(), "No.%d", position + 1));
        String usbDataHexStr = FormatUtil.bytesToHexStr(mUsbDataList.get(position));
        holder.mUsbDataTv.setText(usbDataHexStr);
    }

    @Override
    public int getItemCount() {
        return mUsbDataList.size();
    }

    public void setData(byte[] pData) {
        if (pData == null) return;
        mUsbDataList.add(pData);
        notifyItemInserted(mUsbDataList.size());
    }

    public static class VH extends RecyclerView.ViewHolder {
        private final TextView mNoTv;
        private final TextView mUsbDataTv;

        public VH(@NonNull View itemView) {
            super(itemView);
            mNoTv =   itemView.findViewById(R.id.item_no_tv);
            mUsbDataTv = itemView.findViewById(R.id.item_data_tv);
        }
    }
}
