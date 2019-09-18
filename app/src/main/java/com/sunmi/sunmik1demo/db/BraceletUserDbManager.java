//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunmi.sunmik1demo.db;

import com.sunmi.sunmik1demo.MyApplication;
import com.sunmi.sunmik1demo.bean.BraceletUserBeanDao;
import com.sunmi.sunmik1demo.bean.blescan.BraceletUserBean;
//import com.sunmi.sunmit2demo.bean.blescan.BraceletUserBeanDao;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class BraceletUserDbManager {
    private BraceletUserBeanDao mDaoSession = MyApplication.getInstance().getDaoSession().getBraceletUserBeanDao();

    public BraceletUserDbManager() {
    }

    public void addBraceletUser(String name, String address, int icon) {
        try {
            this.mDaoSession.insertInTx(new BraceletUserBean(name, address, icon));
        } catch (Exception var4) {
            var4.printStackTrace();
        }
    }

    public void deleteAllBraceletUser() {
        try {
            this.mDaoSession.deleteAll();
        } catch (Exception var2) {
            var2.printStackTrace();
        }
    }

    public void deleteBraceletUser(BraceletUserBean var1) {
        try {
            this.mDaoSession.deleteByKey(var1.getId());
        } catch (Exception var2) {
            var2.printStackTrace();
        }
    }

    public List<BraceletUserBean> getAllBraceletUser() {
        try {
            List var1 = this.mDaoSession.queryBuilder().list();
            return var1;
        } catch (Exception var2) {
            var2.printStackTrace();
        }
        return null;
    }

    public List<BraceletUserBean> getBraceletUserByMac(String var1) {
        try {
            List var3 = this.mDaoSession.queryBuilder().where(BraceletUserBeanDao.Properties.Mac.eq(var1), new WhereCondition[0]).list();
            return var3;
        } catch (Exception var2) {
            var2.printStackTrace();
        }
        return null;

    }

    public void updateBraceletUser(BraceletUserBean var1) {
        this.mDaoSession.update(var1);
    }
}
