package com.sunmi.sunmik1demo.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.sunmi.payment.PaymentService;
import com.sunmi.sunmik1demo.bean.Config;
import com.sunmi.sunmik1demo.bean.PaymentRequest;
import com.sunmi.sunmik1demo.bean.PaymentResponse;
import com.sunmi.sunmik1demo.bean.TransType;
import com.sunmi.sunmik1demo.dialog.PayDialog;
import com.sunmi.sunmik1demo.receiver.ResultReceiver;
import com.sunmi.sunmik1demo.utils.InstallApkUtils;


/**
 * Created by zhicheng.liu on 2018/3/30
 * address :liuzhicheng@sunmi.com
 * description :
 */

public class PayMentPayPresenter {

    private static final String TAG = "PayMentPayPresenter";
    Context context;
    ResultReceiver.AliResultCallback aliResultCallback;

    /**
     * 初始化刷脸服务
     *
     * @param context getApplicationContext()
     */
    public PayMentPayPresenter(Context context) {
        this.context = context;
        initReceiver();
    }


    public void init(ResultReceiver.AliResultCallback aliResultCallback) {
        this.aliResultCallback = aliResultCallback;
        if (resultReceiver != null) {
            resultReceiver.setResultCallback(this.aliResultCallback);
        } else {
            initReceiver();
        }
    }

    public boolean startFaceService(String orderId, String phoneNumber) {
        startFaceService(orderId, phoneNumber, 1);
        return true;
    }

    public boolean isHaveSunmiPay() {
        return InstallApkUtils.checkApkExist(context, InstallApkUtils.SunmiPayPkgName);

    }

    /**
     * 开始刷脸
     *
     * @return
     */
    public boolean startFaceService(String orderId, String phoneNumber, long money) {
        if (!isHaveSunmiPay()) {
            if (aliResultCallback != null) {
                aliResultCallback.onFail(null);
            }
            return false;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            phoneNumber = "";
        }
        execute("51", orderId, phoneNumber, money);
        return true;
    }

    public boolean startPayCode(String orderId, String payCode, long money) {
        execute("01", orderId, payCode, money);
        return true;
    }


    private void execute(String payMode, String orderId, String phoneNumber, long money) {
        Intent intent = new Intent();
        intent.setAction("sunmi.payment.action.entry");
        intent.setPackage("com.sunmi.payment");
        PaymentRequest request = new PaymentRequest();
        request.appType = payMode;
        request.appId = context.getPackageName();
        request.transType = TransType.CONSUME.Code();
        request.amount = formartMoney(PayDialog.PayMoney);/*money*/
        request.orderId = orderId;
        request.printTicket = "0";
        request.payCode = phoneNumber;
        Config config = new Config();
//        config.setResultDisplay(false);//默认开启收银台结果页,关闭之后将不会显示sunmi收银台的结果页
//        config.setProcessDisplay(false);//默认开启收银台进度页，关闭后将先不会跳转至sunmi收银台，直接进行交易流程
        request.config = config;
        String jsonString = jsonString(request);
        PaymentService.getInstance().callPayment(jsonString);

    }

    private int formartMoney(String money) {
        return Integer.parseInt(money.replace(".", ""));
    }

    public String jsonString(PaymentRequest request) {
        String string = JSON.toJSONString(request);
        return string;
    }

    private void initReceiver() {
        if (!isHaveSunmiPay()) {
            return;
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("sunmi.payment.action.result");
        resultReceiver = new ResultReceiver();
        context.registerReceiver(resultReceiver, intentFilter);
        resultReceiver.setResultCallback(aliResultCallback);
    }

    ResultReceiver resultReceiver;

    public void destoryReceiver() {
        if (resultReceiver != null) {
            context.unregisterReceiver(resultReceiver);
        }
    }

}
