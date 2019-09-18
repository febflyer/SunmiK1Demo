//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunmi.sunmik1demo.unlock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.sunmi.sunmik1demo.bean.blescan.BraceletUserBean;
import com.sunmi.sunmik1demo.eventbus.UnlockSingValueCalEvent;
import com.sunmi.sunmik1demo.eventbus.UpdateUnLockUserEvent;
import com.sunmi.sunmik1demo.ui.UnlockActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.greenrobot.eventbus.EventBus;

public class BraceletPresenter {
    public static final int DEFAULT_CHECK_VALUE = 77;//选择解锁200阈值
    public static final int DEFAULT_LOCK_VALUE = 77;//锁定阈值  三米以上
    public static final int DEFAULT_SIGN_VALUE = 65;//解锁阈值  一米以下
    private int defaultCheckCount = 6;//选择采样数
    private int defaultLockCount = 6;//选择锁定数
    private int defaultUnlockCount = 2;//连续在解锁值以下解锁
    private final int count = 10;//采样总数

    public static final int DEFAULT_TIME = 200;
    private static final String TAG = BraceletPresenter.class.getSimpleName();
    public static boolean isDomainUnlock;
    public static boolean isDomainlock;
    private static final int tempSingvalue = 3;
    public static BraceletUserBean user = null;
    private Map<String, BraceletUserBean> braceletUser = new HashMap<>();
    private List<String> canUnlocklist = new CopyOnWriteArrayList();
    private List<String> checklist = new CopyOnWriteArrayList();
    private List<String> closeDoorlist = new CopyOnWriteArrayList();

    private int getCount = 0;
    private Handler handler = new Handler(Looper.myLooper());
    private Context mContext;
    private volatile int mSignValue = DEFAULT_SIGN_VALUE;
    private List<String> macAddress = new ArrayList();
    private Map<String, LinkedList<Integer>> temp = new ConcurrentHashMap();

    public BraceletPresenter(Context var1) {
        this.mContext = var1;

        this.watchDog();
    }

    public void setBraceletUser(Map<String, BraceletUserBean> braceletUser) {
        boolean isChange = false;
        for (String mac : this.braceletUser.keySet()) {
            if (!braceletUser.containsKey(mac)) {
                isChange = true;
            }
        }
        for (String mac : braceletUser.keySet()) {
            if (!this.braceletUser.containsKey(mac)) {
                isChange = true;
            }
        }
        if (isChange) {
            for (String mac : this.braceletUser.keySet()) {
                if (!braceletUser.containsKey(mac)) {
                    isChange = true;
                    temp.remove(mac);
                    macAddress.remove(mac);
                }
            }
            this.braceletUser.clear();
            this.braceletUser.putAll(braceletUser);
        }
    }

    private void deletUser() {
        this.updateUser("", false);
        user = null;
    }

    /**
     * 可以解锁的列表
     *
     * @return
     */
    private List<String> getCanOpenDoorUser() {
        ArrayList var3 = new ArrayList();

        LinkedList var6;
        for (Iterator var4 = this.macAddress.iterator(); var4.hasNext(); var6.clear()) {
            String var5 = (String) var4.next();
            int var1;
            if (this.braceletUser.get(var5) != null && ((BraceletUserBean) this.braceletUser.get(var5)).getSingValue() != 0) {
                var1 = ((BraceletUserBean) this.braceletUser.get(var5)).getSingValue();
            } else {
                var1 = this.mSignValue;
            }

            this.mSignValue = var1;
            var1 = 0;
            var6 = new LinkedList();
            var6.addAll((Collection) this.temp.get(var5));
            Iterator var7 = var6.iterator();

            while (var7.hasNext()) {
                if (Math.abs((Integer) var7.next()) < this.mSignValue) {
                    int var2 = var1 + 1;
                    var1 = var2;
                    if (var2 == this.defaultUnlockCount) {
                        var3.add(var5);
                        break;
                    }
                } else {
                    var1 = 0;
                }
            }
        }

        Log.i(TAG, "openDoor>>>>>>" + var3.size() + "  ");
        this.canUnlocklist.clear();
        this.canUnlocklist.addAll(var3);
        return this.canUnlocklist;
    }

