package com.sunmi.sunmik1demo.presenter;

import android.content.Context;
import android.os.Environment;
import android.os.RemoteException;
import android.util.Log;

import com.example.mtreader.AnysizeIDCMsg;
import com.example.mtreader.NativeFunc;
import com.example.mtreader.RootCmd;
import com.example.mtreader.ToolFun;
import com.example.mtreader.UsbDevPermission;
import com.ivsign.android.IDCReader.IDCReaderSDK;
import com.sunmi.idcardservice.IDCardInfo;

import com.sunmi.sunmik1demo.utils.ByteUtils;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;


/**
 * Created by mayflower on 2019/6/20
 * address :jiangli@sunmi.com
 * description :非接和身份证阅读器
 */
public class CardReaderPresenter {
    private static final String TAG = "CardReaderPresenter";

    private static Context context;
    private boolean isThreadRunning = false;       //线程状态，为了安全终止线程
    private boolean isConnected = false;        //设备连接标志

    //取身份证信息
    HashMap<String, byte[]> IDCMsg,FRIDCMsg;


    //USB口，带身份证的都是U口通讯
    private static final int VID = 0x23A4;
    private static final int PID = 0x0219;
    private int	m_device_fd	= 0;    //USB设备描述符
    private static UsbDevPermission usbPermission;

    //串口
    private static final String SERIAL_PATH = "/dev/ttyS3";
    private static final int BAUDRATE = 115200;

    private CardReaderPresenter(){
        int ret = 0;
//        if(context == null)
//            return;
        usbPermission = new UsbDevPermission(context);
        m_device_fd = usbPermission.getUsbFileDescriptor(VID,PID);

        if (m_device_fd < 0){   //已经有权限的话，就直接>0了；申请权限的话，因为来不及点，第一遍必然是<0，所以下面监听授权情况
            if (usbPermission.bRequestPer){
                usbPermission.setOnRequestPermissionListener(new UsbDevPermission.OnRequestPermissionListener() {
                    @Override
                    public void onRequestPermission(boolean request) {
                        if (request){   //同意了授权；拒绝了的话，就没办法了（测试环境下才会需要授权）
                            int et = 0;
                            m_device_fd = usbPermission.getUsbFileDescriptor(VID,PID);  //再尝试连
                            if (m_device_fd < 0){   //连不上U口，尝试连串口
                                Log.d(TAG,"获取USB设备描述符失败，开始尝试串口连接");
                                et = NativeFunc.mt8serialopen(SERIAL_PATH,BAUDRATE);
                                if (et > 0) {
                                    Log.d(TAG, "serial open suc.");
                                    isConnected = true;
                                } else {
                                    Log.e(TAG,"serial open failed.");
                                }
                                return;
                            }
                            else {  //第二次连接U口成功了
                                Log.d(TAG,"USB设备描述符fd=" + m_device_fd);
                                String retStr = RootCmd.getSELinuxMode();
                                if (retStr.trim().equals("Enforcing")){
                                    et = NativeFunc.mt8deviceopenusb(m_device_fd,usbPermission.getUsbDevPath());
                                }
                                else
                                    et = NativeFunc.mt8deviceopen(m_device_fd);

                                if (et > 0) {
                                    Log.d(TAG, "usb open suc.");
                                    isConnected = true;
                                }
                                else
                                    Log.d(TAG,"usb open failed.");
                            }
                        }
                    }
                });
            }
        }
        else {      //已授权
            Log.d(TAG, "USB设备描述符fd=" + m_device_fd);
            String retStr = RootCmd.getSELinuxMode();
            if (retStr.trim().equals("Enforcing")) {
                ret = NativeFunc.mt8deviceopenusb(m_device_fd, usbPermission.getUsbDevPath());
            } else
                ret = NativeFunc.mt8deviceopen(m_device_fd);

            if (ret > 0) {
                Log.d(TAG, "usb open suc.");
                isConnected = true;
            } else
                Log.d(TAG, "usb open failed.");
        }
    }
    private static class CardReaderInstance{
        private static final CardReaderPresenter INSTANCE = new CardReaderPresenter();
    }
    public static CardReaderPresenter getInstance(Context con){
        context = con;
        return CardReaderInstance.INSTANCE;
    }

