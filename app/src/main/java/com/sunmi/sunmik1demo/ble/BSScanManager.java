package com.sunmi.sunmik1demo.ble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.SystemClock;

import com.sunmi.sunmik1demo.ble.core.BSScanCore;
import com.sunmi.sunmik1demo.ble.data.model.BSDevice;
import com.sunmi.sunmik1demo.ble.enums.EnumStatus;
import com.sunmi.sunmik1demo.ble.listener.BSScanListener;


/**
 * Created by yaoh on 2018/11/16.
 */

public class BSScanManager implements BSScanListener {

    private static final String TAG = "BSScanManager";

    private BSScanCore mScanCore;
    private BSScanListener mScanListener;

    public BSScanManager(Context context) {
        mScanCore = new BSScanCore(context);
        mScanCore.setScanListener(this);
    }

    public void setScanListener(BSScanListener mListener) {
        this.mScanListener = mListener;
    }

    public void startScanning() {
        mScanCore.startScanning();
    }

    public void stopScan() {
        mScanCore.stopScan();
    }

    @Override
    public void onScanResult(EnumStatus status, final BSDevice device, BluetoothDevice bluetoothDevice) {
        if (mScanListener != null) {
            mScanListener.onScanResult(EnumStatus.STATUS_SCAN_SUCCESS, device,bluetoothDevice);
        }
    }
}