    private List<String> getChecklist() {
        return this.checklist;
    }

    /**
     * 锁定列表
     *
     * @return
     */
    private List<String> getCloseDoorUser() {
        ArrayList var4 = new ArrayList();
        ArrayList var5 = new ArrayList();
        var4.addAll(this.macAddress);
        var5.addAll(this.macAddress);

        LinkedList var8;
        for (Iterator var6 = this.macAddress.iterator(); var6.hasNext(); var8.clear()) {
            String var7 = (String) var6.next();
            int var1;
            if (this.braceletUser.get(var7) != null && ((BraceletUserBean) this.braceletUser.get(var7)).getSingValue() != 0) {
                var1 = ((BraceletUserBean) this.braceletUser.get(var7)).getSingValue();
            } else {
                var1 = this.mSignValue;
            }

            this.mSignValue = var1;
            int var2 = 0;
            var1 = 0;
            var8 = new LinkedList();
            var8.addAll((Collection) this.temp.get(var7));
            Iterator var9 = var8.iterator();

            while (var9.hasNext()) {
                Integer var10 = (Integer) var9.next();
                int var3 = var2;
                if (Math.abs(var10) > DEFAULT_LOCK_VALUE) {
                    var3 = var2 + 1;
                }

                var2 = var3;
                if (Math.abs(var10) > DEFAULT_CHECK_VALUE) {
                    ++var1;
                    var2 = var3;
                }
            }

            if (var2 > this.defaultLockCount) {
                var4.remove(var7);
            }

            if (var1 > this.defaultCheckCount) {
                var5.remove(var7);
            }
        }

        Log.e(TAG, "closeDoor>>>>>>" + var4.size() + "  " + var5.size());
        this.closeDoorlist.clear();
        this.closeDoorlist.addAll(var4);
        this.checklist.clear();
        this.checklist.addAll(var5);
        return this.closeDoorlist;
    }

    private String getUser() {
        return this.hasUser() ? user.getMac() : "";
    }

    private boolean hasUser() {
        return user != null;
    }


    private void singleConnect(String var1) {
        Log.e(TAG, "singleLock>>>>>>>>>>>>address>>>>>" + var1 + "    " + this.mSignValue + "  ");

        if (isLock()) {
            this.unlock(var1);
        }

    }

    private void startCheckLock(List<String> var1) {
        Log.e(TAG, "startCheckLock>>>>>>" + var1.size() + ">>>>>>mSignValue>>>>>" + this.mSignValue + "  " + user);
        if (!isDomainUnlock) {
            this.deletUser();
            Bundle var2 = new Bundle();
            ArrayList var3 = new ArrayList();
            var3.addAll(var1);
            var2.putStringArrayList("addresses", var3);
            this.startActivity(var2, UnlockActivity.UNLOCK_LIST);
        }
    }

    public static void updateUser(String address, String name, int icon) {
        if (TextUtils.isEmpty(address)) {
            EventBus.getDefault().post(new UpdateUnLockUserEvent(name, UnlockActivity.SHOP_ICON, UnlockActivity.SHOPNAME, false));
            user = null;
        } else {
            BraceletUserBean var3 = new BraceletUserBean();
            var3.setMac(address);
            var3.setName(name);
            var3.setIcon(icon);
            user = var3;
            EventBus.getDefault().post(new UpdateUnLockUserEvent(address, icon, name, false));
        }

    }

    private void updateUser(String var1, boolean var2) {
        if (TextUtils.isEmpty(var1)) {
            EventBus.getDefault().post(new UpdateUnLockUserEvent(var1, UnlockActivity.SHOP_ICON, UnlockActivity.SHOPNAME, false));
            user = null;
        } else {
            user = (BraceletUserBean) this.braceletUser.get(var1);
            EventBus.getDefault().post(new UpdateUnLockUserEvent(var1, user.getIcon(), user.getName(), var2));
        }
    }

