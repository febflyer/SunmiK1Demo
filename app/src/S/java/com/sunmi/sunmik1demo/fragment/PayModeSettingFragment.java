package com.sunmi.sunmik1demo.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.sunmi.sunmik1demo.BaseFragment;
import com.sunmi.sunmik1demo.MyApplication;
import com.sunmi.sunmik1demo.R;
import com.sunmi.sunmik1demo.dialog.PayDialog;
import com.sunmi.sunmik1demo.ui.MainActivity;
import com.sunmi.sunmik1demo.utils.InstallApkUtils;
import com.sunmi.sunmik1demo.utils.SharePreferenceUtil;
import com.sunmi.sunmik1demo.view.CustomDialog;

public class PayModeSettingFragment extends BaseFragment {

    Switch face;
    private Switch swVipFace;
    private Switch swPaymentFace;
    private Switch swPaymentReal;

    public final static String VIP_PAY_KEY = "VIP_PAY_KEY";
    public final static String PAYMENT_PAY_KEY = "PAYMENT_PAY_KEY";
    public final static String IS_REAL_DEAL = "IS_REAL_DEAL";
    CustomDialog customDialog;

    public final static boolean default_SunmiPay = false;
    public final static boolean default_isRealDeal = false;//是否真实交易,true表示按 显示金额扣款

    @Override
    protected int setView() {
        return R.layout.fragment_pay_mode_setting;
    }

    @Override
    protected void init(View view) {

        swVipFace = view.findViewById(R.id.sw_vip_face);
        swPaymentFace = view.findViewById(R.id.sw_payment_face);

        swPaymentReal = view.findViewById(R.id.sw_payment_real);


        face = view.findViewById(R.id.sw_face);

        int payMode = (int) SharePreferenceUtil.getParam(getContext(), PayDialog.PAY_MODE_KEY, 7);

        switch (payMode) {
            case PayDialog.PAY_FACE:
                face.setChecked(true);
                break;
            case PayDialog.PAY_FACE | PayDialog.PAY_CODE | PayDialog.PAY_CASH:
                face.setChecked(false);
                break;
        }
        boolean vip = (boolean) SharePreferenceUtil.getParam(getContext(), PayModeSettingFragment.VIP_PAY_KEY, false);
        boolean payment = (boolean) SharePreferenceUtil.getParam(getContext(), PayModeSettingFragment.PAYMENT_PAY_KEY, default_SunmiPay);
        boolean isRealDeal = (boolean) SharePreferenceUtil.getParam(getContext(), PayModeSettingFragment.IS_REAL_DEAL, default_isRealDeal);

        swVipFace.setChecked(vip);
        swPaymentFace.setChecked(payment);
        swPaymentReal.setChecked(!isRealDeal);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        face.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (MainActivity.isVertical || MyApplication.getInstance().isHaveCamera()) {
                        SharePreferenceUtil.setParam(getContext(), PayDialog.PAY_MODE_KEY, PayDialog.PAY_FACE);
                    }
                } else {
                    if (MainActivity.isVertical) {
                        SharePreferenceUtil.setParam(getContext(), PayDialog.PAY_MODE_KEY, PayDialog.PAY_FACE | PayDialog.PAY_CODE);
                    } else {
                        SharePreferenceUtil.setParam(getContext(), PayDialog.PAY_MODE_KEY, PayDialog.PAY_FACE | PayDialog.PAY_CODE | PayDialog.PAY_CASH);
                    }
                }
            }
        });

        swVipFace.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharePreferenceUtil.setParam(getContext(), PayModeSettingFragment.VIP_PAY_KEY, isChecked);
            }
        });
        swPaymentFace.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (InstallApkUtils.checkApkExist(getContext(), InstallApkUtils.SunmiPayPkgName)) {
                    SharePreferenceUtil.setParam(getContext(), PayModeSettingFragment.PAYMENT_PAY_KEY, isChecked);
                } else {
                    if (isChecked) {
                        if (customDialog == null) {
                            swPaymentFace.setChecked(false);
                            customDialog = new CustomDialog(getContext());
                            customDialog.setOnClickListener(tipsListener);
                            customDialog.show();
                            TextView tv_content = customDialog.findViewById(R.id.tv_content);
                            tv_content.setText(R.string.pay_sunmipay_tip);
                        } else if (!customDialog.isShowing()) {
                            customDialog.show();
                        }
                        swPaymentFace.setChecked(false);
                    }
                }
            }
        });

        swPaymentReal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharePreferenceUtil.setParam(getContext(), PayModeSettingFragment.IS_REAL_DEAL, !isChecked);

            }
        });

    }

    private View.OnClickListener tipsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_cancel:
                    customDialog.dismiss();
                    break;
                default:
                    break;
                case R.id.tv_confirm:
                    customDialog.dismiss();
                    break;
            }
        }
    };

}
