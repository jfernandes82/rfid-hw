package com.cf.tech.adapter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cf.tech.R;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("MissingPermission")
public class BtDeviceListAdapter extends RecyclerView.Adapter<BtDeviceListAdapter.VH> {

    private OnItemClickListener mOnItemClickListener;
    private final List<BluetoothDevice> mBtDeviceList = new ArrayList<>();

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ble_device, parent, false);
        return new VH(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        BluetoothDevice bluetoothDevice = mBtDeviceList.get(position);

        String name = bluetoothDevice.getName();
        if (TextUtils.isEmpty(name)) {
            name = "N/A";
        }
        holder.mDeviceName.setText(name);
        String address = bluetoothDevice.getAddress();
        holder.mDeviceAddress.setText(address);
        holder.itemView.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.OnItemClick(bluetoothDevice);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBtDeviceList.size();
    }

    public void setData(BluetoothDevice pDevice) {
        if (pDevice == null) return;
        if (!mBtDeviceList.contains(pDevice)) {
            mBtDeviceList.add(pDevice);
            notifyItemInserted(mBtDeviceList.size());
        }
    }

    public void setDataForCf(BluetoothDevice pDevice) {
        if (pDevice == null) return;
        if (!mBtDeviceList.contains(pDevice)) {
            mBtDeviceList.add(0, pDevice);
            notifyItemInserted(0);
        }
    }


    public List<BluetoothDevice> getData() {
        return mBtDeviceList;
    }

    public void clearData() {
        mBtDeviceList.clear();
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener pOnItemClickListener) {
        mOnItemClickListener = pOnItemClickListener;
    }

    public static class VH extends RecyclerView.ViewHolder {

        private final TextView mDeviceName;
        private final TextView mDeviceAddress;

        public VH(@NonNull View itemView) {
            super(itemView);
            mDeviceName = itemView.findViewById(R.id.device_name_tv);
            mDeviceAddress = itemView.findViewById(R.id.device_address_tv);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(BluetoothDevice pDevice);
    }
}
