package com.sunmi.sunmik1demo.db;


import com.sunmi.sunmik1demo.MyApplication;
import com.sunmi.sunmik1demo.bean.FilterMacBeanDao;
import com.sunmi.sunmik1demo.bean.blescan.FilterMacBean;
//import com.sunmi.sunmit2demo.bean.blescan.FilterMacBeanDao;

import java.util.List;


/**
 * Created by thinkpad on 2018/1/9.
 */

public class FilterAddressDbManager {
    public static final int TYPE_BRACELET = 1;
    public static final int TYPE_POS = 2;
     private FilterMacBeanDao mDaoSession;

    public FilterAddressDbManager() {
        mDaoSession = MyApplication.getInstance().getDaoSession().getFilterMacBeanDao();
    }

    public void addFilterBracelet(String address){
          try{
              mDaoSession.insertInTx(new FilterMacBean(address,FilterAddressDbManager.TYPE_BRACELET));
          }catch (Exception e){
              e.printStackTrace();
          }
    }

    public void deleteFilterAddress(FilterMacBean bean){
        try{
            mDaoSession.deleteByKey(bean.getId());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteAllFilterBracelet(){
        try{
            mDaoSession.queryBuilder().where(FilterMacBeanDao.Properties.Type.eq(FilterAddressDbManager.TYPE_BRACELET)).buildDelete().executeDeleteWithoutDetachingEntities();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<FilterMacBean> getAllFilterBracelet(){
        try{
            List<FilterMacBean> temp = mDaoSession.queryBuilder().where(FilterMacBeanDao.Properties.Type.eq(FilterAddressDbManager.TYPE_BRACELET)).list();
          return  temp;
        }catch (Exception e){
            e.printStackTrace();
        }

        return  null;
    }

    public long getFilterBraceletCount(){
        try{
          return  mDaoSession.queryBuilder().where(FilterMacBeanDao.Properties.Type.eq(FilterAddressDbManager.TYPE_BRACELET)).buildCount().count();
        }catch (Exception e){
            e.printStackTrace();
        }

        return 0;
    }


    public void addFilterPos(String address){
        try{
            mDaoSession.insertInTx(new FilterMacBean(address,FilterAddressDbManager.TYPE_POS));
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void deleteAllFilterPos(){
        try{
            mDaoSession.queryBuilder().where(FilterMacBeanDao.Properties.Type.eq(FilterAddressDbManager.TYPE_POS)).buildDelete().executeDeleteWithoutDetachingEntities();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<FilterMacBean> getAllFilterPos(){
        try{
            List<FilterMacBean> temp = mDaoSession.queryBuilder().where(FilterMacBeanDao.Properties.Type.eq(FilterAddressDbManager.TYPE_POS)).list();
            return  temp;
        }catch (Exception e){
            e.printStackTrace();
        }

        return  null;
    }

    public long getFilterPosCount(){
        try{
            return  mDaoSession.queryBuilder().where(FilterMacBeanDao.Properties.Type.eq(FilterAddressDbManager.TYPE_POS)).buildCount().count();
        }catch (Exception e){
            e.printStackTrace();
        }

        return 0;
    }
}
