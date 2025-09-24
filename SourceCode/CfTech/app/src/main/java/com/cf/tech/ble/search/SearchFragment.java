package com.cf.tech.ble.search;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cf.beans.AllParamBean;
import com.cf.beans.CmdData;
import com.cf.beans.GeneralBean;
import com.cf.beans.KeyStateBean;
import com.cf.beans.PermissionParamBean;
import com.cf.beans.TagInfoBean;
import com.cf.ble.interfaces.IOnNotifyCallback;
import com.cf.tech.AppC;
import com.cf.tech.CfApplication;
import com.cf.tech.LoadingFragment;
import com.cf.tech.R;
import com.cf.tech.adapter.TagListAdapter;
import com.cf.tech.ble.BleOperatorActivity;
import com.cf.tech.ble.search.interfaces.IDialogDismissCallback;
import com.cf.tech.ble.tag_operation.TagOperationActivity;
import com.cf.tech.databinding.FragmentSearchBinding;
import com.cf.tech.utils.FileUtil;
import com.cf.tech.utils.ToastUtil;
import com.cf.zsdk.CfSdk;
import com.cf.zsdk.SdkC;
import com.cf.zsdk.cmd.CmdBuilder;
import com.cf.zsdk.cmd.CmdType;
import com.cf.zsdk.uitl.FormatUtil;
import com.cf.zsdk.uitl.LogUtil;
import com.tencent.mmkv.MMKV;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressLint("MissingPermission")
public class SearchFragment extends Fragment implements IOnNotifyCallback, IDialogDismissCallback, TagListAdapter.IOnItemClickListener {

    private FragmentSearchBinding mBinding;
    private TagListAdapter mTagListAdapter;
    private boolean mIsInventory = false;
    private SimpleDateFormat mDateFormat;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SearchViewModel searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        mBinding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = mBinding.getRoot();

        final TextView textView = mBinding.textHome;
        searchViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.tagInfoRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mTagListAdapter = new TagListAdapter();
        mBinding.tagInfoRv.setAdapter(mTagListAdapter);
        mBinding.tagInfoRv.setItemAnimator(null);

        mBinding.queryConfigBtn.setOnClickListener(v -> {
            if (mIsInventory) {
                ToastUtil.show(getResources().getString(R.string.place_stop_inventory_first));
                return;
            }
            LoadingFragment.showLoading(getParentFragmentManager(), getString(R.string.getting_configuration));
            byte[] bytes = CmdBuilder.buildGetAllParamCmd();
            CfSdk.get(SdkC.BLE).writeData(AppC.SERVICE_UUID, AppC.WRITE_UUID, bytes);
            mBinding.getRoot().postDelayed(() -> {
                LoadingFragment.dismissLoading();
                ToastUtil.show(getResources().getString(R.string.get_config_info_timeout));
            }, 5000);

        });

        mBinding.inventoryBtn.setOnClickListener(v -> {
            Button btn = (Button) v;
            String text = btn.getText().toString();
            if (getResources().getString(R.string.start_inventory).equals(text)) {
                doClearData();
                btn.setText(R.string.stop_inventory);
                startInventory();
            } else {
                btn.setText(R.string.start_inventory);
                stopInventory();
            }
        });

        //设置查询配置参数
        mBinding.inventoryBtn.setOnLongClickListener(v -> {
            InventoryParamDialogFragment inventoryParamDialogFragment = new InventoryParamDialogFragment();
            inventoryParamDialogFragment.show(getParentFragmentManager(), InventoryParamDialogFragment.class.getName());
            return true;
        });

        //导出文件
        mBinding.exportToFileBtn.setOnClickListener(v -> {

            if (mIsInventory) {
                ToastUtil.show(getResources().getString(R.string.place_stop_inventory_first));
                return;
            }

            if (mTagListAdapter == null) return;

            if (mTagListAdapter.getItemCount() == 0) {
                ToastUtil.show(getString(R.string.empty_data_list));
                return;
            }

            exportToFile(mTagListAdapter.getTagInfoBeanList(), mTagListAdapter.getTagQueryCountMap());
        });

