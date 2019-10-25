package com.sunmi.sunmik1demo.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.sunmi.sunmik1demo.utils.ByteUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by mayflower on 2019/10/15
 * address :jiangli@sunmi.com
 * description :给串口版扫码器（广播收发数据）
 */

public class KCodeScannerPresenter2 {
    private static final String TAG = "KCodeScannerPresenter2";

    //命令格式：Prefix Storage Tag SubTag {Data} [, SubTag {Data}] [; Tag SubTag {Data}] […] ; Suffix
    private static final String PREFIX_HEX = "7E0130303030";       //~<SOH> 0000
    private static final String STORAGE_EVER_HEX = "40";    //@ 是设置永久有效
    private static final String STORAGE_TEMP_HEX = "23";    //# 则是临时设置，断电后失效
    //tag、subtag、data，这三个合起来就是具体的命令；多个命令可以合起来发，也可以挨个发（需间隔50ms或收到返回值后发）
    private static final String SUFFIX_HEX = "3B03";   //;<ETX>

    //应答数据格式：<STX><SOH>0000 Storage cmd [Data]
    private static final String RES_PREFIX_HEX = "020130303030";   //<STX><SOH>0000
    private static final String RES_ACK_HEX = "06";     //操作成功
    private static final String RES_NAK_HEX = "15";     //数据的值不在支持范围
    private static final String RES_ENQ_HEX = "05";     //设置类别或功能项不存在

    //发送和应答间隔
    private static final int MIN_SEND_TIME = 50;        //每个命令之间最小间隔：50ms
    private static final int MAX_RESPONSE_TIME = 500;   //正常应答时间为500ms
    private static final int WAIT_RESPONSE_TIME = 3000; //实际应答时间，会受环境影响，可比MAX_RESPONSE_TIME设置高一些


    //action
    private static final String ACTION_SEND_DATA = "com.sunmi.scanner.Setting_cmd";
    private static final String ACTION_RECEIVE_DATA = "com.sunmi.scanner.ACTION_DATA_CODE_RECEIVED";
    //name
    private static final String SEND_DATA = "cmd_data";
    private static final String RECEIVE_DATA = "data";

    //广播
    private Context context = null;
    private Intent intent = new Intent();
    private IntentFilter filter = new IntentFilter();
    private ScannerDataReceiver scannerDataReceiver = new ScannerDataReceiver();

    //线程池，发送命令
    private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor(); //命令的线程池，一个一个发
    private boolean isWaittingResponse = false;     //等待命令的返回中
    private boolean isNLSCommand = false;           //NLS系列命令的返回值被屏蔽，须单独做判断，等待MAX_RESPONSE_TIME

    //构造
    private KCodeScannerPresenter2(){ }

    //使用静态内部类，避免了线程不安全，延迟加载，效率高;还有另外一种双重检查也可以用
    private static class KCodeScannerInstance{
        private static final KCodeScannerPresenter2 INSTANCE = new KCodeScannerPresenter2();
    }

    public static KCodeScannerPresenter2 getInstance(){
        return KCodeScannerInstance.INSTANCE;
    }

    //注册广播，开始监听
    public void start(Context context){
        if(context == null){
            Log.e(TAG,"start：context is null");
            return;
        }
        Log.i(TAG,"start suc");
        this.context = context;

        //send
        intent.setAction(ACTION_SEND_DATA);
        //receive
        filter.addAction(ACTION_RECEIVE_DATA);
        //异步广播
        context.registerReceiver(scannerDataReceiver,filter);
    }

    //注销广播,停止监听
    public void stop(){
        if(context == null) {
            Log.e(TAG,"stop：context is null");
            return;
        }
        Log.i(TAG,"stop suc");
        context.unregisterReceiver(scannerDataReceiver);
        context = null;
    }

