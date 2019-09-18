package com.sunmi.sunmik1demo.ble.listener;


import android.bluetooth.BluetoothDevice;

import com.sunmi.sunmik1demo.ble.data.model.BSDevice;
import com.sunmi.sunmik1demo.ble.enums.EnumStatus;

/**
 * Created by yaoh on 2018/11/16.
 */

public interface BSScanListener {

    public void onScanResult(EnumStatus status, BSDevice device, BluetoothDevice bluetoothDevice);

}
