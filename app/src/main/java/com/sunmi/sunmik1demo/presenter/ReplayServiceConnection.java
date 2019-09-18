package com.sunmi.sunmik1demo.presenter;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.concurrent.CountDownLatch;

/**
 * Copyright (C), 2018-2019, 商米科技有限公司
 * FileName: ReplayServiceConnection
 * Author: liuzhicheng
 * Date: 19-1-28 下午7:46
 * Description: aidl重启连接封装类
 * History:
 */

public abstract class ReplayServiceConnection implements ServiceConnection {

    private IBinder iBinder;
    private CountDownLatch mLatch;
    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            ReplayServiceConnection.this.binderDied();
            if (iBinder == null)
                return;
            iBinder.unlinkToDeath(this, 0);
            iBinder = null;
            // 这里重新绑定远程Service
            try {
                mLatch = new CountDownLatch(1);
                mLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    public void onServiceConnected(ComponentName name, final IBinder service) {
        try {

            iBinder = service;
            iBinder.linkToDeath(mDeathRecipient, 0);
            if (mLatch != null) {
                mLatch.countDown();
                mLatch = null;
                replayServiceConnected(name, service);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }

    /**
     * 服务是否存活
     * @return
     */
    public boolean isBinderAlive() {
        return iBinder != null && iBinder.isBinderAlive();
    }

    /**
     * 服务重启后调用
     * @param name
     * @param service
     */
    protected abstract void replayServiceConnected(ComponentName name, final IBinder service);

    /**
     * 服务死亡
     */

    protected abstract void binderDied();


}