    private void watchDog() {
        this.handler.postDelayed(new Runnable() {
            public void run() {
                BraceletPresenter.this.watchDog();
                if (BraceletPresenter.this.macAddress.size() == 0) {
                    if (!isDomainUnlock) {
                        BraceletPresenter.this.lock("");
                    }
                } else if (!UnlockActivity.isUnlocking) {
                    Iterator var1 = BraceletPresenter.this.temp.keySet().iterator();
                    while (var1.hasNext()) {
                        String var2 = (String) var1.next();
                        if (temp.get(var2) == null || ((List) BraceletPresenter.this.temp.get(var2)).size() < count) {
                            return;
                        }
                    }

                    List<String> closeDoorUser = BraceletPresenter.this.getCloseDoorUser();
                    List<String> openDoorUser = BraceletPresenter.this.getCanOpenDoorUser();
                    List<String> checklist = BraceletPresenter.this.getChecklist();

                    switch (UnlockActivity.state) {
                        case UnlockActivity.LOCK:
                            if (openDoorUser.size() == 1) {
                                updateUser(openDoorUser.get(0), true);
                                singleConnect(openDoorUser.get(0));
                            } else if (checklist.size() == 1) {
                                updateUser(checklist.get(0), true);
                                singleConnect(checklist.get(0));
                            } else if (checklist.size() > 1) {
                                startCheckLock(checklist);
                            }
                            break;
                        case UnlockActivity.UNLOCK:
                            //从解锁变为锁定
                            if (openDoorUser.size() >= 1 && !openDoorUser.contains(getUser())) {
                                updateUser(openDoorUser.get(0), true);
                            } else if (checklist.size() == 1 && !checklist.contains(getUser())) {
                                updateUser(checklist.get(0), true);
                            } else if (checklist.size() > 1 && !checklist.contains(getUser())) {
                                startCheckLock(checklist);

                            } else if (checklist.contains(getUser())) {
                                isDomainUnlock = false;

                            } else if (closeDoorUser.size() == 0) {
                                BraceletPresenter.this.lock("");
                            }
                            break;
                        case UnlockActivity.UNLOCK_LIST:
                            if (!isDomainlock && openDoorUser.size() == 1 && checklist.contains(openDoorUser.get(0))) {
                                checklist.clear();
                                checklist.add(openDoorUser.get(0));
                                startCheckLock(checklist);
                            } else if (checklist.size() > 1) {
                                startCheckLock(checklist);
                            } else if (checklist.size() == 1) {
                                startCheckLock(checklist);
                            } else if (checklist.size() == 0) {
                                lock("");
                            }
                            break;
                    }
                }

            }
        }, DEFAULT_TIME);
    }

    public void addConnectAddress(String var1) {
        if (!this.macAddress.contains(var1)) {
            this.macAddress.add(var1);
            this.temp.put(var1, new LinkedList());
        }
    }


    public void disconnected(String var1) {
        if (var1.equals(this.getUser())) {
            this.lock("");
        }

        if (this.macAddress.contains(var1)) {
            this.macAddress.remove(var1);
        }

        if (this.temp.containsKey(var1)) {
            this.temp.remove(var1);
        }

    }

    public void disconnectedAll() {
        this.temp.clear();
        this.macAddress.clear();
        isDomainlock = false;
        isDomainUnlock = false;
        this.handler.removeCallbacksAndMessages((Object) null);
    }

    public int getSignValue() {
        return this.mSignValue;
    }

    public void unlock(String var1) {
        if (isDomainlock) {
            return;
        }
        if (isLock()) {
            if (this.temp.containsKey(var1)) {
                Log.i(TAG, "locklocklock===" + var1 + "===  user ====" + ((LinkedList) this.temp.get(var1)).size());
                temp.put(var1, new LinkedList<Integer>());
            }
            this.updateUser(var1, false);
            Bundle var2 = new Bundle();
            var2.putString("address", var1);
            this.startActivity(var2, UnlockActivity.UNLOCK);
        }
    }

