package com.sunmi.sunmik1demo.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.sunmi.sunmik1demo.utils.ByteUtils;

/**
 * created by mayflower on 19/6/27
 * for codescanner EM20-80
 * 跟CodeScannerPresenter区别：
 * 1.5.2版ROM将此串口扫码器做了兼容，这里用广播监听即可；原本的CodeScannerPresenter是直接串口通讯，已经不需要了。
 **/
public class KCodeScannerPresenter {
    private static final String TAG = "KCodeScannerPresenter";

    private static final int CMD_WAIT_TIME = 500;   //EM20-80官方文档：正常应答时间为500ms，但经过广播后肯定不止吧
    private boolean bCmdWaitting = false;       //等待命令的返回中
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

    //构造
    private KCodeScannerPresenter(){ }

    //使用静态内部类，避免了线程不安全，延迟加载，效率高;还有另外一种双重检查也可以用
    private static class KCodeScannerInstance{
        private static final KCodeScannerPresenter INSTANCE = new KCodeScannerPresenter();
    }

    public static KCodeScannerPresenter getInstance(){
        return KCodeScannerInstance.INSTANCE;
    }

    //注册广播，开始监听
    public void start(Context context){
        if(context == null){
            Log.e(TAG,"start：context = null.");
            return;
        }
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
        if(context == null)
            return;
        context.unregisterReceiver(scannerDataReceiver);
        context = null;
    }

    //接收广播（也可以跟普通USBkeyboard扫码器一样用KeyEvent监听，即重写Activity的dispatchKeyEvent）
    private class ScannerDataReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            //这里接收广播和判断数据：返回值与扫码数据作区分
            //onReceive中代码的执行时间不要超过5s，否则android会弹出超时dialog
            //这里如有长时间操作，可开Service解决，不可开线程
            String value = intent.getStringExtra(RECEIVE_DATA);
            //好苦啊，这个特殊命令的返回值太长了,但0000@SENIST是没变的
            if(value.contains(ByteUtils.hexStr2Str("303030304053454E495354"))){
                Log.d(TAG,"cmd return data[hex]：" + value + "[" + ByteUtils.str2HexString(value)+ "]");
                bCmdWaitting = false;
            }
            else {
                Log.d(TAG,"scan data[hex]：" + value + "[" + ByteUtils.str2HexString(value)+ "]");
                onDataReceiveListener.onDataReceive(value);
            }
        }
    }

    //接收数据
    private OnDataReceiveListener onDataReceiveListener = null;

    public static interface OnDataReceiveListener{
        public void onDataReceive(String data);
        public void onCmdFail();
    }

    public void setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener){
        onDataReceiveListener = dataReceiveListener;
    }

    //发送数据（广播），发广播需要：命令+校验
    private void sendData(String data){
        Log.d(TAG,"---sendstr:" + data + "---sendhex:" + ByteUtils.str2HexString(data));
        if(context == null)
            return;
        byte[] bytes = data.getBytes();
        byte[] cmd = new byte[bytes.length + 2];
        System.arraycopy(bytes,0,cmd,0,bytes.length);
        lrcCheckSum(cmd);

        //Log.d(TAG,"---sendbytes+sum->hex:" + ByteUtils.bytes2HexStr(cmd,0,cmd.length));
        intent.putExtra(SEND_DATA, cmd);
        context.sendBroadcast(intent);
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

    //常用命令
    /**
     * 设置扫码模式
     * @param mode 0 模拟触发模式，手动读码（发1B31才读一次码）
     *             1 自动触发模式，自动读码
     * @param wait 0 亮灯后等待时间默认值，手动读码模式默认60000ms，自动读码模式默认1000ms
     *             * 等待时间 ms
     * @param delay 0 扫码后再次扫码间隔时间默认值，自动读码默认1000ms，只对自动读码有效
     *              * 扫码间隔 ms,取值范围200-3000
     */
    public void setMode(int mode,int wait,int delay){
        switch (mode)
        {
            //"NLS0309010;"是默认的0x0D后缀           ，默认都应该有，可放这里发
            //"NLS0310000=0x0D0A;"则是自定义0x0D0A后缀
            case 0:
                //NLS系列为常用命令，可以这样合起来发（分开发的话需要等前一条命令执行完毕才行，但是返回值又被系统屏蔽了，只能用线程+延迟）
                sendData("NLS0302000;" + "NLS0313000=" + (wait>0 ? wait : 60000) + ";" + "NLS0310000=0x0D0A;");
                break;
            case 1:
                bCmdWaitting = true;
                sendData(ByteUtils.hexStr2Str("7E01303030304053454E495354" +
                        ByteUtils.str2HexString(String.valueOf(delay>=200 && delay<=3000 ? delay : 1000)) + "3B03"));

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long curTime = System.currentTimeMillis();
                        while (bCmdWaitting){
                            //这个3000ms是示例，因为别的操作可能会影响系统广播速度；可改别的，也可做set
                            if(System.currentTimeMillis() - curTime > 3000) {
                                Log.e(TAG,"SendThread:等待返回超时");
                                onDataReceiveListener.onCmdFail();
                                return;  //也可以用return,看需要，扫码器处理指令在500ms以内，所以可以用break
                            }
                        }
                        //最后一个指令是配置了回车后缀，因为是固定要加，可以放这里
                        sendData("NLS0302010;" + "NLS0313000=" + (wait>0 ? wait : 1000) + ";" + "NLS0310000=0x0D0A;");
//                        sendData("NLS0302010;" + "NLS0313000=" + (wait>0 ? wait : 1000) + ";" + "NLS0309010;");
                    }
                });
                thread.start();
                break;
            default:
                break;
        }
    }

    //模拟触发按键按下，用于开启支付时读一次码
    public void setKeyDown(){
        sendData(ByteUtils.hexStr2Str("1B31"));
    }

    //模拟触发按键松开，一般用于退出支付时关掉扫码
    public void setKeyUp(){
        sendData(ByteUtils.hexStr2Str("1B30"));
    }

}


