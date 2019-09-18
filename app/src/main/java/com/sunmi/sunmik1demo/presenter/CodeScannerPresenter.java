package com.sunmi.sunmik1demo.presenter;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.nfc.Tag;
import android.support.annotation.MainThread;
import android.util.Log;
import android.widget.Adapter;
import android.widget.Switch;
import android.app.Activity;

import com.sunmi.sunmik1demo.R;
import com.sunmi.sunmik1demo.ui.MainActivity;
import com.sunmi.sunmik1demo.utils.ByteUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Handler;

import android_serialport_api.SerialPort;

/**
 * Created by MayFlower on 2019/4/9
 * CodeScannerPresenter for Scanner: ttyS4,9600
 */

public class CodeScannerPresenter {
    private static final String TAG = "CodeScannerPresenter";

    private Context contest;
    private String path = "dev/ttyS4";
    private int baudrate = 9600;

    private boolean serialPortStatus = false;   //串口是否打开标志
    private boolean threadStatus;               //线程状态，为了安全终止线程

    private volatile boolean cmdStatus = false; //命令状态，false表示当前没有在命令发送中，true表示发送命令后在等待返回
    private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();     //命令的线程池，一个一个发

    private SerialPort serialPort = null;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;

    SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

    public CodeScannerPresenter(Context context){       //標準用instance,可做优化
        this.contest = context;
        openSerialPort();
    }
    /**
     * 打开串口
     * @return serialPort串口对象
     */
    public SerialPort openSerialPort(){
        try{
            //串口无权限则退出，因为SerialPort中的判断有漏洞，所以在此判断
            File file = new File(path);
            if(!file.canRead() && !file.canWrite()) {
                Log.e(TAG,"openSerialPort: Open Failed!Permission denied!");
                return null;
            }

            serialPort = new SerialPort(file, baudrate,0);
            this.serialPortStatus = true;
            this.threadStatus = false;

            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();

            soundPool.load(contest, R.raw.scanner,1);//3

            new ReadThread().start();
        }catch(IOException e){
            Log.e(TAG,"openSerialPort: 打开串口异常：" + e.toString());
            return serialPort;
        }
        Log.d(TAG,"openSerialPort:打开串口成功" );
        return serialPort;
    }

    /**
     *关闭串口
     */
    public void closeSerialPort(){
        if(!this.serialPortStatus)
            return;
        try {
            inputStream.close();
            outputStream.close();
            soundPool.release();
            singleThreadExecutor.shutdown();

            this.serialPortStatus = false;
            this.threadStatus = true;
            serialPort.close();
        }catch (IOException e){
            Log.e(TAG, "closeSerialPort: 关闭串口异常："+e.toString());
            return;
        }
        Log.d(TAG, "closeSerialPort: 关闭串口成功");
    }

    /**
     * 串口打开状态
     * */
    public boolean isOpened(){
        return serialPortStatus;
    }

    /**
     *发送串口指令（字符串）
     *@param data String数据指令
     */
    public void sendSerialPort(String data){
        Log.d(TAG,"sendSerialPort:发送数据:" + data);

        //单线程化线程池，让命令排队发送
        SendThread sendThread = new SendThread(data);
        singleThreadExecutor.execute(sendThread);
    }

    private class SendThread implements Runnable{
        private String cmdData;
        private SendThread(String data){
            this.cmdData = data;
        }

        public void run(){
            try {
                byte[] sendData = cmdData.getBytes();
                if(sendData.length > 0){
                    cmdStatus = true;

                    outputStream.write(sendData);
                    outputStream.write('\n');
                    outputStream.flush();
                    Log.d(TAG, "SendThread: 串口数据发送成功:" + ByteUtils.bytes2Str(sendData,0,sendData.length));

                    long curTime = System.currentTimeMillis();
                    while (cmdStatus){
                        if(System.currentTimeMillis() - curTime > 3000) {       //这个3000ms是示例，可改别的，也可做set
                           Log.e(TAG,"SendThread:等待返回超时");
                           return;
                       }
                   }
                   Log.d(TAG,"SendThread:等待返回成功");
                }
            }catch (IOException e){
                Log.e(TAG, "SendThread: 串口数据发送失败:" + e.toString());
            }
        }
    }

    private class ReadThread extends Thread{
        private String recHexData;    //收到的数据

        @Override
        public void run(){
            super.run();
            //判断进程是否在运行，更安全的结束进程
            while (!threadStatus) {
                Log.d(TAG,"ReadThread:进入线程run");
                byte[] buffer = new byte[1024];

                int size;
                try{
                    size = inputStream.read(buffer);
                    if(size > 0){
                        Log.e(TAG, "ReadThread:接收到了数据：" + ByteUtils.bytes2Str(buffer,0,size)
                                + "[HEX:"  + ByteUtils.bytes2HexStr(buffer,0,size) + "]");
                        //各种命令的返回值暂不详细判断，这里只是演示
                        recHexData = ByteUtils.bytes2HexStr(buffer,0,size);
                        if(recHexData.equals("06") || recHexData.equals("0201303030304053454E49535431303030063B03"))
                            cmdStatus = false;  //收到数据后把发送状态置false，线程池就可以开始发下一条指令
                        else {
                            soundPool.play(1, 1, 1, 10, 0, 1);
                            onDataReceiveListener.onDataReceive(buffer, size);
                        }
                    }
                }catch (IOException e){
                    Log.e(TAG, "ReadThread:数据读取异常：" +e.toString());
                }
            }
        }
    }

    //监听器来监听接收数据
    public OnDataReceiveListener onDataReceiveListener = null;

    public static interface OnDataReceiveListener{
        public void onDataReceive(byte[] buffer, int size);
    }

    public void setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener){
        onDataReceiveListener = dataReceiveListener;
    }

    //扫码器的常用配置码---------------------------------------
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
            case 0:
                sendSerialPort("NLS0302000;");
                sendSerialPort("NLS0313000=" + (wait>0 ? wait : 60000) + ";");
                break;
            case 1:
                sendSerialPort("NLS0302010;");
                sendSerialPort("NLS0313000=" + (wait>0 ? wait : 1000) + ";");
                sendSerialPort(ByteUtils.hexStr2Str("7E01303030304053454E495354" +
                        ByteUtils.str2HexString(String.valueOf(delay>200 && delay<3000 ? delay : 1000)) + "3B03"));
                break;
            default:
                break;
        }
    }

    /**
     * 模拟触发按键按下
     */
    public void setKeyDown(){
        sendSerialPort(ByteUtils.hexStr2Str("1B31"));
    }
    /**
     * 模拟触发按键松开
     */
    public void setKeyUp(){
        sendSerialPort(ByteUtils.hexStr2Str("1B30"));
    }
}
