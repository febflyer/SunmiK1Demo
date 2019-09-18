package com.sunmi.sunmik1demo.unlock;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.sunmi.sunmik1demo.ble.BSScanManager;
import com.sunmi.sunmik1demo.ble.data.model.BSDevice;
import com.sunmi.sunmik1demo.ble.enums.EnumStatus;
import com.sunmi.sunmik1demo.ble.listener.BSScanListener;
import com.sunmi.sunmik1demo.ble.utils.BLEUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class BleService extends Service implements BSScanListener {
    private static final String TAG = BleService.class.getSimpleName();
    public static final int MAX_BLE = 10;
    private boolean isStopRefrshList;
    private Handler handler = new Handler(Looper.myLooper());
    private String mFilterNameAddress = "yuan";


    private BSScanManager mBSScanManager;

    public Map<String, BSDevice> mDeviceMaps = new ConcurrentHashMap<>(MAX_BLE);

    BLEServiceListener bleServiceListener;
    BLEProxy mProxy;

    @Override
    public void onScanResult(EnumStatus status, BSDevice device, BluetoothDevice bluetoothDevice) {
        if (status == EnumStatus.STATUS_SCAN_SUCCESS && device != null && bluetoothDevice != null) {
            if (mDeviceMaps.get(device.getMacAddress()) != null) {
                String cahceDeviceName = mDeviceMaps
                        .get(device.getMacAddress())
                        .getDeviceName();

                if (!TextUtils.equals("NULL", cahceDeviceName)) {
                    device.setDeviceName(cahceDeviceName);
                } else {
                    device.setDeviceName("NULL");
                }
            }
            if (device.getMacAddress().toLowerCase().contains(mFilterNameAddress.toLowerCase())
                    || device.getDeviceName().toLowerCase().contains(mFilterNameAddress.toLowerCase())) {
                Log.i(TAG, "onScanResult==" + "  " + device.getMacAddress());
                mDeviceMaps.put(device.getMacAddress(), device);


                if (bleServiceListener != null) {
                    bleServiceListener.onAddressAndRssi(device.getMacAddress(), device.getRssi(), bluetoothDevice);
                }
            }

        }
    }

    boolean initBle() {
        Log.e(TAG, "蓝牙服务开启");
        if (!BLEUtil.isBLESwitchOn()) {
            Toast.makeText(this, "Bluetooth is not enabled ", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mBSScanManager == null) {
            mBSScanManager = new BSScanManager(this);
            mBSScanManager.setScanListener(this);

        }
        return true;
    }


    void startScanning() {
        if(mBSScanManager!=null) {
            mBSScanManager.startScanning();
        }
    }

    void autoRestartScan() {
        this.handler.postDelayed(new Runnable() {
            public void run() {
                if (!initBle()) {
                    return;
                }
                Log.e(TAG, "autoRestartScan");
                autoRestartScan();
                startScanning();
            }
        }, 30000);
    }

    private void registerBLE() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        this.registerReceiver(this.mReceiver, intentFilter);
    }

    /**
     * 蓝牙变化广播
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            break;
                        case BluetoothAdapter.STATE_ON:
                            if (initBle()) {
                                startScanning();
                            }
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            mBSScanManager.stopScan();

                            break;
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBSScanManager != null) {
            mBSScanManager.stopScan();
        }

        this.unregisterReceiver(this.mReceiver);
        Log.e(TAG, "蓝牙服务关闭");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        registerBLE();
        if (initBle()) {
            startScanning();
//            autoRestartScan();
        }
        if (this.mProxy == null) {
            this.mProxy = new BLEProxy();
        }

        return this.mProxy;
    }


    public interface BLEServiceListener {
        void onAddressAndRssi(String mac, int rssi, BluetoothDevice bluetoothDevice);
    }

    public class BLEProxy extends Binder {

        public void setBleServiceListener(BLEServiceListener bleServiceListener) {
            BleService.this.bleServiceListener = bleServiceListener;
        }
        public void startScan(){
            BleService.this.startScanning();
        }

    }
}
