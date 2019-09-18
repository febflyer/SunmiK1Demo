package com.sunmi.sunmik1demo.ble.core;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.util.Log;

import com.sunmi.sunmik1demo.MyApplication;
import com.sunmi.sunmik1demo.ble.utils.BLEUtil;

/**
 * Created by yaoh on 2018/4/3.
 */

public class BSAdvertiserCore {
    private static final String TAG = "BSAdvertiserCore";

    protected BluetoothManager mBluetoothManager;
    protected BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    protected BluetoothAdapter mBluetoothAdapter;
    protected AdvertiseSettings mAdvertiseSetting;

    private AdvertiseCallback mCallback;

    public BSAdvertiserCore(AdvertiseCallback callback) {
        mCallback = callback;

        Context mContext = MyApplication.getInstance();
        mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        initAdvertiseSetting();
    }

    private void initAdvertiseSetting() {
        AdvertiseSettings.Builder settingsBuilder = new AdvertiseSettings.Builder();
        settingsBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
        settingsBuilder.setTimeout(0);
        mAdvertiseSetting = settingsBuilder.build();
    }


    /**
     * 开始广播数据
     *
     * @param data
     */
    public void startAdvertiseData(AdvertiseData data) {
        if (!BLEUtil.isBLESwitchOn()) {
            mAdvertiseCallback.onStartFailure(AdvertiseCallback.ADVERTISE_FAILED_INTERNAL_ERROR);
            return;
        }

        mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);   // 先停止广播
        mBluetoothLeAdvertiser.startAdvertising(mAdvertiseSetting, data, mAdvertiseCallback);
    }

    /**
     * 停止发送广播
     */
    public void stopAdvertiseData() {
        Log.d(TAG, "stopAdvertiseData------>");

        if (mBluetoothLeAdvertiser != null) {
            mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);   // 停止广播
        }
    }


    AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {

        @Override
        public void onStartFailure(int errorCode) {
            Log.d(TAG, "Advertising failed errorCode ---> " + errorCode);
            mCallback.onStartFailure(errorCode);
        }

        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
//            LogTool.LogE_DEBUG(TAG, "Advertising onStartSuccess--->");
            mCallback.onStartSuccess(settingsInEffect);
        }
    };
}
