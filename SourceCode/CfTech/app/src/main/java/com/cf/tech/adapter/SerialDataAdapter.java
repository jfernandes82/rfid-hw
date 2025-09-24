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
import java.util.List;
import java.util.Locale;

public class SerialDataAdapter extends RecyclerView.Adapter<SerialDataAdapter.VH> {

    private final List<byte[]> mDataList = new ArrayList<>();

    @NonNull
    @Override
    public SerialDataAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_serial_data, parent, false);
        return new VH(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull SerialDataAdapter.VH holder, int position) {
        byte[] bytes = mDataList.get(position);
        String string = FormatUtil.bytesToHexStr(bytes);
        holder.mTextView.setText(String.format(Locale.getDefault(), "No.%d: %s", position + 1, string));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void setData(byte[] pData) {
        if (pData != null) {
            mDataList.add(pData);
            notifyItemInserted(mDataList.size());
        }
    }

    public static class VH extends RecyclerView.ViewHolder {

        private final TextView mTextView;

        public VH(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.serial_data_tv);
        }
    }
}