    public void lock(String var1) {
        isDomainlock = false;
        if (isDomainUnlock) {
            return;
        }
        this.deletUser();
        if (this.temp.containsKey(var1)) {
            Log.i(TAG, "locklocklock===" + var1 + "===  user ====" + ((LinkedList) this.temp.get(var1)).size());
            temp.put(var1, new LinkedList<Integer>());
        }

        Log.e(TAG, "locklocklock===" + var1 + "===  user   ");
        Bundle var2 = new Bundle();
        var2.putString("address", var1);
        this.startActivity(var2, UnlockActivity.LOCK);
    }


    public void setAddressAndRssi(String var1, int var2) {
        if (this.macAddress.size() != 0 && this.braceletUser.containsKey(var1)) {
            if (!this.macAddress.contains(var1)) {
                this.macAddress.add(var1);
            }

            if (!this.temp.containsKey(var1)) {
                this.temp.put(var1, new LinkedList());
            }

            int var3 = Math.abs(var2);
            LinkedList var4 = (LinkedList) this.temp.get(var1);
            if (var4.size() != count) {
                var4.add(var3);
            } else {
                for (var2 = 0; var2 < var4.size() - 1; ++var2) {
                    var4.set(var2, var4.get(var2 + 1));
                }

                var4.set(var4.size() - 1, var3);
            }

            this.temp.put(var1, var4);
            ++this.getCount;
            if (this.getCount >= this.temp.size()) {
                this.getCount = 0;
                return;
            }
        }

    }


    public void setSignValue(int var1) {
        this.mSignValue = var1;
    }

    public void startActivity(final Bundle var1, final int var2) {
        if ((UnlockActivity.state == var2 && var2 == UnlockActivity.LOCK) || UnlockActivity.state == UnlockActivity.UNLOCK_PWD) {
            return;
        }
        UnlockActivity.state = var2;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Intent var3 = new Intent(mContext, UnlockActivity.class);
                var3.putExtra("params", var1);
                var3.putExtra("mode", var2);
                mContext.startActivity(var3);
            }
        });

    }

    public void startCalibration(final String var1) {
        Log.e(TAG, "开始校准>>>>>>" + var1);
        this.handler.postDelayed(new Runnable() {
            public void run() {
                if (!BraceletPresenter.this.temp.containsKey(var1)) {
                    Log.e(BraceletPresenter.TAG, "校准失败>>>>>>" + var1 + ">>>>>>mSignValue>>>>>" + BraceletPresenter.this.mSignValue);
                    EventBus.getDefault().post(new UnlockSingValueCalEvent(0, var1));
                } else {
                    LinkedList var4 = (LinkedList) BraceletPresenter.this.temp.get(var1);
                    int var3 = 0;
                    int var1x = 0;

                    for (int var2 = 0; var2 < var4.size(); ++var2) {
                        var3 += (Integer) var4.get(var2);
                        ++var1x;
                    }

                    var1x = var3 / var1x;
                    EventBus.getDefault().post(new UnlockSingValueCalEvent(var1x + 3, var1));
                    ((BraceletUserBean) BraceletPresenter.this.braceletUser.get(var1)).setSingValue(Math.abs(var1x) + 3);
                    BraceletPresenter.this.mSignValue = Math.abs(var1x) + 3;
                    Log.e(BraceletPresenter.TAG, "校准成功>>>>>>" + var1 + ">>>>>>mSignValue>>>>>" + BraceletPresenter.this.mSignValue);
                }
            }
        }, 5000L);
    }

    public void startLockDomain() {
        isDomainUnlock = false;
        isDomainlock = true;
        ArrayList<String> var1 = new ArrayList();

        this.getCloseDoorUser();
        var1.addAll(this.getChecklist());
        Bundle var2 = new Bundle();
        var2.putStringArrayList("addresses", (ArrayList) var1);
        this.startActivity(var2, UnlockActivity.UNLOCK_LIST);
        this.deletUser();
    }


    public boolean isLock() {
        return UnlockActivity.state == UnlockActivity.LOCK;
    }

}
