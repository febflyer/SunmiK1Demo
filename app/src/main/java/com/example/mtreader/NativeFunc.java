package com.example.mtreader;

import android.util.Log;

public class NativeFunc {
    private static String	TAG	= "NativeFunc";

    static {
        try {
            System.loadLibrary("modcomm");
            System.loadLibrary("stdcomm");
            System.loadLibrary("mt3x32");
            Log.d(TAG, "loadLibrary libmt3x32 succeed!");
        } catch (Throwable ex) {
            String mLoadErrMsg = ex.toString();
            Log.e(TAG, mLoadErrMsg);
        }
    }

    // 设备操作函数
    public static native int mt8serialopen(String port, int baud);

    public static native int mt8deviceopen(int fd);

    public static native int mt8deviceopenusb(int fd, String path);

    public static native int mt8deviceclose();

    public static native int mt8deviceversion(int module, byte verlen[], byte verdata[]);

    public static native int mt8devicereadsnr(int nSnrLen, byte sSnrData[]);

    public static native int mt8devicebeep(int delaytime, int times);

    public static native int mt8devicesetbaud(int module, int baud);



    // 按模块可选查询（查询卡）
    //public static native int mt8GetCardTypeByModule(byte ContactType[], byte noContactType[], byte IDCardType[]);

    // CPU 卡操作函数
    public static native int mt8samsltresetbaud(int cardno, int resetBaud, byte natrlen[], byte atr[]);

    public static native int mt8samsltreset(int delaytime, int cardno, byte natrlen[], byte atr[]);

    public static native int mt8samsltpowerdown(int cardno);

    public static native int mt8cardAPDU(int cardno, int sendApduLen, byte sendApdu[], int nrecvLen[], byte recvApdu[]);

    // 非接CPU卡操作函数
    public static native int mt8opencard(int delaytime, byte cardtype[], byte snrlen[], byte snr[], byte atrlen[],byte atr[]);

    public static native int mt8rfhalt(int delaytime);

    // PBOC 金融IC卡
    // 获取金融IC卡卡号、姓名
    public static native int ReadNAN(int nCardType, byte Cardno[], byte CardName[], byte lpErrMsg[]);

    // 接触式CPU社保卡
    public static native int ReadSBInfo(byte lpSocialCardBasicinfo[], byte lpErrMsg[]);

    // M1卡操作函数
    public static native int mt8rfcard(int delaytime, byte cardtype[], byte cardID[]);

    public static native int mt8rfauthentication(int mode, int nsecno, byte key[]);

    public static native int mt8rfread(int nblock, byte readdata[]);

    public static native int mt8rfwrite(int nblock, byte writedata[]);

    public static native int mt8rfincrement(int nblock, int incvalue);

    public static native int mt8rfdecrement(int nblock, int decvalue);

    public static native int mt8rfinitval(int nblock, int writevalue);

    public static native int mt8rfreadval(int nblock, int readvalue[]);

    // 磁条卡操作
    public static native int mt8magneticread(int jtimeout, int jtrack1_len[], int jtrack2_len[], int jtrack3_len[],byte jtrack1_data[], byte jtrack2_data[], byte jtrack3_data[]);

    // 设置磁条卡模式
    public static native int mt8SetMagneticMode(byte mode);

    // 接触式存储函数
    public static native int mt8contactsettype(int cardno, int cardtype);

    public static native int mt8contactidentifytype(int cardno, byte cardtype[]);

    public static native int mt8contactpasswordinit(int cardno, int pinlen, byte pinstr[]);

    public static native int mt8contactpasswordcheck(int cardno, int pinlen, byte pinstr[]);

    public static native int mt8contactread(int cardno, int address, int rlen, byte readdata[]);

    public static native int mt8contactwrite(int jcardno, int address, int wlen, byte writedata[]);



    // 二代证
    public static native int mt8CLCardOpen(int delaytime, byte cardtype[], byte snrlen[], byte snr[], byte rlen[],
                                           byte recdata[]);

    // 读二代证原始数据
    public static native int mt8IDCardReadAll(int[] idcMsgLen, byte[] idcMsg, int isReadFinger);

    // public static native int mt8IDCardGetCardInfo(int index, byte infodata[],
    // int infodatalen[]);
    public static native int mt8IDCardGetModeID(byte IDLen[], byte sIDData[]);

