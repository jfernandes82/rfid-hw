package com.cf.tech.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cf.tech.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ScanDataAdapter extends RecyclerView.Adapter<ScanDataAdapter.VH> {

    private final List<String> mScanDataList = new ArrayList<>();
    private final Map<Integer, Integer> mTagQueryCountMap = new HashMap<>();

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scan_data, parent, false);
        return new VH(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        String string = mScanDataList.get(position);
        holder.mScanDataTv.setText(String.format("Data: %s", string));
        String string1 = mScanDataList.get(position);
        int index = mScanDataList.indexOf(string1);
        Integer i = mTagQueryCountMap.get(index);
        holder.mScanCountTv.setText(String.format(Locale.getDefault(), "Num: %d", i));
    }

    @Override
    public int getItemCount() {
        return mScanDataList.size();
    }

    public void setData(String pScanData) {
        if (TextUtils.isEmpty(pScanData)) return;
        if (mScanDataList.contains(pScanData)) {
            int index = mScanDataList.indexOf(pScanData);
            Integer integer = mTagQueryCountMap.get(index);
            if (integer != null) {
                integer++;
                mTagQueryCountMap.put(index, integer);
                notifyItemChanged(index);
            }
        } else {
            mScanDataList.add(pScanData);
            int index = mScanDataList.indexOf(pScanData);
            mTagQueryCountMap.put(index, 1);
            notifyItemInserted(mScanDataList.size());
        }
    }

    public void clearData() {
        mTagQueryCountMap.clear();
        mScanDataList.clear();
        notifyDataSetChanged();
    }

    public static class VH extends RecyclerView.ViewHolder {
        public TextView mScanDataTv;
        public TextView mScanCountTv;

        public VH(@NonNull View itemView) {
            super(itemView);
            mScanDataTv = itemView.findViewById(R.id.scan_data_tv);
            mScanCountTv = itemView.findViewById(R.id.scan_count_tv);
        }
    }
}
