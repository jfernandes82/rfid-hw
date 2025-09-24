package com.cf.tech.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cf.beans.TagInfoBean;
import com.cf.zsdk.uitl.FormatUtil;
import com.cf.tech.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TagListAdapter extends RecyclerView.Adapter<TagListAdapter.VH> {

    private final List<TagInfoBean> mTagInfoBeanList = new ArrayList<>();
    private final Map<Integer, Integer> mTagQueryCountMap = new HashMap<>();
    private IOnItemClickListener mOnItemClickListener;

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, parent, false);
        return new VH(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.mItemNoTv.setText(String.format(Locale.getDefault(), "No: %d", position + 1));
        TagInfoBean tagInfoBean = mTagInfoBeanList.get(position);
        String str = FormatUtil.bytesToHexStr(tagInfoBean.mEPCNum);
        holder.mDataLenTv.setText(String.format(Locale.getDefault(), "Data Len：%d", tagInfoBean.mEPCNum.length));
        holder.mDataTv.setText(String.format("Data: %s", str));
        holder.mRSSITv.setText(String.format("RSSI: %s", tagInfoBean.mRSSI));
        Integer i = mTagQueryCountMap.get(position);
        holder.mNumTv.setText(String.format(Locale.getDefault(), "Num: %d", i));
        holder.itemView.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemCLick(tagInfoBean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTagInfoBeanList.size();
    }

    public void setData(TagInfoBean pBean) {
        if (pBean == null) return;
        if (mTagInfoBeanList.contains(pBean)) {
            int index = mTagInfoBeanList.indexOf(pBean);
            TagInfoBean old = mTagInfoBeanList.get(index);
            if (old.mRSSI != pBean.mRSSI) {
                mTagInfoBeanList.get(index).mRSSI = pBean.mRSSI;
            }
            Integer count = mTagQueryCountMap.get(index);
            if (count == null) count = 1;
            mTagQueryCountMap.put(index, count + 1);
            notifyItemChanged(index);
            return;
        }
        mTagInfoBeanList.add(pBean);
        int index = mTagInfoBeanList.indexOf(pBean);
        mTagQueryCountMap.put(index, 1);
        notifyItemInserted(index);
    }

    public void setOnItemClickListener(IOnItemClickListener pOnItemClickListener) {
        mOnItemClickListener = pOnItemClickListener;
    }

    public Map<Integer, Integer> getTagQueryCountMap() {
        return mTagQueryCountMap;
    }

    public List<TagInfoBean> getTagInfoBeanList() {
        return mTagInfoBeanList;
    }

    public void clearData() {
        mTagQueryCountMap.clear();
        mTagInfoBeanList.clear();
        notifyDataSetChanged();
    }

    public static class VH extends RecyclerView.ViewHolder {
        private final TextView mItemNoTv;
        private final TextView mDataTv;
        private final TextView mRSSITv;
        private final TextView mNumTv;
        private final TextView mDataLenTv;

        public VH(@NonNull View itemView) {
            super(itemView);
            mItemNoTv = itemView.findViewById(R.id.item_no_tv);
            mDataTv = itemView.findViewById(R.id.data_tv);
            mRSSITv = itemView.findViewById(R.id.rssi_tv);
            mNumTv = itemView.findViewById(R.id.num_tv);
            mDataLenTv = itemView.findViewById(R.id.data_lan_tv);
        }
    }

    public interface IOnItemClickListener {
        void onItemCLick(TagInfoBean pBean);
    }

}
