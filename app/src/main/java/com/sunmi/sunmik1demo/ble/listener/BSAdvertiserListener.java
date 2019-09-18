package com.sunmi.sunmik1demo.ble.listener;

/**
 * Created by yaoh on 2018/11/16.
 */

public interface BSAdvertiserListener {

    public void onAdvertiserResult(boolean isSuccess);

    public void onAdvertiserData(String data);

}
