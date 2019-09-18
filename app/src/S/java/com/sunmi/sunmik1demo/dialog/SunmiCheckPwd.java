//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunmi.sunmik1demo.dialog;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.sunmi.sunmik1demo.utils.SharePreferenceUtil;

public class SunmiCheckPwd {
    private static final String TAG = "SunmiCheckPwd";
    private Context mContext;
    private SunmiCheckPwd.SunmiUpdateInterface mSunmiUpdateInterface;
    private String mTempPwd = "";

    public SunmiCheckPwd(SunmiCheckPwd.SunmiUpdateInterface var1, Context var2) {
        this.mSunmiUpdateInterface = var1;
        this.mContext = var2;
    }

    public int getPwdInputNum() {
        return this.mTempPwd.length();
    }

    public void onClick(View view)
    {
        if(view==null) return;
        String tag = (String)view.getTag();
        if(tag!=null)
        {
            switch (tag)
            {
                case "N1":
                {
                    mTempPwd+="1";
                    break;
                }
                case "N2":
                {
                    mTempPwd+="2";
                    break;
                }
                case "N3":
                {
                    mTempPwd+="3";
                    break;
                }
                case "N4":
                {
                    mTempPwd+="4";
                    break;
                }
                case "N5":
                {
                    mTempPwd+="5";
                    break;
                }
                case "N6":
                {
                    mTempPwd+="6";
                    break;
                }
                case "N7":
                {
                    mTempPwd+="7";
                    break;
                }
                case "N8":
                {
                    mTempPwd+="8";
                    break;
                }
                case "N9":
                {
                    mTempPwd+="9";
                    break;
                }
                case "N0":
                {
                    mTempPwd+="0";
                    break;
                }
                case "ND":
                {
                    if(mTempPwd.length()>=1)
                    {
                        mTempPwd = mTempPwd.substring(0,mTempPwd.length()-1);
                    }
                    break;
                }
            }
        }

        if(mTempPwd.length()==4)
        {

            String pwd = "1111";
            try {
                pwd = (String)SharePreferenceUtil.getParam(this.mContext, "BLE_PWD_KEY", "1111");
            }catch (Exception e){
                Log.d(TAG,"Exception e:"+e);
            }

            if (TextUtils.isEmpty(pwd)){
                pwd = "1111";
            }

            if(mTempPwd.equals(pwd))
            {
                mSunmiUpdateInterface.updatepwd(mTempPwd.length(),1);
                mTempPwd="";
            }
            else
            {
                mSunmiUpdateInterface.updatepwd(mTempPwd.length(),2);
                mTempPwd="";
            }
        }
        else
        {
            mSunmiUpdateInterface.updatepwd(mTempPwd.length(),0);
        }
        //Log.e("ZHANGZEYUAN","=======>mTempPwd="+mTempPwd);
    }

    public interface SunmiUpdateInterface {
        void updatepwd(int var1, int var2);
    }
}