        mBinding.clearDataBtn.setOnClickListener(v -> doClearData());
    }

    /**
     * 导出文件
     */
    private synchronized void exportToFile(List<TagInfoBean> pTagInfoBeanList, Map<Integer, Integer> pTagQueryCountMap) {
        if (mDateFormat == null) {
            mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        }
        String format = mDateFormat.format(System.currentTimeMillis());

        LoadingFragment.showLoading(getParentFragmentManager(), "数据导出中…");
        CfApplication.sCtp.submit(() -> {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < pTagInfoBeanList.size(); i++) {
                TagInfoBean tagInfoBean = pTagInfoBeanList.get(i);
                builder.append("Data ");
                builder.append(i + 1);
                builder.append(": ");
                byte[] epcNum = tagInfoBean.mEPCNum;
                builder.append(FormatUtil.bytesToHexStr(epcNum));
                builder.append("\n");
                builder.append("Data Len: ");
                builder.append(epcNum.length);
                builder.append("  ");
                builder.append("RSSI: ");
                builder.append(tagInfoBean.mRSSI);
                builder.append("  ");
                builder.append("Num: ");
                Integer i1 = pTagQueryCountMap.get(i);
                builder.append(i1);
                builder.append("\n\n");
            }
            //File targetFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + File.separator + requireContext().getPackageName() + File.separator + format + ".txt");
            File targetFile = new File(requireContext().getExternalFilesDir(null) + File.separator + format + ".txt");
            LogUtil.d("target file path == > " + targetFile.getPath());
            String msg = FileUtil.writeDataToFile(builder.toString(), targetFile, true) ? "数据导出成功" : "数据写入失败";
            mBinding.getRoot().post(() -> {
                ToastUtil.show(msg);
                LoadingFragment.dismissLoading();
            });
        });
    }

    private void doClearData() {
        mBinding.tagTotalCountTv.setText("0");
        mBinding.queryTotalCountTv.setText("0");
        if (mTagListAdapter != null) {
            mTagListAdapter.clearData();
        }
    }

    private void startInventory() {
        int inventoryType = MMKV.defaultMMKV().getInt(AppC.INVENTORY_TYPE, 0);
        int invParam = 0;
        if (inventoryType == 0) {
            invParam = MMKV.defaultMMKV().getInt(AppC.INVENTORY_BY_TIME, 0);
        } else if (inventoryType == 1) {
            invParam = MMKV.defaultMMKV().getInt(AppC.INVENTORY_BY_COUNT, 0);
        }
        byte[] bytes = CmdBuilder.buildInventoryISOContinueCmd((byte) inventoryType, invParam);
        CfSdk.get(SdkC.BLE).writeData(AppC.SERVICE_UUID, AppC.WRITE_UUID, bytes);
        mIsInventory = true;
    }

    private void stopInventory() {
        byte[] bytes = CmdBuilder.buildStopInventoryCmd();
        CfSdk.get(SdkC.BLE).writeData(AppC.SERVICE_UUID, AppC.WRITE_UUID, bytes);
        mIsInventory = false;
    }

    @Override
    public void onStart() {
        super.onStart();
        BleOperatorActivity bleOperatorActivity = (BleOperatorActivity) getActivity();
        if (bleOperatorActivity != null && bleOperatorActivity.getDeviceInfoBean() != null) {
            if (bleOperatorActivity.getDeviceInfoBean().mFirmVer.contains("H103")) {
                mBinding.adventureConfigRl.setVisibility(View.VISIBLE);
                mBinding.adventureConfigBtn.setOnClickListener(v -> {
                    if (mIsInventory) {
                        ToastUtil.show(getString(R.string.place_stop_inventory_first));
                        return;
                    }
                    byte[] bytes = CmdBuilder.buildGetPermissionParamCmd();
                    LoadingFragment.showLoading(getParentFragmentManager(), getString(R.string.get_config_info));
                    CfSdk.get(SdkC.BLE).writeData(AppC.SERVICE_UUID, AppC.WRITE_UUID, bytes);
                    LoadingFragment.dismissLoadingTimeOut(5000, getString(R.string.get_config_info_timeout));
                });
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mIsInventory) {
            mBinding.inventoryBtn.setText(R.string.start_inventory);
        }
        CfSdk.get(SdkC.BLE).setOnNotifyCallback(this);
        if (mTagListAdapter != null) {
            mTagListAdapter.setOnItemClickListener(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTagListAdapter != null) {
            mTagListAdapter.setOnItemClickListener(null);
        }
        //停止盘点
        if (mIsInventory) stopInventory();
        CfSdk.get(SdkC.BLE).setOnNotifyCallback(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Handler handler = mBinding.getRoot().getHandler();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        mBinding = null;
    }

    @Override
    public void onNotify(int pCmdType, CmdData pCmdData) {
        switch (pCmdType) {
            case CmdType.TYPE_INVENTORY:
                //盘点操作
                TagInfoBean tagInfoBean = (TagInfoBean) pCmdData.getData();
                if (mTagListAdapter == null) return;
                mBinding.getRoot().post(() -> {
                    if (tagInfoBean.mStatus == 0x12) {
                        mBinding.inventoryBtn.setText(R.string.start_inventory);
                        mIsInventory = false;
                        return;
                    }
                    //过滤状态为0x00的，但是没有数据的情况
                    if (tagInfoBean.mEPCNum == null) return;
                    mBinding.tagInfoTipsTv.setVisibility(View.GONE);
                    mTagListAdapter.setData(tagInfoBean);
                    //标签总数
                    mBinding.tagTotalCountTv.setText(String.format(Locale.getDefault(), "%d", mTagListAdapter.getItemCount()));
                    //查询总数
                    int integer = Integer.parseInt((String) mBinding.queryTotalCountTv.getText()) + 1;
                    mBinding.queryTotalCountTv.setText(String.format(Locale.getDefault(), "%d", integer));
                });

                break;
            case CmdType.TYPE_STOP_INVENTORY:
                //停止盘点操作
                GeneralBean bean2 = (GeneralBean) pCmdData.getData();
                mBinding.getRoot().post(() -> ToastUtil.show(bean2.mMsg));
                break;
            case CmdType.TYPE_GET_ALL_PARAM:
                AllParamBean allParamBean = (AllParamBean) pCmdData.getData();
                LoadingFragment.dismissLoading();
                mBinding.getRoot().getHandler().removeCallbacksAndMessages(null);
                //弹出设置界面
                QueryConfigDialogFragment queryConfigDialogFragment = new QueryConfigDialogFragment();
                queryConfigDialogFragment.setIFragmentStateChangeCallback(this);
                Bundle bundle = new Bundle();
                bundle.putSerializable(QueryConfigDialogFragment.class.getName(), allParamBean);
                queryConfigDialogFragment.setArguments(bundle);
                queryConfigDialogFragment.show(getParentFragmentManager(), QueryConfigDialogFragment.class.getName());
                break;
            case CmdType.TYPE_GET_OR_SET_PERMISSION:
                PermissionParamBean permissionParamBean = (PermissionParamBean) pCmdData.getData();
                LoadingFragment.dismissLoading();
                if (permissionParamBean.mStatus != 0) {
                    ToastUtil.show(permissionParamBean.mMsg);
                } else {
                    AdvancedSetupDialogFragment advancedSetupDialogFragment = new AdvancedSetupDialogFragment();
                    advancedSetupDialogFragment.setIFragmentStateChangeCallback(SearchFragment.this);
                    Bundle bundle1 = new Bundle();
                    bundle1.putSerializable(AdvancedSetupDialogFragment.class.getName(), permissionParamBean);
                    advancedSetupDialogFragment.setArguments(bundle1);
                    advancedSetupDialogFragment.show(getParentFragmentManager(), AdvancedSetupDialogFragment.class.getName());
                }
                break;
            case CmdType.TYPE_KEY_STATE:
                KeyStateBean bean = (KeyStateBean) pCmdData.getData();
                mBinding.getRoot().post(() -> {
                    if (bean.mKeyState == 0x01) {
                        //开始
                        mBinding.inventoryBtn.setText(R.string.stop_inventory);
                        mBinding.inventoryBtn.setEnabled(false);
                        mIsInventory = true;
                        if (mTagListAdapter != null) {
                            mTagListAdapter.clearData();
                        }
                    } else if (bean.mKeyState == 0x02) {
                        //结束
                        mBinding.inventoryBtn.setText(R.string.start_inventory);
                        mBinding.inventoryBtn.setEnabled(true);
                        mIsInventory = false;
                    }
                });
                break;
            default:
                break;
        }
    }


    @Override
    public void onDialogDismiss() {
        CfSdk.get(SdkC.BLE).setOnNotifyCallback(this);
    }

    @Override
    public void onItemCLick(TagInfoBean pBean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", pBean);
        TagOperationActivity.startActivity(getContext(), TagOperationActivity.class, bundle);
    }
}