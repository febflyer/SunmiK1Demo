//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunmi.sunmik1demo.unlock;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.bst.bsbandlib.listeners.BSConnectDeviceListener2;
import com.bst.bsbandlib.listeners.BSGetCustomizedWordEnableListener;
import com.bst.bsbandlib.listeners.BSSetCustomizedWordEnableListener;
import com.bst.bsbandlib.listeners.BSSetCustomizedWordListener;
import com.bst.bsbandlib.listeners.BSUpdateBandShakeListener;
import com.bst.bsbandlib.sdk.BSBandSDKManager;
import com.bst.bsbandlib.sdk.BSDevFuncManager;
import com.bst.bsbandlib.sdk.EnumCmdStatus;
import com.sunmi.sunmik1demo.ble.BSAdvertiserManager;
import com.sunmi.sunmik1demo.ble.data.model.BSDevice;
import com.sunmi.sunmik1demo.ble.listener.BSAdvertiserListener;
import com.sunmi.sunmik1demo.eventbus.UnlockSuccessEvent;
import com.sunmi.sunmik1demo.ui.UnlockActivity;
import com.sunmi.sunmik1demo.utils.SharePreferenceUtil;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.sunmi.sunmik1demo.unlock.BraceletPresenter.DEFAULT_LOCK_VALUE;
import static com.sunmi.sunmik1demo.unlock.BraceletPresenter.DEFAULT_SIGN_VALUE;

public class UnlockServer extends Service implements BleService.BLEServiceListener, BSAdvertiserListener {
    public static final String BLE_KEY = "BLE_KEY";
    public static final String BLE_PWD_KEY = "BLE_PWD_KEY";
    private static final String TAG = UnlockServer.class.getSimpleName();
    boolean isOpen;
    private BraceletPresenter mBraceletPresenter;
    private BLEUserModel bleUserModel;
    private UnlockServer.Proxy mProxy;
    BleService.BLEProxy bleProxy;
    private BSAdvertiserManager mBSAdvertiserManager;
    private Map<String, BluetoothGatt> bluetoothDeviceMap = new HashMap<>();
    private Map<String, Long> bluetoothHaveTime = new HashMap<>();
    private ScheduledExecutorService mExecutor;
    private static int REFRESH_INTVAL = 5000;//无法搜索到手环之后，判断该手环离开的时间

    private static int leaveTime = 15000;

    private List<String> leaveMac = new ArrayList<>();

    private boolean isLeaving = false;
    private BSBandSDKManager mSDK;
    private boolean isConnect;
    int i;
    private Handler myhandler = new Handler(Looper.myLooper());

    public static int mParam_A = 65;
    public static float mParam_n = 2.5f;

    @Override
    public void onAddressAndRssi(String mac, int rssi, BluetoothDevice bluetoothDevice) {
        if (bleUserModel.getUserBeanMap().containsKey(mac)) {
            if (i > 100) {
                i = 0;
            }
            float distance = (float) Math.pow(10, (Math.abs(rssi) - mParam_A) / (10 * mParam_n));
            if(Math.abs(rssi) >= DEFAULT_LOCK_VALUE){
                Log.e(TAG, "onAddressAndRssi==" + mac + "  " + rssi + "  距离/米=" + distance + "   " + i++);
            }else if(Math.abs(rssi) <= DEFAULT_SIGN_VALUE){
                Log.i(TAG, "onAddressAndRssi==" + mac + "  " + rssi + "  距离/米=" + distance + "   " + i++);
            }else {
                Log.w(TAG, "onAddressAndRssi==" + mac + "  " + rssi + "  距离/米=" + distance + "   " + i++);
            }
            bluetoothHaveTime.put(mac, SystemClock.uptimeMillis());
            mBraceletPresenter.addConnectAddress(mac);
            mBraceletPresenter.setAddressAndRssi(mac, rssi);
//            addBluetoothGatt(mac, bluetoothDevice);
        }
    }

    private void addBluetoothGatt(String mac, BluetoothDevice bluetoothDevice) {
        connectDevice(mac);
        if (bluetoothDeviceMap.containsKey(mac) && !bluetoothDeviceMap.get(mac).connect()) {
            bluetoothDeviceMap.get(mac).close();
        }
        BluetoothGatt bluetoothGatt = bluetoothDevice.connectGatt(this, false, bluetoothGattCallback);
        bluetoothDeviceMap.put(mac, bluetoothGatt);


    }

