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

public class LanDeviceDataAdapter extends RecyclerView.Adapter<LanDeviceDataAdapter.VH> {

    private final ArrayList<byte[]> mArrayList = new ArrayList<>();

    @NonNull
    @Override
    public LanDeviceDataAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lan_device_data, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LanDeviceDataAdapter.VH holder, int position) {
        byte[] bytes = mArrayList.get(position);
        holder.mTextView.setText(String.format("Data: %s", FormatUtil.bytesToHexStr(bytes)));
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public void setData(byte[] pData) {
        if (pData == null) {
            return;
        }
        if (mArrayList.contains(pData)) {
            return;
        }
        mArrayList.add(pData);
        notifyItemInserted(mArrayList.size());

    }

    public void clearData() {
        mArrayList.clear();
        notifyDataSetChanged();
    }

    public static class VH extends RecyclerView.ViewHolder {

        private TextView mTextView;

        public VH(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.lan_device_data_tv);
        }
    }
}