    public static native int mt8IDCardReadIDNUM(byte rlen[], byte receivedata[]);

    // 密码键盘1
    public static native int mt8desencrypt(byte key[], byte ptrSource[], int msgLen, byte ptrDest[]);

    public static native int mt8desdecrypt(byte key[], byte ptrSource[], int msgLen, byte ptrDest[]);

    public static native int mt8des3encrypt(byte key[], byte ptrSource[], int msgLen, byte ptrDest[]);

    public static native int mt8des3decrypt(byte key[], byte ptrSource[], int msgLen, byte ptrDest[]);

    public static native int mt8pwddecrypt(byte ptrSource[], int nDataLen, byte ptrDest[]);

    public static native int mwkbdownloadmainkey(int destype, int mainkeyno, byte mainkey[]);

    public static native int mwkbdownloaduserkey(int destype, int mainkeyno, int userkeyno, byte userkey[]);

    public static native int mt8mwkbactivekey(int mainkeyno, int userkeyno);

    public static native int mt8mwkbsetpasslen(int passlen);

    public static native int mt8mwkbsettimeout(int timeout);

    public static native int mt8mwkbgetpin(int type, byte planpin[]);

    public static native int mt8mwkbgetenpin(int type, byte cardno[], byte planpin[]);

    /* 新增加428终端上使用的接口 */

    /* 428 RS232口和RJ11上使用的串口透传测试通道 */
    //public static native int mt8serialtranschal(byte serialPath[], byte senddata, int sendlen, byte recvdata[],int recvlen[]);

    /* 428密码键盘 */
	/*public static native int mt8keyopen();

	public static native int mt8keyclose();

	public static native int mt8getkeynum(byte status[], byte keynum[]);

	public static native int mt8getkeyplainpin(byte status[], byte keynum[], byte pin[]);

	public static native int mt8downmainkey(byte index, byte enMode, byte keyLen, byte key[]);

	public static native int mt8downpinkey(byte index, byte keyLen, byte key[]);

	public static native int mt8getkeyenpin(byte index, byte cardNo[], int pinLen[], byte pin[]);

	public static native int mt8getkeyversion(int verlen[], byte verdata[]);

	/* 428指纹仪通道 */
    //public static native int mt8fingersetbaudrate(byte baudrate);

    /*
     * 0:(9600); 1: (19200); 2:(38400); 3: (57600);4:(115200); 5:(128000);
     * 6:(256000);default: (115200);
     */
    public static native int mt8fingerchannel(byte mode, int sendLen, byte sendData[], int recvLen[], byte recvData[]);

    // 工具函数
    public static native int mt8hexasc(byte hex[], byte asc[], int len);

    public static native int mt8aschex(byte asc[], byte hex[], int len);

    public static native int mt8hexbase64(byte hex[], byte base64[], int hexlen);

    public static native int mt8base64hex(byte base64[], byte hex[], int base64len);

    public static native int mt8rfencrypt(byte key[], byte ptrSource[], int msgLen, byte ptrDest[]);

    public static native int mt8rfdecrypt(byte key[], byte ptrSource[], int msgLen, byte ptrDest[]);

    public static native int WriteBMP(byte pImage[], byte pBmp[], int iWidth, int iHeight);

    // 获取非接卡状态
    public static native int mt8GetContactlessCardState(byte cardState[]);

    // 设备状态查询函数,全部查询（查询读卡模块）
    public static native int mt8GetDeviceStatus(byte ndev_status[]);

    //public static native int ReadHealthDDF1EF05(int slotNo, byte KLB[], byte GFBB[], byte FKJGMC[], byte FKJGDM[],byte FKJGZS[], byte FKSJ[], byte KH[], byte AQM[], byte XPXLH[], byte YYCSDM[]);

    //public static native int ReadHealthDDF1EF06(int slotNo, byte XM[], byte XB[], byte MZ[], byte CSRQ[], byte SFZH[]);

    /**
     * 二维码接口
     */
    //public static native int GetScanerVer(byte scanerVer[]);
    //public static native int GetTwoDimensionCode(int timeOuts,byte codeData[]);

    public static native int  mt8DevGetEMID(byte datalen[], byte data[]);
}