    //接收广播（也可以跟普通USBkeyboard扫码器一样用KeyEvent监听，即重写Activity的dispatchKeyEvent）
    private class ScannerDataReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            //这里接收广播和判断数据：应答数据与扫码数据作区分
            String value = intent.getStringExtra(RECEIVE_DATA);
            if(value.contains(ByteUtils.hexStr2Str(RES_PREFIX_HEX))){
                Log.i(TAG,"ScannerDataReceiver:response data[hex]:" + value + "[" + ByteUtils.str2HexString(value)+ "]");
                isWaittingResponse = false;
            }
            else {
                Log.i(TAG,"ScannerDataReceiver:scan data[hex]:" + value + "[" + ByteUtils.str2HexString(value)+ "]");
                onDataReceiveListener.onDataReceive(value);
            }
        }
    }

    //接收数据
    private OnDataReceiveListener onDataReceiveListener = null;

    public interface OnDataReceiveListener{
        void onDataReceive(String data);    //扫码数据
//        void onResponseReceive();     //可用于查询
        void onResponseTimeout();      //返回超时，硬件连接异常或服务异常
    }

    public void setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener){
        onDataReceiveListener = dataReceiveListener;
    }

    //发送数据（广播），发广播需要：命令+校验
    private void sendCmd(String cmd){
        Log.i(TAG,"sendCmd[hex]:" + cmd + "[" + ByteUtils.str2HexString(cmd) + "]");
        if(context == null || cmd.isEmpty())
            return;
        SendThread sendThread = new SendThread(cmd);
        singleThreadExecutor.execute(sendThread);
    }

    private class SendThread implements Runnable {
        private String strCmd;
        private SendThread(String cmd){ strCmd = cmd; }

        @Override
        public void run(){
            if (strCmd.substring(0,3).equals("NLS"))    //NLS系列指令无返回值
                isNLSCommand = true;
            isWaittingResponse = true;

            String cmdTmp = strCmd;
            if (!isNLSCommand)  //如果除了有别的系列指令，可以把这个逻辑拿出去，直接发整个儿的指令
                cmdTmp = ByteUtils.hexStr2Str(PREFIX_HEX + ByteUtils.str2HexString(strCmd) + SUFFIX_HEX);
            Log.i(TAG,"SendThread[hex]:" + cmdTmp + "[" + ByteUtils.str2HexString(cmdTmp) + "]");

            byte[] bytes = cmdTmp.getBytes();
            byte[] cmd = new byte[bytes.length + 2];
            System.arraycopy(bytes,0,cmd,0,bytes.length);
            lrcCheckSum(cmd);
            intent.putExtra(SEND_DATA, cmd);
            context.sendBroadcast(intent);

            long curTime = System.currentTimeMillis();
            while (isWaittingResponse){
                if (isNLSCommand && (System.currentTimeMillis() - curTime > MAX_RESPONSE_TIME)) {
                    Log.i(TAG,"SendThread:NLS cmd has no response");
                    isNLSCommand = false;
                    isWaittingResponse = false;
                    return;
                }
                if(System.currentTimeMillis() - curTime > WAIT_RESPONSE_TIME) {
                    Log.e(TAG,"SendThread:response timeout");
                    onDataReceiveListener.onResponseTimeout();
                    return;
                }
            }
            Log.i(TAG,"SendThread:response suc");
        }
    }

    //校验和
    private void lrcCheckSum(byte[] content) {
        int len = content.length;
        int crc = 0;
        for (int l = 0; l < len - 2; l++) {
            crc += content[l] & 0xFF;
        }
        crc = ~crc + 1;
        content[len - 2] = (byte) ((crc >> 8) & 0xFF);
        content[len - 1] = (byte) (crc & 0xFF);
    }


    //常用命令（常考常考，敲黑板啦(#^.^#)）
    /**
     * 设置扫码模式
     * @param mode 0 电平触发模式，手动读码（发1B31才读一次码）
     *             1 自动触发模式，自动读码
     * @param wait 0 亮灯后等待时间默认值，手动读码模式默认60000ms，自动读码模式默认1000ms
     *             $ 等待时间 ms
     * @param delay 0 同码间隔时间默认值(异码间隔默认200，一般不需要改)，自动读码默认1000ms，只对自动读码有效
     *              $ 扫码间隔 ms
     */
    public void setMode(int mode,int wait,int delay){
        switch (mode)
        {
            case 0:
                sendCmd("@SCNMOD0;ORTSET"+(wait>0 ? wait : 60000)+";");
                break;
            case 1:
                sendCmd("@SCNMOD2;ORTSET"+(wait>0 ? wait : 1000)+";" + "RRDDUR"+(delay>=200 ? delay : 1000));
                break;
            default:
                break;
        }
    }

    //模拟触发按键按下，用于开启支付时读一次码
    public void setKeyDown(){
        sendCmd("#SCNTRG1");
    }

    //模拟触发按键松开，一般用于退出支付时关掉扫码
    public void setKeyUp(){
        sendCmd("#SCNTRG0");
    }

    //设置后缀
    public void setSuffix(String suffix){
        //激活+设置
        sendCmd("@TSUENA1,SET" + (suffix==null ? "" : suffix));
    }

    //开关蜂鸣器,1开 0关
    public void setBuzzer(boolean on){
        sendCmd("@GRBENA" + (on ? 1 : 0));
    }
}


