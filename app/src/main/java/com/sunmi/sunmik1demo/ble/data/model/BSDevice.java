package com.sunmi.sunmik1demo.ble.data.model;

import android.support.annotation.NonNull;

/**
 * Created by yaoh on 2018/11/16.
 */

public class BSDevice implements Comparable<BSDevice>{

    private String macAddress;
    private int deviceType;
    private String deviceName;

    private String networkID;
    private int groupID;
    private int rssi;
    private float distance; // 到设备的距离

    private long time;//最后的时间

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public BSDevice setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        return  this;
    }

    public String getNetworkID() {
        return networkID;
    }

    public void setNetworkID(String networkID) {
        this.networkID = networkID;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    @Override
    public int compareTo(@NonNull BSDevice o) {
        return o.rssi;
    }
}