    //读身份证
    public IDCardInfo readCard()throws RemoteException{
        if (isConnected == false)
            return null;
        IDCardInfo idCardInfo = null;

        int[] idcMsgLen = new int[2];
        byte[] idcMsg = new byte[4096];
        int  isReadFinger = 1; //读指纹
        String StrBmpFilePath = "";
        int ret = NativeFunc.mt8IDCardReadAll(idcMsgLen, idcMsg, isReadFinger);
        if (ret == 0){
            //取证件照
            byte[] wltData = null;
            try{
                wltData = AnysizeIDCMsg.CombinationWltData(idcMsgLen, idcMsg, isReadFinger);
            }catch(Exception ex){
                return null;
            }

            if (IDCReaderSDK.GetLoadSoState()){
                ToolFun.initLicData(context, Environment.getExternalStorageDirectory() + "/wltlib");
                int et = IDCReaderSDK.Init();
                if (et == 0){
                    int t = IDCReaderSDK.unpack(wltData,AnysizeIDCMsg.byLicData);
                    if (t == 1){
                        StrBmpFilePath = Environment.getExternalStorageDirectory() + "/wltlib/zp.bmp";
                        FileInputStream fis;
                        try {
                            fis = new FileInputStream(StrBmpFilePath);
                            fis.close();
                        }catch (Exception e){
                            Log.i(TAG,"");
                        }
                    }
                    else
                        Log.i(TAG,"身份证照片解码库解码失败,t=" + t);
                }
                else
                    Log.i(TAG,"身份证照片解码库初始化失败,请检查< \"+ Environment.getExternalStorageDirectory() + \"/wltlib/ > 目录下是否有照片解码库授权文件!\"");
            }
            else
                Log.i(TAG,"未找到身份证照片解码库libwltdecode.so!");

            //取身份信息
            FRIDCMsg = AnysizeIDCMsg.anysizeIDCMsg(isReadFinger,idcMsgLen[0],idcMsg);
            String idType = "";
            try {
                idType = new String(FRIDCMsg.get("TypeID"),"UTF-16LE");
                //哎，借用这个现成的IDCardInfo吧
                idCardInfo = new IDCardInfo(
                        new String(FRIDCMsg.get("ChinaName"),"UTF-16LE"),
                        new String(FRIDCMsg.get("Sex")),
                        new String(FRIDCMsg.get("Nation")),
                        new String(FRIDCMsg.get("Birth"),"UTF-16LE"),
                        new String(FRIDCMsg.get("Address"),"UTF-16LE"),
                        new String(FRIDCMsg.get("IDNum"),"UTF-16LE"),
                        new String(FRIDCMsg.get("Issued"),"UTF-16LE"),
                        new String(FRIDCMsg.get("DateStart"),"UTF-16LE"),
                        new String(FRIDCMsg.get("DateEnd"),"UTF-16LE"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //证件照
            if(idCardInfo != null)
                idCardInfo.setImageAddress(StrBmpFilePath);
            //指纹
            String strFingerData = "";
            for (int i = 0; i < FRIDCMsg.get("FingerData").length; i++) {
                strFingerData = strFingerData + String.format("%02x ", FRIDCMsg.get("FingerData")[i] & 0xFF);
            }
            //显然IDCardInfo不方便这么用，指纹暂时就不存了吧
        }
//        else
//            Log.i(TAG,"read idcard info failed.");

        return idCardInfo;
    }

    //非接寻卡
    public int rfCard(byte[] cardtype, byte[] cardID){
        int ret = 0;
        int delayTime = 0;

        ret = NativeFunc.mt8rfcard(delayTime,cardtype,cardID);
        if (ret == 0){
            return 0;
        }
        return -99;
    }
    //认证
    public int rfAuthEntication(int mode, int nsecno, byte[] key){
        int ret = 0;
        ret = NativeFunc.mt8rfauthentication(mode,nsecno,key);
        if (ret == 0) {
            return 0;
        }
        return -99;
    }
    //读卡
    public int rfRead(int nblock, byte[] readdata){
        int ret = 0;
        ret = NativeFunc.mt8rfread(nblock,readdata);
        if (ret == 0) {
            return 0;
        }
        return -99;
    }

    private class ReadThread extends Thread{

        @Override
        public void run(){
            super.run();
            //身份证
            IDCardInfo info = null;
            //非接
            int ret = 0;
            byte[] snr = new byte[10];      //卡片UID
            byte[] cardtype = new byte[8];  //卡片类型，0AH  TypeA卡;  0BH  TypeB卡
            int mode = 0;       //认证模式 0 KEYA 模式 1 KEYB 模式
            int addr = 0;       //绝对地址 = 扇区号 * 4 + 块地址，地址0是厂家信息，已固化只读
            byte[] key = new byte[10];      //默认密码：FFFFFFFFFFFF，这几个如果想要设定，可以放到成员属性
            for(int i=0;i<6;i++){ key[i] = -1;}
            key[6] = 0;
            byte[] rdata = new byte[32];    //读卡数据

            while (isThreadRunning){
                //身份证
                try {
                    info = null;
                    info = readCard();
                    if(isThreadRunning) {      //防止读卡中途退出了
                        if(info != null)
                            CardCallback.onGetIDCardData(info, 10);
                        else
                            CardCallback.onGetIDCardData(info, -10);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                //非接
                try {
                    //寻卡，支持的卡片都能成功
                    ret = rfCard(cardtype, snr);
                    if(ret != 0){ continue;}
                    //卡片UID
                    if(isThreadRunning)
                        CardCallback.onGetMiCardData(ByteUtils.bytes2HexStr(snr,0,snr.length), 10);

                    //认证，密码正确才能成功认证
                    ret = rfAuthEntication(mode,addr,key);
                    if(ret != 0){ continue;}

                    //读卡，认证成功后才能成功读卡
                    ret = rfRead(addr,rdata);
                    if(ret != 0){ continue;}
                    //绝对地址读到的数据
                    if(isThreadRunning)
                        CardCallback.onGetMiCardData(ByteUtils.bytes2HexStr(rdata,0,rdata.length), 10);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static interface CardCallback{
        public void onGetMiCardData(String data, int code);
        public void onGetIDCardData(IDCardInfo data, int code);
    }

    private CardCallback CardCallback = null;

    public void readCardAuto(CardCallback cardDataListener){
        if(isThreadRunning)
            return;
        isThreadRunning = true;   //表示线程起来了
        new ReadThread().start();

        CardCallback = cardDataListener;
    }

    public void cancelAutoReading(){
        isThreadRunning = false;
    }
}
