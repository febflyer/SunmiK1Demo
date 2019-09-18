package com.sunmi.sunmik1demo.presenter;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.sunmi.electronicscaleservice.ScaleCallback;
import com.sunmi.scalelibrary.ScaleManager;
import com.sunmi.sunmik1demo.R;
import com.sunmi.sunmik1demo.bean.GvBeans;
import com.sunmi.sunmik1demo.utils.SharePreferenceUtil;

import java.text.DecimalFormat;


/**
 * Copyright (C), 2018-2019,商米科技有限公司
 * FileName: ScalePresenter
 * Author: liuzhicheng
 * Date: 19-1-9 上午9:54
 * Description:
 * History:
 */

public class ScalePresenter {
    private Context context;
    private static final String TAG = "ScalePresenter";
    private ScaleManager mScaleManager;
    private boolean isScaleSuccess;

    public ScalePresenterCallback callback;

    private DecimalFormat decimalFormat = new DecimalFormat("0.000");
    private DecimalFormat meonyFormat = new DecimalFormat("0.00");
    private int status = -1;

    private float price = 0;

    private GvBeans gvBeans;
    public static int net;
    public int pnet;//皮重


    public interface ScalePresenterCallback {
        void getData(int net, int pnet, int statu);

        void isScaleCanUse(boolean isCan);

    }

    public ScalePresenter(Context context, ScalePresenterCallback callback) {
        this.context = context;
        this.callback = callback;
        connectScaleService();
    }

    //连接电子秤服务
    private void connectScaleService() {
        mScaleManager = ScaleManager.getInstance(context);
        mScaleManager.connectService(new ScaleManager.ScaleServiceConnection() {
            @Override
            public void onServiceConnected() {
                getScaleData();
            }

            @Override
            public void onServiceDisconnect() {
                isScaleSuccess = false;
                callback.isScaleCanUse(false);
            }
        });
    }

    private void getScaleData() {
        try {
            mScaleManager.getData(new ScaleCallback.Stub() {
                @Override
                public void getData(final int i, int i1, final int i2) throws RemoteException {
                    // i = 净重量 单位 克 ，i1 = 皮重量 单位 克 ，i2 = 稳定状态  1 为稳定。具体其他状态请参考商米开发者文档
                    Log.d("SUNMI", "update: ----------------->" + decimalFormat.format(i * 1.0f / 1000));
                    net = i;
                    pnet = i1;
                    status = i2;
                    callback.getData(i, pnet, i2);
                    if (isScaleSuccess) {
                        return;
                    }
                    isScaleSuccess = true;
                    callback.isScaleCanUse(true);

                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 清皮
     */
    public void clearTare() {
        if (pnet + net == 0 && isScaleSuccess) {
            tare();
        } else {
            Toast.makeText(context, R.string.scale_tips_clearfail, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 数字皮重
     *
     * @param numPnet
     */
    public void setNumPnet(int numPnet) {
        if (numPnet > 0 && numPnet <= 5998) {
            if (isScaleSuccess) {
                try {
                    mScaleManager.digitalTare(numPnet);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 去皮
     */
    public void tare() {
        try {
            if (isScaleSuccess) {
                mScaleManager.tare();
                Toast.makeText(context, getPnet() == 0 ? R.string.more_peele : R.string.more_clear_peele, Toast.LENGTH_LONG).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 清零
     */
    public void zero() {
        try {
            if (isScaleSuccess) {
                mScaleManager.zero();
                Toast.makeText(context, R.string.more_clear, Toast.LENGTH_LONG).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public String formatTotalMoney() {
        return meonyFormat.format(net * price / 1000);
    }

    public String formatTotalMoney(int net) {
        return meonyFormat.format(net * price / 1000);
    }

    public String formatQuality() {
        return decimalFormat.format(net * 1.0f / 1000);
    }

    public String formatQuality(int net) {
        return decimalFormat.format(net * 1.0f / 1000);
    }

    public boolean isScaleSuccess() {
        return isScaleSuccess;
    }

    public void setGvBeans(GvBeans gvBeans) {
        this.gvBeans = gvBeans;
        price = Float.parseFloat(gvBeans.getPrice().substring(1));
    }

    public float getPrice() {
        return price;
    }

    public boolean isStable() {
        return (status & 1) == 1;
    }

    public GvBeans getGvBeans() {
        return gvBeans;
    }


    public void onDestroy() {
        try {
            mScaleManager.cancelGetData();
            mScaleManager.onDestroy();
            mScaleManager = null;
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public int getPnet() {
        return pnet;
    }

    public void setPnet(int pnet) {
        this.pnet = pnet;
    }

}
