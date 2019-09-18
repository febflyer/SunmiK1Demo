package com.sunmi.sunmik1demo.presenter;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.unionpay.facepay.IFacePayInterface;
import com.unionpay.facepay.IFacePayListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.content.Context.BIND_AUTO_CREATE;

public class UnionPayPreseter {
    private static final String TAG = "PrinterPresenter";
    private Context context;

    private IFacePayInterface mPayInterface;

    private CountDownLatch mLatch;
    private Executor mExecutor = Executors.newSingleThreadExecutor();

    private UnionPayCallBack callBack;

    public void setCallBack(UnionPayCallBack callBack) {
        this.callBack = callBack;
    }

    public UnionPayPreseter(Context context) {
        this.context = context;
        context.getApplicationContext().bindService(getServiceIntent(), mConn, BIND_AUTO_CREATE);
    }

    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, final IBinder service) {
            try {
                Log.d(TAG, "bind to service!");
                mPayInterface = IFacePayInterface.Stub.asInterface(service);
                service.linkToDeath(new IBinder.DeathRecipient() {
                    @Override
                    public void binderDied() {
                        service.unlinkToDeath(this, 0);
                        context.getApplicationContext().bindService(getServiceIntent(), mConn, BIND_AUTO_CREATE);
                    }
                }, 0);
                if (mLatch != null) {
                    mLatch.countDown();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPayInterface = null;
            Log.d(TAG, "unbind service");
        }
    };


    public void startThirdPay(final String amount) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mPayInterface == null) {
                        context.getApplicationContext().bindService(getServiceIntent(), mConn, BIND_AUTO_CREATE);
                        mLatch = new CountDownLatch(1);
                        mLatch.await();
                    }
                    JSONObject object = new JSONObject();
                    object.putOpt("amount", amount);
                    boolean success = mPayInterface.facePay(object.toString(), mPayListener);
                    Log.d(TAG, "get start facePay result:" + success);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private IFacePayListener mPayListener = new IFacePayListener.Stub() {
        @Override
        public void onDetectResult(boolean result, String msg) throws RemoteException {
            if(callBack!=null){
                callBack.onDetectResult(result,msg);
            }
            Log.w(TAG, "detect result:" + result + " msg:" + msg);
        }

        @Override
        public void onPayResult(boolean result, String msg) throws RemoteException {
            if(callBack!=null){
                callBack.onPayResult(result,msg);
            }
            Log.w(TAG, "pay result:" + result + " msg:" + msg);
        }
    };

    private Intent getServiceIntent() {
        Intent intent = new Intent();
        intent.setAction("com.unionpay.facepay.action_third_pay");
        intent.setPackage("com.unionpay.facepay.canteen");
        return intent;
    }

    public boolean isHave() {
        return mPayInterface != null;
    }

    public void unBindService() {
        if (mPayInterface != null) {
            context.getApplicationContext().unbindService(mConn);
            context.getApplicationContext().stopService(getServiceIntent());
        }
    }

    public interface UnionPayCallBack {
        void onDetectResult(boolean result, String msg);

        void onPayResult(boolean result, String msg);
    }

}
