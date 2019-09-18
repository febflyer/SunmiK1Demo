package com.sunmi.sunmik1demo.ble.core;

import android.bluetooth.le.AdvertiseData;

import com.sunmi.sunmik1demo.ble.data.model.MSAdvertiseMeshPackage;
import com.sunmi.sunmik1demo.ble.utils.ConvertUtils;


/**
 * Created by yaoh on 2018/3/16.
 */

public class BSAdvertiserMeshCore {

    private static final String TAG = "AdvertiserMeshCore";

    private MSAdvertiseMeshPackage.MeshData mMeshData;

    public MSAdvertiseMeshPackage.MeshData getMeshData() {
        return mMeshData;
    }

    private byte[] buildMeshDataWithMac(byte[] _data, String macAddress) {
        byte[] macAddressData = ConvertUtils.hexString2Bytes(macAddress);
        int len = macAddressData.length + _data.length;

        byte[] data = new byte[len];
        System.arraycopy(macAddressData, 0, data, 0, macAddressData.length);

        int index = macAddressData.length;
        System.arraycopy(_data, 0, data, index, _data.length);

        return data;
    }


    /**
     * 针对设备
     *
     * @param networkID
     * @param data
     * @param macAddress
     * @return
     */
    public AdvertiseData buildAdvertiserDataWithDevice(String networkID, byte[] data, String macAddress) {
        MSAdvertiseMeshPackage meshPackage = new MSAdvertiseMeshPackage();
        meshPackage.setGroupId(0x00);
        meshPackage.setNetworkId(ConvertUtils.hexString2Bytes(networkID));
        meshPackage.setData(buildMeshDataWithMac(data, macAddress));

        return createAdvertiserData(meshPackage);
    }

    /**
     * 针对分组
     *
     * @param networkID
     * @param groupID
     * @param data
     * @return
     */
    public AdvertiseData buildAdvertiserDataWithGroup(String networkID, int groupID, byte[] data) {
        MSAdvertiseMeshPackage meshPackage = new MSAdvertiseMeshPackage();
        meshPackage.setGroupId(groupID);
        meshPackage.setNetworkId(ConvertUtils.hexString2Bytes(networkID));
        meshPackage.setData(data);

        return createAdvertiserData(meshPackage);
    }

    /**
     * 针对网络
     *
     * @param networkID
     * @param data
     * @return
     */
    public AdvertiseData buildAdvertiserDataWithNetwork(String networkID, byte[] data) {
        MSAdvertiseMeshPackage meshPackage = new MSAdvertiseMeshPackage();
        meshPackage.setGroupId(0xff);
        meshPackage.setNetworkId(ConvertUtils.hexString2Bytes(networkID));
        meshPackage.setData(data);

        return createAdvertiserData(meshPackage);
    }

    private AdvertiseData createAdvertiserData(MSAdvertiseMeshPackage meshPackage) {
        AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder();
        MSAdvertiseMeshPackage.MeshData meshData = meshPackage.buildMeshPackageData();
        dataBuilder.addManufacturerData(meshData.getManufacturerID(), meshData.getManufacturerData());
        mMeshData = meshData;

        return dataBuilder.build();
    }

}