    /**
     * 连接设备
     */
    private void connectDevice(String mDeviceMacAddress) {
        if (isConnect) {
            return;
        }
        isConnect = true;
        Log.e(TAG, "connectDevice===");

        mSDK.connectDevice(mDeviceMacAddress, false, new BSConnectDeviceListener2() {
            @Override
            public void onRequestCheckDevice(BSDevFuncManager.BSDevFunc bsDevFunc, BSDeviceConnectCheckListener checkListener) {
                checkListener.onDeviceConnectChecked(true, "000102030405060708090a0b0c0d0e0f");
                Log.e(TAG, "onRequestCheckDevice===");
            }

            @Override
            public void onDisconnect() {
                Log.e(TAG, "onDisconnect===");
                isConnect = false;
            }

            @Override
            public void onFailedToConnect() {
                Log.e(TAG, "onFailedToConnect===");
                isConnect = false;

            }

            @Override
            public String onReuqestVerificationCode() {
                StringBuffer sb = new StringBuffer();
                Random rand = new Random(System.currentTimeMillis());
                for (int i = 0; i < 6; i++) {
                    int num = rand.nextInt(10);
                    sb.append(Integer.toString(num));
                }

                final String code = sb.toString();

                Log.e(TAG, "onReuqestVerificationCode===" + "  " + code);

                return code;
            }

            @Override
            public void onConnectSucceed() {
//                Toast.makeText(DeviceActivity.this, "Connect Device Succeed !", Toast.LENGTH_LONG)
//                        .show();
                Log.e(TAG, "onConnectSucceed===" + "  ");
                isConnect = true;
            }
        });
    }

    private void showToast(String var1) {
        Toast.makeText(this.getApplicationContext(), var1, Toast.LENGTH_SHORT).show();
    }

    private void startScan() {
        if (mBraceletPresenter == null) {
            mBraceletPresenter = new BraceletPresenter(this);
            bleUserModel = new BLEUserModel();
            mBraceletPresenter.setBraceletUser(bleUserModel.getUserBeanMap());
            mBSAdvertiserManager = new BSAdvertiserManager();
            mBSAdvertiserManager.setBSAdvertiserListener(this);
            mExecutor = Executors.newSingleThreadScheduledExecutor();
            mExecutor.schedule(mTask, REFRESH_INTVAL, TimeUnit.MILLISECONDS);

            mSDK = BSBandSDKManager.getSDKManager();
            mSDK.loadConfig("SHOW_LOG_DEBUG");
        }
    }

    Runnable mTask = new Runnable() {

        @Override
        public void run() {
            if (mExecutor.isShutdown()) {
                return;
            }
            if (mBraceletPresenter != null) {
//                updateRemoteRssi();
                //判断是否设备已经离开
                delayRemoveMac();

            }
            mExecutor.schedule(mTask, REFRESH_INTVAL, TimeUnit.MILLISECONDS);
        }
    };

