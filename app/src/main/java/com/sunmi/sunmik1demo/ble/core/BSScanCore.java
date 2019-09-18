package com.sunmi.sunmik1demo.ble.core;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;


import com.sunmi.sunmik1demo.ble.data.model.BSDevice;
import com.sunmi.sunmik1demo.ble.enums.EnumStatus;
import com.sunmi.sunmik1demo.ble.listener.BSScanListener;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by yaoh on 2018/2/28.
 * <p>
 * 不是单例
 */

public class BSScanCore {
    private static final String TAG = "MSScanCore";

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;

    private ScanSettings mScanSettings;

    private BSScanListener mScanListener;

    private ScheduledExecutorService mExecutor;

    public BSScanCore(Context context) {
        Context mContext = context.getApplicationContext();
        mBluetoothAdapter = ((BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE))
                .getAdapter();
    }

    public void setScanListener(BSScanListener mScanListener) {
        this.mScanListener = mScanListener;
    }

    /**
     * 这里如果过滤 uuid的方法 会导致在三星s7手机 搜索变慢的问题 所以改为搜索全部在 回调里进行过滤
     */
    private void initScanSetting() {
        ScanSettings.Builder settingbuilder = new ScanSettings.Builder();
        settingbuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        mScanSettings = settingbuilder.build();
    }

    /**
     * Start scanning for BLE Advertisements (& set it up to stop after a set period of time).
     */
    public void startScanning() {

        initScanSetting();

        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (mBluetoothLeScanner == null) {
            mScanListener.onScanResult(EnumStatus.STATUS_BLE_NOT_OPEN, null,null);
            return;
        }

        if (mExecutor != null) {
            mExecutor.shutdownNow();
        }
        mExecutor = Executors.newSingleThreadScheduledExecutor();
        mBluetoothLeScanner.stopScan(mScanCallback);
        mBluetoothLeScanner.startScan(null, mScanSettings, mScanCallback);
    }

    /**
     * 停止搜索
     */
    public void stopScan() {
        Log.d(TAG, " stopScan----------->");
        if (isBLESwitchOn()) {
            if (mBluetoothLeScanner != null) {
                mBluetoothLeScanner.stopScan(mScanCallback);
            }
        }

        if (mExecutor != null) {
            mExecutor.shutdownNow();
        }
    }

    public static boolean isBLESwitchOn() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.isEnabled();
    }

    /**
     * Custom ScanCallback object - adds to adapter on success, displays error on failure.
     */
    ScanCallback mScanCallback = new ScanCallback() {

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);

        }

        @Override
        public void onScanResult(int callbackType, final ScanResult result) {
            if (mExecutor == null || mExecutor.isShutdown()) {
                return;
            }

            mExecutor.execute(new Runnable() {
                @Override
                public void run() {

                    if (mScanListener != null) {
                        BSDevice device = new BSDevice();
//                        device.setDeviceName(result.getDevice().getName());

                        String deviceName = result.getDevice().getName();
                        if (TextUtils.isEmpty(deviceName)) {
                            deviceName = "NULL";
                        }
                        device.setDeviceName(deviceName);

                        String macAddress = result.getDevice().getAddress();
                        device.setMacAddress(macAddress);

                        int rssi = result.getRssi();
                        device.setRssi(rssi);

                        device.setTime(SystemClock.uptimeMillis());

                        if (mScanListener != null) {
                            mScanListener.onScanResult(EnumStatus.STATUS_SCAN_SUCCESS, device,result.getDevice());
                        }
                    }
                }
            });
        }

        @Override
        public void onScanFailed(int errorCode) {
            if (mScanListener != null) {
                mScanListener.onScanResult(EnumStatus.STATUS_SCAN_FAILED, null,null);
            }
        }
    };

}
