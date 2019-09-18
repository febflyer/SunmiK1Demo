package com.sunmi.sunmik1demo.ble;

import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.util.Log;

import com.sunmi.sunmik1demo.ble.core.BSAdvertiserCore;
import com.sunmi.sunmik1demo.ble.core.BSAdvertiserMeshCore;
import com.sunmi.sunmik1demo.ble.listener.BSAdvertiserListener;
import com.sunmi.sunmik1demo.ble.utils.ConvertUtils;


/**
 * Created by yaoh on 2018/11/15.
 */

public class BSAdvertiserManager {

    private static final String TAG = "BSAdvertiserManager";

    private BSAdvertiserCore mAdvertiserCore;
    private BSAdvertiserMeshCore mAdvertiserMeshCore;

    private BSAdvertiserListener mBSAdvertiserListener;

    public BSAdvertiserManager() {
        mAdvertiserCore = new BSAdvertiserCore(mAdvertiseCallback);
        mAdvertiserMeshCore = new BSAdvertiserMeshCore();
    }

    public void setBSAdvertiserListener(BSAdvertiserListener mBSAdvertiserListener) {
        this.mBSAdvertiserListener = mBSAdvertiserListener;
    }

    //    public static BSAdvertiserManager get() {
//        if (mInstance == null) {
//            synchronized (BSAdvertiserManager.class) {
//                if (mInstance == null) {
//                    mInstance = new BSAdvertiserManager();
//                }
//            }
//        }
//        return mInstance;
//    }

    /**
     * 针对 整个网络广播
     *
     * @param networkID
     * @param content
     */
    public void startAdvertiserDataWithNetwork(String networkID, String content) {
        byte[] data = ConvertUtils.hexString2Bytes(hexString2Unicode(content));
        AdvertiseData advertiseData = mAdvertiserMeshCore
                .buildAdvertiserDataWithNetwork(networkID, data);

        if (mBSAdvertiserListener != null) {
            mBSAdvertiserListener.onAdvertiserData(mAdvertiserMeshCore.getMeshData().toString());
        }

        mAdvertiserCore.startAdvertiseData(advertiseData);
    }

    /**
     * 针对分组广播
     *
     * @param networkID
     * @param groupID
     * @param content
     */
    public void startAdvertiserDataWithGroup(String networkID, int groupID, String content) {
        byte[] data = ConvertUtils.hexString2Bytes(hexString2Unicode(content));

        AdvertiseData advertiseData = mAdvertiserMeshCore
                .buildAdvertiserDataWithGroup(networkID, groupID, data);

        if (mBSAdvertiserListener != null) {
            mBSAdvertiserListener.onAdvertiserData(mAdvertiserMeshCore.getMeshData().toString());
        }

        mAdvertiserCore.startAdvertiseData(advertiseData);
    }

    /**
     * 针对设备广播
     *
     * @param networkID
     * @param macAddress
     * @param content
     */
    public void startAdvertiserDataWithDevice(String networkID, String macAddress, String content) {
        byte[] data = ConvertUtils.hexString2Bytes(hexString2Unicode(content));

        AdvertiseData advertiseData = mAdvertiserMeshCore
                .buildAdvertiserDataWithDevice(networkID, data, macAddress);

        if (mBSAdvertiserListener != null) {
            mBSAdvertiserListener.onAdvertiserData(mAdvertiserMeshCore.getMeshData().toString());
        }

        mAdvertiserCore.startAdvertiseData(advertiseData);
    }

    /**
     * 停止广播
     */
    public void stop() {
        mAdvertiserCore.stopAdvertiseData();
    }


    /**
     * mesh 广播的回调
     */
    AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {

        @Override
        public void onStartFailure(int errorCode) {
            Log.d(TAG, "onStartFailure --->");
            if (mBSAdvertiserListener != null) {
                mBSAdvertiserListener.onAdvertiserResult(false);
            }
        }

        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.d(TAG, " Advertising successfully --->");

            if (mBSAdvertiserListener != null) {
                mBSAdvertiserListener.onAdvertiserResult(true);
            }

        }
    };

    /**
     * 字符串转换unicode
     */
    public static String hexString2Unicode(String string) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {

            // 取出每一个字符
            int strChar = string.charAt(i) & 0xffff;

            byte byteL = (byte) (strChar >> 8 & 0xff);
            byte byteH = (byte) (strChar & 0xff);
            int newStrChar = (byteH << 8 & 0xffff) | (byteL & 0xff);

            // 转换为unicode
            unicode.append(String.format("%04X", newStrChar));
        }

        Log.d(TAG, "unicode data ---> " + unicode.toString());

        return unicode.toString();
    }


}