    private void updateRemoteRssi() {
        Iterator<String> iterator = bluetoothDeviceMap.keySet().iterator();
        while (iterator.hasNext()) {
            String s = iterator.next();
            //防止连续读取 ，无返回结果问题
            try {
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //主动读取信号强度
            readRemoteRssi(s);
        }
    }

    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.w(TAG, "onConnectionStateChange===" + newState + ">>>>>" + gatt.getDevice().getAddress());
            String mac = gatt.getDevice().getAddress();
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mBraceletPresenter.addConnectAddress(mac);
                leaveMac.remove(mac);

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                leaveMac.add(mac);
                delayRemoveMac(mac);
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            Log.i(TAG, "onReadRemoteRssi===" + rssi + ">>>>>" + gatt.getDevice().getAddress());
            mBraceletPresenter.setAddressAndRssi(gatt.getDevice().getAddress(), rssi);

        }
    };


    public boolean readRemoteRssi(String mac) {
        if (bluetoothDeviceMap.containsKey(mac) && !leaveMac.contains(mac)) {
            return bluetoothDeviceMap.get(mac).readRemoteRssi();
        }
        return false;
    }

    /**
     * 使用gatt
     *
     * @param mac
     */
    private void delayRemoveMac(String mac) {
        if (isLeaving) {
            return;
        }
        isLeaving = true;
        myhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (String s : leaveMac) {
                    bluetoothDeviceMap.get(s).close();
                    bluetoothDeviceMap.remove(s);
                    mBraceletPresenter.disconnected(s);
                }
                leaveMac.clear();
                isLeaving = false;
            }
        }, leaveTime);
    }

    /**
     *
     */
    private void delayRemoveMac() {
        for (String s : bluetoothHaveTime.keySet()) {
            if (Math.abs(bluetoothHaveTime.get(s) - SystemClock.uptimeMillis()) >= leaveTime) {
                Log.e(TAG, "delayRemoveMac===" + bluetoothHaveTime.get(s) + ">>>>>" + s);
                bluetoothHaveTime.remove(s);
                mBraceletPresenter.disconnected(s);
            }
        }
    }

    @Nullable
    public IBinder onBind(Intent var1) {
        if (!this.isOpen) {
            this.isOpen = (Boolean) SharePreferenceUtil.getParam(this, "BLE_KEY", false);
            Log.e(TAG, "onBind>>>>>>" + this.isOpen);
            if (this.isOpen) {
                startBLEService();
            }
        }

        if (this.mProxy == null) {
            this.mProxy = new UnlockServer.Proxy();
        }
        EventBus.getDefault().register(this);

        return this.mProxy;
    }

    private void startBLEService() {
        this.bindService(new Intent(this, BleService.class), this.mServiceConnection, Service.BIND_AUTO_CREATE);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bleProxy = (BleService.BLEProxy) service;
            bleProxy.setBleServiceListener(UnlockServer.this);
            startScan();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bleProxy = null;
        }
    };

    public boolean onUnbind(Intent var1) {

        if (this.mBraceletPresenter != null) {
            this.mBraceletPresenter.disconnectedAll();
        }
        if (bleProxy != null) {
            unbindService(mServiceConnection);
        }

        if (mExecutor != null) {
            mExecutor.shutdownNow();
        }
        if (isConnect) {
            mSDK.disconnectDevice();
        }
        EventBus.getDefault().unregister(this);

        return super.onUnbind(var1);
    }

    @Override
    public void onAdvertiserResult(boolean isSuccess) {

    }

    @Override
    public void onAdvertiserData(String data) {

    }

    public class Proxy extends Binder {
        public Proxy() {
        }

        public void updateAllUser() {
            bleUserModel.updateUserBeanMap();
            Iterator<String> iterator = bluetoothDeviceMap.keySet().iterator();
            if (iterator.hasNext()) {
                String mac = iterator.next();
                if (!bleUserModel.getUserBeanMap().containsKey(mac)) {
                    bluetoothDeviceMap.get(mac).disconnect();
                    bluetoothDeviceMap.get(mac).close();
                    iterator.remove();
                }
            }

            mBraceletPresenter.setBraceletUser(bleUserModel.getUserBeanMap());
            bleProxy.setBleServiceListener(UnlockServer.this);
            bleProxy.startScan();
        }

        public int getSignValue() {
            return UnlockServer.this.mBraceletPresenter.getSignValue();
        }


        public void setDedalyTime(int time) {
            REFRESH_INTVAL = time;

        }


        public void setSignValue(int var1) {
            UnlockServer.this.mBraceletPresenter.setSignValue(var1);
        }

        public void startCalibration(String var1) {

        }

        public void startLockDomain() {
            if (mBraceletPresenter != null) {
                UnlockServer.this.mBraceletPresenter.startLockDomain();
            }
        }


        public void close() {
            isOpen = false;
            if (mBraceletPresenter != null) {
                mBraceletPresenter.disconnectedAll();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(UnlockSuccessEvent var1) {
        int groupID = Integer.parseInt("0", 16);
        String networkID = "FCFBFA";
        String msg = "已解锁设备";
        sendMsgToBLE(msg, var1.getAddress().replaceAll(":", ""), groupID, networkID);

    }

    private void sendMsgToBLE(String content, String macAddress, int groupID, String networkID) {
        if (groupID == 0) {
            mBSAdvertiserManager.startAdvertiserDataWithDevice(networkID, macAddress, content);
        } else if (groupID == 0xff) {
            mBSAdvertiserManager.startAdvertiserDataWithNetwork(networkID, content);
        } else {
            mBSAdvertiserManager.startAdvertiserDataWithGroup(networkID, groupID, content);
        }
//        if (isConnect) {
//            mSDK.getCustomizedWordEnable(new BSGetCustomizedWordEnableListener() {
//
//                @Override
//                public void onGetCustomizedWordEnable(EnumCmdStatus enumCmdStatus, boolean b) {
//                    Log.e("@@@", "onGetCustomizedWordEnable==" + enumCmdStatus.name());
//                    if (b) {
//                        sendMsg("解锁成功0000");
//                    } else {
//                        setEnableSendMsg("解锁成功0000");
//                    }
//                }
//            });
//        }

    }


    private void startVibrate() {
        mSDK.updateBandShake(0, 5, 5, new BSUpdateBandShakeListener() {
            @Override
            public void onUpdateBandShake(EnumCmdStatus enumCmdStatus) {
                Log.e(TAG, "onUpdateBandShake==" + enumCmdStatus.name());
            }
        });
    }

    private void sendMsg(String msg) {
        mSDK.setCustomizedWord(msg, new BSSetCustomizedWordListener() {
            @Override
            public void onSetCustomizedWord(EnumCmdStatus enumCmdStatus) {
                Log.e(TAG, "onSetCustomizedWord==" + enumCmdStatus.name());
                if (enumCmdStatus == EnumCmdStatus.CMD_STATUS_CMD_EXECUTE_SUCCEED) {
//                    startVibrate();
                }

            }
        });
    }

    private void setEnableSendMsg(final String msg) {
        mSDK.setCustomizedWordEnable(true, new BSSetCustomizedWordEnableListener() {
            @Override
            public void onSetCustomizedWordEnable(EnumCmdStatus enumCmdStatus) {
                Log.e(TAG, "onSetCustomizedWordEnable==" + enumCmdStatus.name());
                if (enumCmdStatus == EnumCmdStatus.CMD_STATUS_CMD_EXECUTE_SUCCEED) {
                    sendMsg(msg);
                }
            }
        });
    }

}
