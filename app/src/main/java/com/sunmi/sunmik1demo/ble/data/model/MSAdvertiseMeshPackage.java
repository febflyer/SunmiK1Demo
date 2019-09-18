package com.sunmi.sunmik1demo.ble.data.model;

/**
 * Created by yaoh on 2018/6/13.
 */

import android.util.Log;


import com.sunmi.sunmik1demo.ble.utils.ConvertUtils;

import java.util.Random;

/**
 * MESH 广播包
 */

public class MSAdvertiseMeshPackage {

    private static final String TAG = "MSAdvertiseMeshPackage";

    /**
     * 默认固定，后续可改 FC FB FA
     */
    private byte[] networkId;

    /**
     * 0x00 - 针对单个设备，后6个字节为设备MAC地址
     * 0xFF - 针对整个网络推送
     * 其它 - 表示分组ID，针对改分组推送
     */
    private byte groupId;


    /**
     * 如果针对单设备，前6个字节为设备MAC地址。
     * <p>
     * 推送内容编码为unicode，即2个字节表示一个字符或汉字。针对不同的推送对象，对应可推送的内容长度为：
     * 针对单设备推送，推送内容最多为16个字节，即8个字符或汉字。
     * 针对分组或全网络推送，推送内容最多为22个字节，即11个字符或汉字。
     * <p>
     * 推送内容实际长度根据manufacture data总长度推算
     */
    private byte[] data;

    public byte[] getNetworkId() {
        return networkId;
    }

    public void setGroupId(int groupId) {
        this.groupId = (byte) (groupId & 0xff);
    }

    public void setNetworkId(byte[] networkId) {
        this.networkId = networkId;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public MeshData buildMeshPackageData() {
        int len = (byte) (data.length & 0xff);
        int dataLen = 3 + 2 + 1 + len; // 3个字节networkID,1个字节 groupId, 2个字节随机数，内容长度

        int index = 0;
        byte[] advertiseData = new byte[dataLen];
        System.arraycopy(networkId, 0, advertiseData, 0, networkId.length);
        index = networkId.length;

        byte[] uniqueID = random2BitsNumber();
        System.arraycopy(uniqueID, 0, advertiseData, index, uniqueID.length);
        index += uniqueID.length;

        advertiseData[index] = groupId;
        index++;

        System.arraycopy(data, 0, advertiseData, index, len);

        Log.e(TAG, "build advertiseData -------> " + ConvertUtils.bytes2HexString(advertiseData));

        byte[] manufacturerData = new byte[advertiseData.length - 2];
        System.arraycopy(advertiseData, 2, manufacturerData, 0, manufacturerData.length);

        int manufacturerID = ((advertiseData[1] << 8) & 0xffff) | (advertiseData[0] & 0xff);
        MeshData meshData = new MeshData(manufacturerID, manufacturerData);

        Log.e(TAG, " meshData---> " + meshData.toString());
        return meshData;
    }

    public static class MeshData {

        private int manufacturerID;
        private byte[] manufacturerData;

        public MeshData(int manufacturerID, byte[] manufacturerData) {
            this.manufacturerID = manufacturerID;
            this.manufacturerData = manufacturerData;
        }

        public int getManufacturerID() {
            return manufacturerID;
        }

        public byte[] getManufacturerData() {
            return manufacturerData;
        }

        @Override
        public String toString() {
            return
//                    "{ \n" +
                    "manufacturerID : " + String.format("%04X", manufacturerID) +
                            "\n \n manufacturerData : " + ConvertUtils.bytes2HexString(manufacturerData);
//                    "\n }";
        }
    }

    /**
     * 随机数 uniqueId
     *
     * @return
     */
    public static byte[] random2BitsNumber() {
        Random random = new Random();
        byte[] randomArray = new byte[2];
        int data1 = random.nextInt(127);
        int data2 = random.nextInt(127);
        randomArray[0] = (byte) data1;
        randomArray[1] = (byte) data2;

        return randomArray;
    }


}
