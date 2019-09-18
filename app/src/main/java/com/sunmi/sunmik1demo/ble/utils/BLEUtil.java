package com.sunmi.sunmik1demo.ble.utils;

import android.bluetooth.BluetoothAdapter;

public class BLEUtil {

    /**
     * 开启蓝牙
     *
     * @return
     */
    public static boolean switchOnBLE() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            return bluetoothAdapter.enable();
        }
        return false;
    }

    /**
     * 判断蓝牙是否开启
     *
     * @return
     */
    public static boolean isBLESwitchOn() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.isEnabled();
    }
}
