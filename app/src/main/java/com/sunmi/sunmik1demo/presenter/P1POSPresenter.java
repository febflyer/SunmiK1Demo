package com.sunmi.sunmik1demo.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * Created by mayflower on 2020/01/14
 * address :jiangli@sunmi.com
 * description :P1Pos临时演示（广播收发数据）
 * 这是临时方案（先连接设备，安装1和2后分别点击“开启通信”）：
 * 1.K1USBCommHost.apk安装在K设备上
 * 2.P1USBCommAccessory.apk安装在P设备上
 * 3.SunmiPay.apk安装在P设备上
 * 4.打开K1USBCommHost,点击连接即可建立连接
 */

public class P1POSPresenter {
    private static final String TAG = P1POSPresenter.class.getSimpleName();

    private Context context;
    private POSReceiver posReceiver = new POSReceiver();
    //检查POS超时
    private boolean bPayOvertime = false;
    private Handler myHandler = new Handler(Looper.getMainLooper());

    private P1POSPresenter(Context context){
        this.context = context;
    }

    private static P1POSPresenter instance = null;
    public static P1POSPresenter getInstance(Context context){
        if (instance == null)
            instance = new P1POSPresenter(context);
        return instance;
    }

    public void startPayByPOS(String money){
        Log.i(TAG, "startPayByPOS:" + "context:" + context + ",money:" + money);
        Intent it = new Intent();
        it.setAction("host.send");
        it.putExtra("message", money);  //金额10元
        context.sendBroadcast(it);

        bPayOvertime = true;

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("host.demo");
        context.registerReceiver(posReceiver, intentFilter);

        //加一个超时退出
//        myHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (bPayOvertime){
//                    posPayCallback.onFail();
//                }
//            }
//        }, 20000);
    }

    public void closePOSReceiver(){
        if (context == null)
            return;
        context.unregisterReceiver(posReceiver);
        context = null;
    }

    private class POSReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            String ret = intent.getStringExtra("result");
            Log.i(TAG, "startPayByPOS->onReceive:" + ret);

            bPayOvertime = false;
            if(ret.contains("00")){
                //支付成功
                posPayCallback.onSuc();
            }
            else {
                posPayCallback.onFail();
            }
        }
    }
    public void setPOSPayCallback(POSPayCallback callback){
        posPayCallback = callback;
    }

    POSPayCallback posPayCallback = null;

    public interface POSPayCallback{
        void onSuc();
        void onFail();
    }
}
