//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunmi.sunmik1demo.fragment;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Build.VERSION;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.sunmi.sunmik1demo.BaseFragment;
import com.sunmi.sunmik1demo.R;
import com.sunmi.sunmik1demo.adapter.BraceletHeaderAdapter;
import com.sunmi.sunmik1demo.adapter.BraceletHeaderAdapter.MyBluetoothDevice;
import com.sunmi.sunmik1demo.adapter.BraceletHeaderAdapter.OnAddListener;
import com.sunmi.sunmik1demo.bean.blescan.BraceletUserBean;
import com.sunmi.sunmik1demo.bean.blescan.FilterMacBean;
import com.sunmi.sunmik1demo.eventbus.UnlockSingValueCalEvent;
import com.sunmi.sunmik1demo.unlock.BLEUserModel;
import com.sunmi.sunmik1demo.unlock.BleService;
import com.sunmi.sunmik1demo.utils.SharePreferenceUtil;
import com.sunmi.sunmik1demo.view.CustomDialog;
import com.sunmi.sunmik1demo.view.PinEntryDialog;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersTouchListener;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersTouchListener.OnHeaderClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class BraceletFragment extends BaseFragment implements BleService.BLEServiceListener {
    private static final String TAG = BraceletFragment.class.getSimpleName();

    BLEUserModel bleUserModel;
    private BraceletHeaderAdapter adapter;
    private CustomDialog customDialog;
    private List<MyBluetoothDevice> filterDeviceList = new CopyOnWriteArrayList();
    private FrameLayout flBraceletUser;
    private Handler handler = new Handler(Looper.getMainLooper());
    boolean isCalibration;
    private boolean isClicking;
    private List<BraceletUserBean> isConnectDeviceList = new CopyOnWriteArrayList();
    boolean isOpen;
    private ImageView ivBraceletStatue;
    private ImageView ivBraceletUserIcon;
    private LinearLayout llBraceletEmpty;
    private LinearLayout llBraceletStatue;
    private LinearLayout llBraceletUser;
    private RecyclerView recyclerView;
    private RelativeLayout rlBraceletPwd;
    private Switch swBraceletOpen;
    private int mLastFoundDeviceNum;
    private BleService.BLEProxy mProxy;
    ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName var1, IBinder var2) {
            BraceletFragment.this.mProxy = (BleService.BLEProxy) var2;
            mProxy.setBleServiceListener(BraceletFragment.this);
        }

        public void onServiceDisconnected(ComponentName var1) {
        }
    };
    private OnAddListener onAddListener = new OnAddListener() {
        public void onClick(int var1, boolean var2) {
            Log.e(BraceletFragment.TAG, "添加删除设备===" + var1 + "  " + var2 + "" + (BraceletFragment.this.filterDeviceList.get(var1)).getAddress());
            if (!BraceletFragment.this.isClicking) {
                if (BluetoothAdapter.checkBluetoothAddress((BraceletFragment.this.filterDeviceList.get(var1)).getAddress().toUpperCase())) {
                    BraceletFragment.this.isClicking = true;
                    if (var2) {
                        BraceletFragment.this.deletUser(var1);
                    } else {
                        if (BraceletFragment.this.isConnectDeviceList.size() >= 10) {
                            BraceletFragment.this.isClicking = false;
                            BraceletFragment.this.showUser((BraceletFragment.this.filterDeviceList.get(var1)).getAddress(), (BraceletFragment.this.filterDeviceList.get(var1)).isAdd());
                            return;
                        }

                        BraceletFragment.this.setupUser(var1);
                    }

                    BraceletFragment.this.adapter.notifyDataSetChanged();
                    BraceletFragment.this.recyclerView.invalidateItemDecorations();
                    Log.e(BraceletFragment.TAG, "isConnectDeviceList===" + BraceletFragment.this.isConnectDeviceList.size());
                    BraceletFragment.this.handler.postDelayed(new Runnable() {
                        public void run() {
                            BraceletFragment.this.isClicking = false;
                        }
                    }, 500L);
                } else {
                    Toast.makeText(BraceletFragment.this.getContext(), "地址格式有误", Toast.LENGTH_SHORT).show();
                }
            }
        }

        public void onItemClick(int var1) {
            Log.e(BraceletFragment.TAG, "选中===" + var1 + "  " + (BraceletFragment.this.filterDeviceList.get(var1)).getAddress());
            Iterator<MyBluetoothDevice> var2 = filterDeviceList.iterator();

            while (var2.hasNext()) {
                (var2.next()).setCheck(false);
            }

            (BraceletFragment.this.filterDeviceList.get(var1)).setCheck(true);
            BraceletFragment.this.adapter.notifyDataSetChanged();
            BraceletFragment.this.recyclerView.invalidateItemDecorations();
            BraceletFragment.this.showUser((BraceletFragment.this.filterDeviceList.get(var1)).getAddress(), (BraceletFragment.this.filterDeviceList.get(var1)).isAdd());
        }
    };
    private PinEntryDialog pinEntryDialog;
    OnClickListener pinInputListener = new OnClickListener() {
        public void onClick(View var1) {
            switch (var1.getId()) {
                case R.id.tv_cancel:
                    BraceletFragment.this.pinEntryDialog.dismiss();
                    return;
                default:
                    return;
                case R.id.tv_confirm:
                    if (TextUtils.isEmpty(BraceletFragment.this.pinEntryDialog.getPin())) {
                        Toast.makeText(BraceletFragment.this.getContext(), "请输入正确的密码", Toast.LENGTH_SHORT).show();
                    } else {
                        SharePreferenceUtil.setParam(BraceletFragment.this.getContext(), "BLE_PWD_KEY", BraceletFragment.this.pinEntryDialog.getPin());
                        BraceletFragment.this.tvBraceletPwd.setText(BraceletFragment.this.pinEntryDialog.getPin());
                        BraceletFragment.this.pinEntryDialog.dismiss();
                    }
            }
        }
    };

    OnClickListener tipsListener = new OnClickListener() {
        public void onClick(View var1) {
            switch (var1.getId()) {
                case R.id.tv_cancel:
                    BraceletFragment.this.isClicking = true;
                    BraceletFragment.this.swBraceletOpen.setChecked(true);
                    BraceletFragment.this.customDialog.dismiss();
                    return;
                default:
                    return;
                case R.id.tv_confirm:
                    BraceletFragment.this.isOpen = false;
                    BraceletFragment.this.rlBraceletPwd.setAlpha(0.6F);
                    SharePreferenceUtil.setParam(BraceletFragment.this.getContext(), "BLE_KEY", false);
                    BraceletFragment.this.customDialog.dismiss();
                    BraceletFragment.this.disBle();
                    BraceletFragment.this.setBraceletStatue(0);
            }
        }
    };
    private TextView tvBraceletConnectState;
    private TextView tvBraceletMac;
    private TextView tvBraceletPwd;
    private TextView tvBraceletSingvalue;
    private TextView tvBraceletStatue;
    private TextView tvBraceletUserMac;
    private TextView tvBraceletUserName;

    public BraceletFragment() {
    }

    private void connectUnlockServer() {
        this.getActivity().bindService(new Intent(this.getActivity(), BleService.class), this.mServiceConnection, Service.BIND_AUTO_CREATE);
    }

    private void deletUser(int var1) {
        String var3 = (this.filterDeviceList.get(var1)).getAddress();
        boolean var2 = false;
        if ((this.filterDeviceList.get(var1)).isCheck()) {
            var2 = true;
        }

        this.setBraceletStatue(1);
        MyBluetoothDevice var4 = this.filterDeviceList.remove(var1);
        var4.setAdd(false);
        var4.setName(var4.getName().replace("（" + (bleUserModel.getName(var3)) + "）", ""));
        var4.setIcon(0);
        this.filterDeviceList.add(var4);

        bleUserModel.removeUser(var3);
        isConnectDeviceList.clear();
        this.isConnectDeviceList.addAll(bleUserModel.getAllFilterBracelet());
        if (var2) {
            this.showUser(var3, false);
        }

    }

    private void disBle() {
        this.filterDeviceList.clear();
        this.adapter.notifyDataSetChanged();
    }

    private void foundNewDevice(String mac) {
        if (!isOpen) {
            return;
        }
        for (MyBluetoothDevice bluetoothDevice : filterDeviceList) {
            if (bluetoothDevice.getAddress().equals(mac)) {
                return;
            }
        }
        synchronized (filterDeviceList) {
            MyBluetoothDevice myBluetoothDevice = new MyBluetoothDevice(mac, false);
            myBluetoothDevice.setName("YUAN");
            if (bleUserModel.getUserBeanMap().containsKey(mac)) {
                myBluetoothDevice.setName(myBluetoothDevice.getName() + "（" + (this.bleUserModel.getUserBeanMap().get(myBluetoothDevice.getAddress())).getName() + "）");
                myBluetoothDevice.setIcon((this.bleUserModel.getUserBeanMap().get(myBluetoothDevice.getAddress())).getIcon());
                myBluetoothDevice.setAdd(true);

                this.filterDeviceList.add(0, myBluetoothDevice);
            } else {
                this.filterDeviceList.add(myBluetoothDevice);
            }

            if (this.mLastFoundDeviceNum != this.filterDeviceList.size()) {
                this.mLastFoundDeviceNum = this.filterDeviceList.size();
                this.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        BraceletFragment.this.adapter.notifyDataSetChanged();
                    }
                });
            }
        }

    }

    private void initBle() {
        bleUserModel = new BLEUserModel();


        if (this.adapter == null) {
            this.adapter = new BraceletHeaderAdapter(this.filterDeviceList);
            this.adapter.setOnAddClickListener(this.onAddListener);
            this.recyclerView.setAdapter(this.adapter);
            LinearLayoutManager var1 = new LinearLayoutManager(this.getContext(), 1, false);
            this.recyclerView.setLayoutManager(var1);
            final StickyRecyclerHeadersDecoration var2 = new StickyRecyclerHeadersDecoration(this.adapter);
            this.recyclerView.addItemDecoration(var2);
            new StickyRecyclerHeadersTouchListener(this.recyclerView, var2).setOnHeaderClickListener(new OnHeaderClickListener() {
                public void onHeaderClick(View var1, int var2, long var3) {
                }
            });
            this.adapter.registerAdapterDataObserver(new AdapterDataObserver() {
                public void onChanged() {
                    var2.invalidateHeaders();
                }
            });
        }

        this.isConnectDeviceList.clear();
        this.isConnectDeviceList.addAll(bleUserModel.getAllFilterBracelet());
        if (isConnectDeviceList.size() > 0) {
            this.setBraceletStatue(2);

            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    for (BraceletUserBean braceletUserBean : isConnectDeviceList) {
                        MyBluetoothDevice myBluetoothDevice = new MyBluetoothDevice(braceletUserBean.getMac(), true);
                        myBluetoothDevice.setName(braceletUserBean.getName());
                        myBluetoothDevice.setIcon(braceletUserBean.getIcon());
                        myBluetoothDevice.setAdd(true);
                        filterDeviceList.add(0, myBluetoothDevice);
                    }
                    adapter.notifyDataSetChanged();
                }
            });
        } else {
            this.setBraceletStatue(1);
        }
        this.connectUnlockServer();
    }

    private void promptTurnBlutooth() {
        this.startActivity(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"));
    }

    private void setBraceletStatue(int var1) {
        this.llBraceletStatue.setVisibility(View.VISIBLE);
        this.llBraceletEmpty.setVisibility(View.GONE);
        this.llBraceletUser.setVisibility(View.GONE);
        switch (var1) {
            case 0:
                this.ivBraceletStatue.setImageResource(R.drawable.more_bracelet_statue_1);
                this.tvBraceletStatue.setText(R.string.more_bracelet_statue_1);
                return;
            case 1:
                this.ivBraceletStatue.setImageResource(R.drawable.more_bracelet_statue_2);
                this.tvBraceletStatue.setText(R.string.more_bracelet_statue_2);
                return;
            case 2:
                this.ivBraceletStatue.setImageResource(R.drawable.more_bracelet_statue_3);
                this.tvBraceletStatue.setText(R.string.more_bracelet_statue_3);
                return;
            case 3:
                this.llBraceletStatue.setVisibility(View.GONE);
                this.llBraceletEmpty.setVisibility(View.VISIBLE);
                return;
            default:
        }
    }

    private void setupUser(int position) {
        String address = (this.filterDeviceList.get(position)).getAddress();
        boolean ischeck = false;
        if ((this.filterDeviceList.get(position)).isCheck()) {
            ischeck = true;
        }

        MyBluetoothDevice bluetoothDevice = this.filterDeviceList.remove(position);
        bluetoothDevice.setAdd(true);
        List<BraceletUserBean> braceletUserBeans = bleUserModel.getAllBraceletUser();
        ArrayList<BraceletUserBean> temp = new ArrayList();
        for (BraceletUserBean braceletUserBean : braceletUserBeans) {
            if (TextUtils.isEmpty(braceletUserBean.getMac())) {
                temp.add(braceletUserBean);
            }
        }
        position = (new Random()).nextInt(temp.size());
        Log.e(TAG, "setupUser===" + position + temp.size());
        temp.get(position).setMac(address);
        bleUserModel.addUser(temp.get(position));
        this.isConnectDeviceList.clear();
        this.isConnectDeviceList.addAll(bleUserModel.getAllFilterBracelet());

        bluetoothDevice.setIcon((temp.get(position)).getIcon());
        bluetoothDevice.setName(bluetoothDevice.getName() + "（" + (temp.get(position)).getName() + "）");
        this.filterDeviceList.add(0, bluetoothDevice);
        if (ischeck) {
            this.showUser(address, true);
        } else {
            this.setBraceletStatue(2);
        }
    }

    private void showUser(String var1, boolean var2) {
        this.flBraceletUser.setVisibility(View.VISIBLE);
        this.llBraceletEmpty.setVisibility(View.GONE);
        this.llBraceletStatue.setVisibility(View.GONE);
        this.llBraceletUser.setVisibility(View.GONE);
        if (var2) {
            this.llBraceletUser.setVisibility(View.VISIBLE);
            this.tvBraceletMac.setText(var1);
            this.tvBraceletUserMac.setText(var1);
            this.tvBraceletConnectState.setText(R.string.more_bracelet_conncet);
            BraceletUserBean var3 = this.bleUserModel.getUserBeanMap().get(var1);
            this.ivBraceletUserIcon.setImageResource(var3.getIcon());
            this.tvBraceletUserName.setText(var3.getName());
        } else if (this.isConnectDeviceList.size() >= 10) {
            this.setBraceletStatue(3);
        } else {
            this.llBraceletUser.setVisibility(View.VISIBLE);
            this.tvBraceletUserName.setText(R.string.more_bracelet_connect_info);
            this.tvBraceletMac.setText(var1);
            this.tvBraceletUserMac.setText(var1);
            this.tvBraceletConnectState.setText(R.string.more_bracelet_unconncet);
            this.ivBraceletUserIcon.setImageResource(R.drawable.more_bracelet_default_icon_2);
        }
    }

    private void startCalibration(String var1) {

    }

    @Subscribe(
            threadMode = ThreadMode.MAIN
    )
    public void Event(UnlockSingValueCalEvent var1) {
        this.isCalibration = false;
        if (var1.getSingValue() != 0) {
            BraceletUserBean var2 = bleUserModel.getUserBeanMap().get(var1.getAddress());
            var2.setSingValue(var1.getSingValue());
            bleUserModel.addUser(var2);
            Toast.makeText(this.getContext(), "校准成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this.getContext(), "校准失败", Toast.LENGTH_SHORT).show();
        }
    }

    protected void init(View var1) {
//        tvTitle = (TextView) var1.findViewById(R.id.tv_title);
        swBraceletOpen = (Switch) var1.findViewById(R.id.sw_bracelet_open);
        rlBraceletPwd = (RelativeLayout) var1.findViewById(R.id.rl_bracelet_pwd);
        tvBraceletPwd = (TextView) var1.findViewById(R.id.tv_bracelet_pwd);
        recyclerView = (RecyclerView) var1.findViewById(R.id.recycler_bracelet);
        flBraceletUser = (FrameLayout) var1.findViewById(R.id.fl_bracelet_user);
        llBraceletStatue = (LinearLayout) var1.findViewById(R.id.ll_bracelet_statue);
        ivBraceletStatue = (ImageView) var1.findViewById(R.id.iv_bracelet_statue);
        tvBraceletStatue = (TextView) var1.findViewById(R.id.tv_bracelet_statue);
        llBraceletEmpty = (LinearLayout) var1.findViewById(R.id.ll_bracelet_empty);
        llBraceletUser = (LinearLayout) var1.findViewById(R.id.ll_bracelet_user);
        tvBraceletMac = (TextView) var1.findViewById(R.id.tv_bracelet_mac);
        tvBraceletConnectState = (TextView) var1.findViewById(R.id.tv_bracelet_connect_state);
        ivBraceletUserIcon = (ImageView) var1.findViewById(R.id.iv_bracelet_user_icon);
        tvBraceletUserName = (TextView) var1.findViewById(R.id.tv_bracelet_user_name);
        tvBraceletUserMac = (TextView) var1.findViewById(R.id.tv_bracelet_user_mac);
        tvBraceletSingvalue = (TextView) var1.findViewById(R.id.tv_bracelet_singvalue);

    }

    protected void initData(Bundle var1) {
        EventBus.getDefault().register(this);
        this.isOpen = (Boolean) SharePreferenceUtil.getParam(this.getContext(), "BLE_KEY", false);
        this.swBraceletOpen.setChecked(this.isOpen);
        if (this.isOpen) {
            this.initBle();
        } else {
            this.rlBraceletPwd.setAlpha(0.6F);
            this.setBraceletStatue(0);
        }

        this.swBraceletOpen.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton var1, boolean var2) {
                if (var2) {
                    if (!BraceletFragment.this.isClicking) {
                        BraceletFragment.this.isOpen = true;
                        Log.e(BraceletFragment.TAG, "开启手环解锁");
                        SharePreferenceUtil.setParam(BraceletFragment.this.getContext(), "BLE_KEY", var2);
                        BraceletFragment.this.initBle();
                        BraceletFragment.this.rlBraceletPwd.setAlpha(1.0F);
                    }

                    BraceletFragment.this.isClicking = false;
                } else {
                    if (BraceletFragment.this.customDialog == null) {
                        BraceletFragment.this.customDialog = new CustomDialog(BraceletFragment.this.getContext());
                        BraceletFragment.this.customDialog.setOnClickListener(BraceletFragment.this.tipsListener);
                        BraceletFragment.this.customDialog.show();
                        return;
                    }

                    if (!BraceletFragment.this.customDialog.isShowing()) {
                        BraceletFragment.this.customDialog.show();
                        return;
                    }
                }

            }
        });
        this.tvBraceletSingvalue.setOnClickListener(new OnClickListener() {
            public void onClick(View var1) {
                Iterator var2 = BraceletFragment.this.isConnectDeviceList.iterator();

                while (var2.hasNext()) {
                    if (((FilterMacBean) var2.next()).getAddress().equals(BraceletFragment.this.tvBraceletUserMac.getText().toString())) {
                        BraceletFragment.this.startCalibration(BraceletFragment.this.tvBraceletUserMac.getText().toString());
                    }
                }

            }
        });
        this.rlBraceletPwd.setOnClickListener(new OnClickListener() {
            public void onClick(View var1) {
                if (BraceletFragment.this.isOpen && (BraceletFragment.this.pinEntryDialog == null || !BraceletFragment.this.pinEntryDialog.isShowing())) {
                    BraceletFragment.this.pinEntryDialog = new PinEntryDialog(BraceletFragment.this.getContext());
                    BraceletFragment.this.pinEntryDialog.setOnClickListener(BraceletFragment.this.pinInputListener);
                    BraceletFragment.this.pinEntryDialog.show();
                }
            }
        });
        String var2 = (String) SharePreferenceUtil.getParam(this.getContext(), "BLE_PWD_KEY", "1111");
        this.tvBraceletPwd.setText(var2);
    }

    public void onDestroy() {
        if (this.mProxy != null) {
            this.getActivity().unbindService(this.mServiceConnection);
        }

        if (this.pinEntryDialog != null && this.pinEntryDialog.isShowing()) {
            this.pinEntryDialog.dismiss();
        }

        if (this.customDialog != null && this.customDialog.isShowing()) {
            this.customDialog.dismiss();
        }

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        super.onDestroy();
    }

    protected int setView() {
        return R.layout.fragment_bracelet;
    }


    @Override
    public void onAddressAndRssi(String mac, int rssi, BluetoothDevice bluetoothDevice) {
        foundNewDevice(mac);
    }

}
