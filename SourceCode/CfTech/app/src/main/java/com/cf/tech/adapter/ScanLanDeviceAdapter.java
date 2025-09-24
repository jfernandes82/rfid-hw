package com.cf.tech.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cf.beans.RemoteNetParaBean;
import com.cf.tech.R;
import com.cf.tech.utils.ToastUtil;
import com.cf.zsdk.cmd.CmdHandler;
import com.cf.zsdk.uitl.FormatUtil;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;


public class ScanLanDeviceAdapter extends RecyclerView.Adapter<ScanLanDeviceAdapter.VH> {

    private final ArrayList<DatagramPacket> mDatagramPackets = new ArrayList<>();
    private IOnItemClickListener mIOnItemClickListener;

    @NonNull
    @Override
    public ScanLanDeviceAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lan_device, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScanLanDeviceAdapter.VH holder, int position) {
        DatagramPacket datagramPacket = mDatagramPackets.get(position);
        byte[] data = datagramPacket.getData();

        holder.mHexDataText.setText(String.format("Hex数据: %s", FormatUtil.bytesToHexStr(data)));
        holder.mIpAddrText.setText(String.format("IP地址: %s", datagramPacket.getAddress().toString()));

        holder.itemView.setOnClickListener(v -> {
            if (mIOnItemClickListener != null) {
                // TODO: 2024-8-6 解析数据
                int cmdType = CmdHandler.getCmdType(data);
                if (cmdType == -1) {
                    ToastUtil.show("this device can't be connect");
                    return;
                }
                RemoteNetParaBean bean = (RemoteNetParaBean) CmdHandler.handleCmd(cmdType, data);
                if (bean == null) {
                    ToastUtil.show("unknown device");
                    return;
                }
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < bean.mIpAddr.length; i++) {
                    builder.append(bean.mIpAddr[i] & 0xff).append(".");
                }
                builder.deleteCharAt(builder.length() - 1);
                int ipPort = FormatUtil.byteArrayToInt(bean.mPort);
                mIOnItemClickListener.onItemClick(builder.toString(), ipPort);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatagramPackets.size();
    }

    public void setData(DatagramPacket pPacket) {
        if (pPacket == null) return;
        for (int i = 0; i < mDatagramPackets.size(); i++) {
            DatagramPacket datagramPacket = mDatagramPackets.get(i);
            InetAddress address = datagramPacket.getAddress();
            InetAddress address1 = pPacket.getAddress();
            byte[] data = datagramPacket.getData();
            byte[] data1 = pPacket.getData();
            //地址相同且数据相同，认为是同一个广播
            if (Arrays.equals(data, data1) && address.equals(address1)) return;
        }
        mDatagramPackets.add(pPacket);
        notifyItemInserted(mDatagramPackets.size());
    }

    public void clearData() {
        mDatagramPackets.clear();
        notifyDataSetChanged();
    }

    public void setIOnItemClickListener(IOnItemClickListener pIOnItemClickListener) {
        mIOnItemClickListener = pIOnItemClickListener;
    }

    public interface IOnItemClickListener {
        void onItemClick(String pIpAddr, int pPort);
    }

    public static class VH extends RecyclerView.ViewHolder {

        private final TextView mHexDataText;
        private final TextView mIpAddrText;

        public VH(@NonNull View itemView) {
            super(itemView);
            mHexDataText = itemView.findViewById(R.id.hex_data_tv);
            mIpAddrText = itemView.findViewById(R.id.ip_addr_tv);
        }
    }
}
