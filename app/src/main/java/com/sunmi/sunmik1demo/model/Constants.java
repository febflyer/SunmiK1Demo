package com.sunmi.sunmik1demo.model;

/**
 * Created by thinkpad on 2018/1/9.
 */

public class Constants {

    public static final String SERVER_UUID = "6E400001-B5A3-F393-E0A9-E50E24DCCA1E";
    public static final String RX_UUID = "6E400002-B5A3-F393-E0A9-E50E24DCCA1E";
    public static final String TX_UUID = "6E400003-B5A3-F393-E0A9-E50E24DCCA1E";



//    public static final String SERVER_UUID = "00000001-0000-1000-8000-00805f9b34fb";
//    public static final String RX_UUID = "00000002-0000-1000-8000-00805f9b34fb";
//    public static final String TX_UUID = "00000003-0000-1000-8000-00805f9b34fb";
//    public static final String DESC_UUID = "00002902-0000-1000-8000-00805f9b34fb";

    public static final String SERVICE_SERVER_UUID = "00000001-0000-1000-8000-00805f9b34fb";
    public static final String SERVICE_RX_UUID = "00000002-0000-1000-8000-00805f9b34fb";
    public static final String SERVICE_TX_UUID = "00000003-0000-1000-8000-00805f9b34fb";
    public static final String SERVICE_DESC_UUID = "00002902-0000-1000-8000-00805f9b34fb";


    /**
     * 发送蓝牙地址
     */
    public static final short CMD_SEND_FILTER_POS_ADDRESS = 0x2E01;

    /**
     * 发送消息提醒
     */
    public static final byte[] CMD_NOTIFY_TYPE = new byte[]{0x2A,0x04};
    public static final byte CMD_NOTIFY_TITLE = 0x2B;
    public static final byte CMD_NOTIFY_CONTENT = 0x2C;
    public static final byte CMD_NOTIFY_END = 0x2D;

    /**
     * 删除蓝牙地址
     */
    public static final short CMD_REMOVE_FILTER_POS_ADDRESS = 0x2E02;

    /**
     * 设置信号强度
     */
    public static final byte[]  CMD_SET_SIGN = new byte[]{0x2E,0x03};

    /**
     * 获取信号强度
     */
    public static final byte[]  CMD_GET_SIGN = new byte[]{0x2E,0x03};

    /**
     * 设置时间
     */
    public static final byte[] CMD_SET_DATE = new byte[]{0x10,0x00,0x00};

    /**
     * 读取历史运动详情
     */
    public static final byte[] CMD_GET_TODAY_SPORT_DATA = new byte[]{0x20,0x00,0x00,0x00,0x15};


    /**
     * 读取历史运动详情
     */
    public static final byte[] CMD_GET_HISTORY_SPORT_DATA = new byte[]{0x20,0x00,0x00,0x00,0x13};


    /**
     * 设置自动心率
     */
    public static final byte[] CMD_SET_HEART_RATE = new byte[]{0x26,0x00,0x00,0x00,0x54};


    /**
     * 获取历史心率
     */
    public static final byte[] CMD_GET_HISTORY_HEAR_RATE_DATA = new byte[]{0x20,0x00,0x00,0x00,0x14};

    /**
     * 设置用户信息
     */
    public static final byte[] CMD_SET_USER_INFO = new byte[]{0x29,0x00,0x00,0x00,0x16};


    /**
     * 消息提醒开关
     */
    public static final byte[] CMD_SET_NOTIFY_ENABLE= new byte[]{0x29,0x00,0x00,0x00,0x14};

    /**
     * 进入升级模式
     */
    public static final byte[] CMD_GET_VERSION= new byte[]{0x25,0x00,0x00,0x00,0x04};

    /**
     * 进入升级模式
     */
    public static final byte[] CMD_INTO_UPDATE_MODE= new byte[]{0x27,0x00,0x00,0x00,0x55};

    /**
     * 重启
     */
    public static final byte[] CMD_REBOOT= new byte[]{0x01};

    /**
     * 震动
     */
    public static final byte[] CMD_VIBRATE=  new byte[]{0x26,0x00,0x00,0x00,0x20,0x03};

    /**
     * 恢复出厂设置
     */
    public static final byte[] CMD_FACTORY_RESET= new byte[]{0x27,0x00,0x00,0x00,0x02};

}
