package com.sunmi.sunmik1demo.unlock;

import android.text.TextUtils;
import android.util.Log;

import com.sunmi.sunmik1demo.R;
import com.sunmi.sunmik1demo.bean.blescan.BraceletUserBean;
import com.sunmi.sunmik1demo.db.BraceletUserDbManager;
import com.sunmi.sunmik1demo.utils.ResourcesUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BLEUserModel {
    private BraceletUserDbManager braceletUserDbManager;
    Map<String, BraceletUserBean> userBeanMap = new HashMap();

    public BLEUserModel() {
        if (this.braceletUserDbManager == null) {
            this.braceletUserDbManager = new BraceletUserDbManager();
        }

        if (this.braceletUserDbManager.getAllBraceletUser().size() == 0) {
            this.braceletUserDbManager.addBraceletUser(ResourcesUtils.getString(R.string.more_bracelet_user_1), "", R.drawable.bracelet_user_1);
            this.braceletUserDbManager.addBraceletUser(ResourcesUtils.getString(R.string.more_bracelet_user_2), "", R.drawable.bracelet_user_2);
            this.braceletUserDbManager.addBraceletUser(ResourcesUtils.getString(R.string.more_bracelet_user_3), "", R.drawable.bracelet_user_3);
            this.braceletUserDbManager.addBraceletUser(ResourcesUtils.getString(R.string.more_bracelet_user_4), "", R.drawable.bracelet_user_4);
            this.braceletUserDbManager.addBraceletUser(ResourcesUtils.getString(R.string.more_bracelet_user_5), "", R.drawable.bracelet_user_5);
            this.braceletUserDbManager.addBraceletUser(ResourcesUtils.getString(R.string.more_bracelet_user_6), "", R.drawable.bracelet_user_6);
            this.braceletUserDbManager.addBraceletUser(ResourcesUtils.getString(R.string.more_bracelet_user_7), "", R.drawable.bracelet_user_7);
            this.braceletUserDbManager.addBraceletUser(ResourcesUtils.getString(R.string.more_bracelet_user_8), "", R.drawable.bracelet_user_8);
            this.braceletUserDbManager.addBraceletUser(ResourcesUtils.getString(R.string.more_bracelet_user_9), "", R.drawable.bracelet_user_9);
            this.braceletUserDbManager.addBraceletUser(ResourcesUtils.getString(R.string.more_bracelet_user_10), "", R.drawable.bracelet_user_10);
        } else {
            updateUserBeanMap();
        }
    }

    public void updateUserBeanMap(){
        userBeanMap.clear();
        Iterator var1 = this.braceletUserDbManager.getAllBraceletUser().iterator();
        while (var1.hasNext()) {
            BraceletUserBean var2 = (BraceletUserBean) var1.next();
            if (!TextUtils.isEmpty(var2.getMac())) {
                userBeanMap.put(var2.getMac(), var2);
            }
        }
    }

    public Map<String, BraceletUserBean> getUserBeanMap() {
        if(userBeanMap.size()==0) {
            Iterator var1 = this.braceletUserDbManager.getAllBraceletUser().iterator();
            while (var1.hasNext()) {
                BraceletUserBean var2 = (BraceletUserBean) var1.next();
                if (!TextUtils.isEmpty(var2.getMac())) {
                    userBeanMap.put(var2.getMac(), var2);
                }
            }
        }
        return userBeanMap;

    }

    public List<BraceletUserBean> getAllBraceletUser() {
        return braceletUserDbManager.getAllBraceletUser();
    }

    public void updateBraceletUser(BraceletUserBean braceletUserBean) {
        braceletUserDbManager.updateBraceletUser(braceletUserBean);
    }


    public List<BraceletUserBean> getAllFilterBracelet() {
        return new ArrayList<>(userBeanMap.values());
    }
    public void addUser(BraceletUserBean braceletUserBean){
        braceletUserDbManager.updateBraceletUser(braceletUserBean);
        userBeanMap.put(braceletUserBean.getMac(),braceletUserBean);
    }
    public void removeUser(String key) {
        BraceletUserBean braceletUserBean = (BraceletUserBean) this.userBeanMap.get(key);
        braceletUserBean.setMac("");
        updateBraceletUser(braceletUserBean);
        userBeanMap.remove(key);
    }

    public String getName(String key){
        return userBeanMap.get(key).getName();
    }

    public BraceletUserBean getBraceletUserByMac(String mac){
        return  userBeanMap.get(mac);
    }
}
