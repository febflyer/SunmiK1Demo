package com.sunmi.sunmik1demo.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.sunmi.sunmik1demo.bean.PaymentResponse;
import com.sunmi.sunmik1demo.bean.ThirdPayEvent;
import com.sunmi.sunmik1demo.fragment.PayModeSettingFragment;
import com.sunmi.sunmik1demo.present.TextDisplay;
import com.sunmi.sunmik1demo.presenter.P1POSPresenter;
import com.sunmi.sunmik1demo.presenter.PayMentPayPresenter;
import com.sunmi.sunmik1demo.presenter.UnionPayPreseter;
import com.sunmi.sunmik1demo.receiver.ResultReceiver;
import com.sunmi.sunmik1demo.ui.MainActivity;
import com.sunmi.sunmik1demo.R;
import com.sunmi.sunmik1demo.bean.MenuBean;
import com.sunmi.sunmik1demo.model.AlipaySmileModel;
import com.sunmi.sunmik1demo.presenter.AlipaySmilePresenter;
import com.sunmi.sunmik1demo.utils.InstallApkUtils;
import com.sunmi.sunmik1demo.utils.ResourcesUtils;
import com.sunmi.sunmik1demo.utils.SharePreferenceUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by highsixty on 2018/3/14.
 * mail  gaolulin@sunmi.com
 */

public class PayDialog extends AppCompatDialogFragment implements View.OnClickListener, ResultReceiver.AliResultCallback {
    private static final String TAG = "PayDialog";
    public final static int PAY_MODE_0 = 0;//现金
    public final static int PAY_MODE_1 = 1;//二维码
    public final static int PAY_MODE_2 = 2;//支付宝刷脸
    public final static int PAY_MODE_3 = 3;//真实二维码扣款
    public final static int PAY_MODE_4 = 4;//
    public final static int PAY_MODE_5 = 5;//银联刷脸
    public final static int PAY_MODE_6 = 6;//P1 POS刷卡支付,本次是配合P1临时演示功能

    public static String PayMoney = "0.01";


    private RadioGroup radioGroup;
    private ImageView ivTop;
    private ImageView ivMid;
    private ImageView ivBottom;
    private ImageView ivBottom2;
    private ImageView ivLogo;
    private TextView tvDescrib;
    private TextView tvMoney;
    private TextView tvFaccPayTips, tv_pay_fail_money, tv_pay_success, tv_name;
    private Button btnCancel;
    private Button btnOk;
    private Button btnComplete;
    private LinearLayout llyPay;
    private LinearLayout llyPayComplete;
    private LinearLayout llPaying, llNoFace, llCanPay, llPayFail;
    private Button btnCancelPaying;
    private TextView noFaceTitle;
    private TextView noFaceContent;
    private RadioButton rbOne, rbTwo, rbthree, rbfour;
    private String mMoney;
    private TextView tvMoneyComplete;
    boolean isShow = false;//防多次点击
    boolean isPay = false;//防多次点击

    private TextDisplay textDisplays;
    private StringBuilder sb = new StringBuilder();
    private AlipaySmilePresenter alipaySmilePresenter;
    private PayMentPayPresenter payMentPayPresenter;
    private P1POSPresenter p1POSPresenter;


    private UnionPayPreseter unionPayPreseter;


    MainActivity activity = null;


    private Handler myHandler = new Handler(Looper.getMainLooper());

    public final static int PAY_CASH = 1;
    public final static int PAY_CODE = 2;
    public final static int PAY_FACE = 4;
    public static final int PAY_POS  = 8;
    public final static String PAY_MODE_KEY = "PAY_MODE_KEY";
    private int supportPayMode = 7;
    boolean payment;//是否为收银台支付
    private int payMode = -1;
    private int payCompleteTime = 5;
    private boolean isPayComplete = true;

    private String phoneNumber;

    public PayDialog() {
        super();

    }

    public interface OnCompleteListener {
        void onCancel();

        void onSuccess(int payMode);

        void onComplete(int payMode);
    }

    public OnCompleteListener completeListener = null;

    public void setCompleteListener(OnCompleteListener completeListener) {
        this.completeListener = completeListener;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.paydialog_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initAction();
        initData();
    }


    private void initView(View view) {
        radioGroup = (RadioGroup) view.findViewById(R.id.rg);
        ivTop = (ImageView) view.findViewById(R.id.iv_top);
        ivMid = view.findViewById(R.id.iv_mid);
        ivBottom = (ImageView) view.findViewById(R.id.iv_bottom);
        ivBottom2 = (ImageView) view.findViewById(R.id.iv_bottom2);
        ivLogo = (ImageView) view.findViewById(R.id.iv_logo);
        tvDescrib = (TextView) view.findViewById(R.id.tv_describ);
        tvMoney = (TextView) view.findViewById(R.id.tv_money);
        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnOk = (Button) view.findViewById(R.id.btn_ok);
        btnComplete = (Button) view.findViewById(R.id.btn_complete);
        llyPay = (LinearLayout) view.findViewById(R.id.lly_pay);
        llyPayComplete = (LinearLayout) view.findViewById(R.id.lly_pay_complete);
        llPaying = view.findViewById(R.id.ll_paying);
        btnCancelPaying = view.findViewById(R.id.btn_cancal_paying);
        llCanPay = (LinearLayout) view.findViewById(R.id.ll_can_pay);
        llNoFace = (LinearLayout) view.findViewById(R.id.ll_no_face);
        llPayFail = (LinearLayout) view.findViewById(R.id.ll_pay_fail);
        rbOne = (RadioButton) view.findViewById(R.id.rbone);
        rbTwo = (RadioButton) view.findViewById(R.id.rbtwo);
        rbthree = view.findViewById(R.id.rbthree);
        rbfour = view.findViewById(R.id.rbfour);
        tvMoneyComplete = (TextView) view.findViewById(R.id.tv_money_complete);
        tvFaccPayTips = view.findViewById(R.id.tv_face_pay);
        tv_pay_fail_money = view.findViewById(R.id.tv_pay_fail_money);
        tv_pay_success = view.findViewById(R.id.tv_pay_success);
        tv_name = view.findViewById(R.id.tv_name);

        noFaceTitle = (TextView) view.findViewById(R.id.no_face_title);
        noFaceContent = (TextView) view.findViewById(R.id.no_face_content);

        rbOne.setAlpha(1f);
        rbTwo.setAlpha(0.7f);
        rbthree.setAlpha(0.7f);
        rbfour.setAlpha(0.7f);

    }

    private void initAction() {
        activity = (MainActivity) getActivity();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ivLogo.setVisibility(View.VISIBLE);
                switch (checkedId) {
                    case R.id.rbone:
//                        if(activity.scannerPresenter.isOpened())
//                            activity.scannerPresenter.setKeyUp();

                        payMode = PAY_MODE_0;
                        ivTop.setVisibility(View.VISIBLE);
                        ivMid.setVisibility(View.INVISIBLE);
                        ivBottom.setVisibility(View.INVISIBLE);
                        ivBottom2.setVisibility(View.INVISIBLE);
                        tvFaccPayTips.setVisibility(View.GONE);
                        llCanPay.setVisibility(View.VISIBLE);
                        llNoFace.setVisibility(View.GONE);
                        llPayFail.setVisibility(View.GONE);
                        tvDescrib.setText(ResourcesUtils.getString(getContext(), R.string.tips_pay_money));
                        ivLogo.setImageResource(R.drawable.cash);
                        tv_name.setText(ResourcesUtils.getString(getContext(), R.string.pay_need_money));
                        tvMoney.setText(mMoney);

                        btnOk.setVisibility(View.VISIBLE);
                        btnOk.setText(ResourcesUtils.getString(getContext(), R.string.pay_confirm));
                        rbOne.setAlpha(1f);
                        rbTwo.setAlpha(0.7f);
                        rbthree.setAlpha(0.7f);
                        rbfour.setAlpha(0.7f);

                        updateTextDisplay(0, ResourcesUtils.getString(R.string.pay_give_money) + mMoney, 0);

                        break;
                    case R.id.rbtwo:
//                        if(activity.scannerPresenter.isOpened())
//                            activity.scannerPresenter.setKeyDown();

                        payMode = PAY_MODE_1;
                        ivTop.setVisibility(View.INVISIBLE);
                        ivMid.setVisibility(View.VISIBLE);
                        ivBottom.setVisibility(View.INVISIBLE);
                        ivBottom2.setVisibility(View.INVISIBLE);
                        tvFaccPayTips.setVisibility(View.GONE);
                        llCanPay.setVisibility(View.VISIBLE);
                        llNoFace.setVisibility(View.GONE);
                        llPayFail.setVisibility(View.GONE);
                        tvDescrib.setText(ResourcesUtils.getString(getContext(), R.string.tips_pay_qrcode));
                        ivLogo.setImageResource(R.drawable.paycode);
                        tv_name.setText(ResourcesUtils.getString(getContext(), R.string.pay_need_money));
                        tvMoney.setText(ResourcesUtils.getString(getContext(), R.string.units_money_units) + PayMoney);

                        btnOk.setVisibility(payment ? View.VISIBLE : View.GONE);
                        btnOk.setText(R.string.pay_start_qrcode);
                        rbOne.setAlpha(0.7f);
                        rbTwo.setAlpha(1);
                        rbthree.setAlpha(0.7f);
                        rbfour.setAlpha(0.7f);

                        updateTextDisplay(1, ResourcesUtils.getString(R.string.pay_give_money) + ResourcesUtils.getString(R.string.units_money_units) + PayMoney, 0);
                        break;
                    case R.id.rbthree:
//                        if(activity.scannerPresenter.isOpened())
//                            activity.scannerPresenter.setKeyUp();

                        payMode = PAY_MODE_2;
                        ivTop.setVisibility(View.INVISIBLE);
                        ivMid.setVisibility(View.INVISIBLE);
                        ivBottom.setVisibility(View.VISIBLE);
                        ivBottom2.setVisibility(View.INVISIBLE);
                        rbOne.setAlpha(0.7f);
                        rbTwo.setAlpha(0.7f);
                        rbthree.setAlpha(1);
                        rbfour.setAlpha(0.7f);

                        tvFaccPayTips.setVisibility(View.GONE);
                        llCanPay.setVisibility(View.VISIBLE);
                        llNoFace.setVisibility(View.GONE);
                        llPayFail.setVisibility(View.GONE);

                        if (!isCanFacePay()) {
                            showNoCanFacePay();
                            return;
                        }
                        tv_name.setText(ResourcesUtils.getString(getContext(), R.string.pay_total_moeny) + mMoney + "  " + ResourcesUtils.getString(getContext(), R.string.pay_give_money));
                        tvMoney.setText(ResourcesUtils.getString(getContext(), R.string.units_money_units) + PayMoney);
                        tvFaccPayTips.setVisibility(View.VISIBLE);
                        tvDescrib.setText(ResourcesUtils.getString(getContext(), R.string.pay_face_tips));
                        ivLogo.setImageResource(R.drawable.face_recognition);
                        btnOk.setVisibility(View.VISIBLE);
                        btnOk.setText(ResourcesUtils.getString(getContext(), R.string.pay_get_moeny));
                        updateTextDisplay(2, ResourcesUtils.getString(R.string.pay_give_money) + ResourcesUtils.getString(R.string.units_money_units) + PayMoney, 0);
                        break;
                    case R.id.rbfour:
                        payMode = PAY_MODE_6;
                        ivTop.setVisibility(View.INVISIBLE);
                        ivMid.setVisibility(View.INVISIBLE);
                        ivBottom.setVisibility(View.INVISIBLE);
                        ivBottom2.setVisibility(View.VISIBLE);
                        rbOne.setAlpha(0.7f);
                        rbTwo.setAlpha(0.7f);
                        rbthree.setAlpha(0.7f);
                        rbfour.setAlpha(1);

                        tvFaccPayTips.setVisibility(View.GONE);
                        llCanPay.setVisibility(View.VISIBLE);
                        llNoFace.setVisibility(View.GONE);
                        llPayFail.setVisibility(View.GONE);

//                        if (!bP1Connected){
//                            showNoCanPosPay();
//                            return;
//                        }
                        tv_name.setText(ResourcesUtils.getString(getContext(), R.string.pay_total_moeny) + mMoney + "  " + ResourcesUtils.getString(getContext(), R.string.pay_give_money));
                        tvMoney.setText(ResourcesUtils.getString(getContext(), R.string.units_money_units) + PayMoney);
                        tvFaccPayTips.setVisibility(View.GONE);
                        tvDescrib.setText(ResourcesUtils.getString(getContext(), R.string.pay_pos_tips));
                        ivLogo.setImageResource(R.drawable.paypos);
                        btnOk.setVisibility(View.VISIBLE);
                        btnOk.setText(ResourcesUtils.getString(getContext(), R.string.pay_get_moeny));
                        updateTextDisplay(2, ResourcesUtils.getString(R.string.pay_give_money) + ResourcesUtils.getString(R.string.units_money_units) + PayMoney, 0);
                        break;
                    default:
                        break;
                }

                switch (supportPayMode) {

                    case PAY_CASH + PAY_CODE + PAY_FACE:
                        break;
                    case PAY_CODE + PAY_FACE:
                        ivTop.setVisibility(View.GONE);
                        break;
                    case PAY_FACE:
                        ivTop.setVisibility(View.GONE);
                        ivMid.setVisibility(View.GONE);
                        break;
                }

            }
        });
        btnComplete.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnCancelPaying.setOnClickListener(this);

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                int action = event.getAction();
                switch (action) {
                    case KeyEvent.ACTION_DOWN:
                        int unicodeChar = event.getUnicodeChar();
                        sb.append((char) unicodeChar);
                        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
                            return false;
                        }
                        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
                            return false;
                        }
                        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                            return false;
                        }
                        if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
                            return false;
                        }
                        if (event.getKeyCode() == KeyEvent.KEYCODE_HOME) {
                            return false;
                        }
                        if (event.getKeyCode() == KeyEvent.KEYCODE_POWER) {
                            return false;
                        }
                        final int len = sb.length();
                        sendMessageToUser(sb.toString());
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (len != sb.length()) return;
                                if (sb.length() > 0) {
                                    if (rbTwo.isChecked()) {
                                        payByCode(sb.toString());
                                    }
                                    sb.setLength(0);
                                }
                            }
                        }, 200);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        if (textDisplays != null) {
            textDisplays.setOnClickListener(this);
        }
    }


    private void initData() {
        Bundle bundle = getArguments();
        mMoney = bundle.getString("MONEY", "0.00");
        boolean isRealDeal = (boolean) SharePreferenceUtil.getParam(getContext(), PayModeSettingFragment.IS_REAL_DEAL, PayModeSettingFragment.default_isRealDeal);
        if (isRealDeal) {
            PayMoney = mMoney.substring(1);
            Toast.makeText(getContext(), "此次交易为真实金额扣款", Toast.LENGTH_LONG).show();
        } else {
            PayMoney = "0.01";
        }
        supportPayMode = bundle.getInt("PAYMODE", 7);
        MenuBean menuBean = JSON.parseObject(bundle.getString("GOODS", ""), MenuBean.class);
        Log.e(TAG, menuBean.toString());
        tvMoney.setText(mMoney);
        tvMoneyComplete.setText(mMoney);

        payment = (boolean) SharePreferenceUtil.getParam(getContext(), PayModeSettingFragment.PAYMENT_PAY_KEY, PayModeSettingFragment.default_SunmiPay);
        if (payment) {
            payMentPayPresenter.init(this);
        } else {
            alipaySmilePresenter.setGoods(menuBean);
            alipaySmilePresenter.init(mMoney.substring(1), ResourcesUtils.getString(getContext(), R.string.menus_title), ResourcesUtils.getString(getContext(), R.string.menus_title2));
        }


        switch (supportPayMode) {
            case PAY_CASH + PAY_CODE + PAY_FACE:
                payMode = PAY_MODE_0;
                break;
            case PAY_CODE + PAY_FACE:
                payMode = PAY_MODE_1;
                rbOne.setVisibility(View.GONE);
                ivTop.setVisibility(View.GONE);
                rbTwo.performClick();
                rbTwo.setChecked(true);
                break;
            case PAY_FACE:
                payMode = PAY_MODE_2;
                rbOne.setVisibility(View.GONE);
                ivTop.setVisibility(View.GONE);
                rbTwo.setVisibility(View.GONE);
                ivMid.setVisibility(View.GONE);
                rbthree.performClick();
                rbthree.setChecked(true);
                llyPay.setVisibility(View.GONE);
                llPaying.setVisibility(View.VISIBLE);
                startFacePaying();
                break;
        }
        textDisplays = activity.textDisplay;
        if (!payment) {
            updateTextDisplay(ResourcesUtils.getString(R.string.pay_give_money) + mMoney, 0);
        }
    }

    public void setAlipaySmilePresenter(AlipaySmilePresenter alipaySmilePresenter) {
        this.alipaySmilePresenter = alipaySmilePresenter;
    }

    public void setPayMentPayPresenter(PayMentPayPresenter payMentPayPresenter) {
        this.payMentPayPresenter = payMentPayPresenter;
    }

    public void setUnionPayPreseter(UnionPayPreseter unionPayPreseter) {
        this.unionPayPreseter = unionPayPreseter;
    }

    public void setP1POSPresenter(P1POSPresenter p1POSPresenter) {
        this.p1POSPresenter = p1POSPresenter;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
//                if(activity.scannerPresenter.isOpened())
//                    activity.scannerPresenter.setKeyUp();

                if (completeListener != null) {
                    completeListener.onCancel();
                }
                dismiss();
                break;
            case R.id.btn_ok:
                if (isPay) {
                    return;
                }
                isPay = true;
                rbOne.setClickable(false);
                rbTwo.setClickable(false);
                rbthree.setClickable(false);
                if (payMode == 0) {
                    showSuccess(payMode);
                } else if (payMode == 1) {
                    if (payment) {
//                        if (payCode.startsWith("28") || payCode.startsWith("13")) {
//                            payMode = PAY_MODE_4;
//                        } else {
//                            payMode = PAY_MODE_3;
//                        }
                        llyPay.setVisibility(View.GONE);
                        llPaying.setVisibility(View.VISIBLE);
                        String orderId = "" + (System.currentTimeMillis() / 1000) + (int) (Math.random() * 9000 + 1000);
                        payMentPayPresenter.startPayCode(orderId, "", 1);
                    }
                }else if (payMode == 6){    //POS付款,临时演示
                    llyPay.setVisibility(View.GONE);
                    llPaying.setVisibility(View.VISIBLE);
                    p1POSPresenter.startPayByPOS(mMoney.substring(1));  //mMoney的第一个字符是¥,所以得去掉
                    p1POSPresenter.setPOSPayCallback(posPayCallback);

                } else {
                    startFacePaying();
                }
                break;
            case R.id.btn_complete:
                payCompleteTime = 5;
                isPayComplete = true;
                isPay = false;
                if (completeListener != null) {
                    if (rbOne.isChecked()) {
                        completeListener.onComplete(payMode);
                    } else if (rbTwo.isChecked()) {
                        completeListener.onComplete(payMode);
                    } else if (rbthree.isChecked()) {
                        completeListener.onComplete(payMode);
                    } else if (rbfour.isChecked()) {
                        completeListener.onComplete(payMode);
                    }
                }
                dismiss();
                break;
            case R.id.btn_cancal_paying:    //中断支付
                isPay = false;
                showFail("pos cancel");
                break;

            case R.id.paymode_one:
                rbOne.performClick();
                break;
            case R.id.paymode_two:
                rbTwo.performClick();
                break;
            case R.id.paymode_three:
                rbthree.performClick();
                break;

            case R.id.present_fail_one:
                btnOk.performClick();
                break;
            case R.id.present_fail_two:
                textDisplays.update(ResourcesUtils.getString(getContext(), R.string.pay_give_money) + mMoney);
                rbOne.performClick();
                break;
            case R.id.present_fail_three:
                btnCancel.performClick();
                break;
            default:
                break;
        }
    }

    private void startFacePaying() {
        llyPay.setVisibility(View.GONE);
        llPaying.setVisibility(View.VISIBLE);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateTextDisplay(mMoney, 3);
            }
        });
        if (unionPayPreseter.isHave()) {
            unionPayPreseter.startThirdPay(PayMoney);
            unionPayPreseter.setCallBack(unionPayCallBack);
        } else if (payment) {
            String orderId = "" + (System.currentTimeMillis() / 1000) + (int) (Math.random() * 9000 + 1000);
            payMentPayPresenter.startFaceService(orderId, phoneNumber);
        } else {
            alipaySmilePresenter.setPhoneNumber(phoneNumber);
            alipaySmilePresenter.startFaceService(AlicallBack);
        }
    }

    //收银台支付回调

    @Override
    public void onSuccess(PaymentResponse response) {
        Log.e(TAG, "onSuccess==" + response.appType);
        if ("01".equals(response.appType)) {
            payMode = PAY_MODE_3;
            showSuccess(payMode);
        } else {
            showSuccess(payMode);
        }
    }

    @Override
    public void onFail(PaymentResponse response) {
        isPay = false;

        if (response == null) {
            showFail("no install SunmiPay");
        } else {
            if ("01".equals(response.appType)) {
                showPayCodeFail(response.resultMsg);
            } else {
                showFail(response.resultMsg);
            }
        }

    }

    /**
     * 银联刷脸回调
     */
    UnionPayPreseter.UnionPayCallBack unionPayCallBack = new UnionPayPreseter.UnionPayCallBack() {
        @Override
        public void onDetectResult(boolean result, String msg) {
            isPay = false;
        }

        @Override
        public void onPayResult(final boolean result, final String msg) {
            Log.i(TAG, "onPayResult =====" + msg);
            isPay = false;
            if (TextUtils.isEmpty(msg)) {
                showFail("失败");
                return;
            }

            final ThirdPayEvent mEvent = JSON.parseObject(msg, new TypeReference<ThirdPayEvent>() {
            });

            Log.i(TAG, "onPayResult=====" + mEvent.toString());

            if (mEvent != null && mEvent.isResult()) {
                payMode = PAY_MODE_5;
                showSuccess(PAY_MODE_5);

            } else {

                showFail(msg);

            }

        }
    };

    //支付宝刷脸支付回调
    AlipaySmilePresenter.AlipaySmileCallBack AlicallBack = new AlipaySmilePresenter.AlipaySmileCallBack() {
        @Override
        public void onStartFaceService() {
            sendMessageToUser("onStartFaceService");
            isPay = false;

        }

        @Override
        public void onFaceSuccess(String code, String msg) {
            sendMessageToUser("刷脸" + code + "  " + msg);

        }

        @Override
        public void onSuccess(String code, String msg) {
            sendMessageToUser("支付成功" + code + "  " + msg);
            showSuccess(payMode);
        }

        @Override
        public void onGetMetaInfo(String metaInfo) {


        }

        @Override
        public void onGetZimIdSuccess(String zimId) {
            sendMessageToUser("获得id成功" + zimId);
            isPay = false;
        }


        @Override
        public void onFail(final String code, final String msg) {
            sendMessageToUser("失败" + code + "  " + msg);
            isPay = false;
            showFail(msg);
        }

    };

    //P1 POS刷卡支付回调
    P1POSPresenter.POSPayCallback posPayCallback = new P1POSPresenter.POSPayCallback() {
        @Override
        public void onSuc() {
            showSuccess(payMode);
        }

        @Override
        public void onFail() {
            if (isPay) {
                isPay = false;
                showFail("pos fail");
            }
        }
    };

    //选择交易方式
    private void enableChoose(boolean enable) {
        rbOne.setClickable(enable);
        rbTwo.setClickable(enable);
        rbthree.setClickable(enable);
    }

    private void showSuccess(final int payMode) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                enableChoose(false);
                isPay = false;
                llPaying.setVisibility(View.GONE);
                llyPay.setVisibility(View.GONE);

                llyPayComplete.setVisibility(View.VISIBLE);

                tvMoneyComplete.setText(ResourcesUtils.getString(getContext(), R.string.units_money_units) + PayMoney);
                tv_pay_success.setText(R.string.pay_pay_true);
                if (completeListener != null) {
                    completeListener.onSuccess(payMode);
                    if (!isPayComplete) {
                        return;
                    }
                    updatePresentationByPay(payMode);
                    isPayComplete = false;
                    btnComplete.setText(ResourcesUtils.getString(R.string.tips_confirm) + "(" + payCompleteTime + ")");
                    paySuccessToAutoComplete();
                }
            }
        });
    }

    private void showPayCodeFail(final String msg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                enableChoose(true);
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();

                llyPay.setVisibility(View.VISIBLE);
                llPaying.setVisibility(View.GONE);

                btnOk.setText(ResourcesUtils.getString(getContext(), R.string.pay_repay));
                updateTextDisplay(mMoney, 4);

            }
        });

    }

    private void showFail(final String msg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                enableChoose(true);

                llPaying.setVisibility(View.GONE);
                llCanPay.setVisibility(View.GONE);
                llNoFace.setVisibility(View.GONE);
                ivLogo.setVisibility(View.GONE);

                llyPay.setVisibility(View.VISIBLE);
                btnOk.setVisibility(View.VISIBLE);
                llPayFail.setVisibility(View.VISIBLE);

                tv_pay_fail_money.setText(ResourcesUtils.getString(getContext(), R.string.pay_pay_moeny) + ResourcesUtils.getString(getContext(), R.string.units_money) + PayMoney);

                btnOk.setText(ResourcesUtils.getString(getContext(), R.string.pay_repay));

                updateTextDisplay(mMoney, 4);

            }
        });
    }

    public void sendMessageToUser(final String msg) {
        Log.i("@@@@@@", msg);
    }

    //会员id
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    //扫码支付
    public void payByCode(String payCode) {
        if (rbTwo.isChecked()) {
            rbOne.setClickable(false);
            rbTwo.setClickable(false);
            rbthree.setClickable(false);
            llyPay.setVisibility(View.GONE);
            llyPayComplete.setVisibility(View.VISIBLE);
            if (completeListener != null) {
                completeListener.onSuccess(payMode);
                updatePresentationByPay(payMode);
                isPayComplete = false;
                btnComplete.setText(ResourcesUtils.getString(R.string.tips_confirm) + "(" + payCompleteTime + ")");
                paySuccessToAutoComplete();
            }

        }
    }

    private void paySuccessToAutoComplete() {
        if (isPayComplete) {
            return;
        }
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                payCompleteTime--;
                btnComplete.setText(ResourcesUtils.getString(R.string.tips_confirm) + "(" + payCompleteTime + ")");
                if (payCompleteTime == -1) {
                    btnComplete.performClick();
                    btnComplete.setText(ResourcesUtils.getString(R.string.tips_confirm));
                    return;
                }
                paySuccessToAutoComplete();

            }
        }, 950);
    }

    private void showNoCanFacePay() {
        ivLogo.setImageResource(R.drawable.face_unsupport);
        llCanPay.setVisibility(View.GONE);
        llNoFace.setVisibility(View.VISIBLE);
        llPayFail.setVisibility(View.GONE);
        btnOk.setVisibility(View.GONE);
        if (!isHaveCamera() && !MainActivity.isVertical) {
            noFaceTitle.setText(ResourcesUtils.getString(R.string.pay_noface));
            noFaceContent.setText(ResourcesUtils.getString(R.string.pay_noface_change));
        } else if (!InstallApkUtils.checkApkExist(getContext(), InstallApkUtils.smilePkgName)) {
            noFaceTitle.setText(ResourcesUtils.getString(R.string.tips_no_find_smile_title));
            noFaceContent.setText(ResourcesUtils.getString(R.string.tips_find_smile_content));
        }
    }

    private void updatePresentationByPay(int payMode) {
        //使用收银台时，直接展示欢迎
        if (payment) {
            sayBye();
        } else {
            updateTextDisplay(ResourcesUtils.getString(R.string.pay_confirm_true) + mMoney, 1);
            //注释掉by mayflower on 191028,这东西跟主界面的确认按钮会存在时间差,在这个显示之前点确认,会导致这界面一直存在
//            myHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    sayBye();
//
//                }
//            }, 2000);
        }
    }


    private void sayBye() {
        updateTextDisplay(ResourcesUtils.getString(R.string.tips_bye), 2);
    }


    private void updateTextDisplay(String context, int state) {
        updateTextDisplay(1, context, state);
    }

    private void updateTextDisplay(int selet, String context, int state) {
        if (!payment) {
            if (null != textDisplays && !textDisplays.isShow) {
                textDisplays.show();
                textDisplays.update(context, state);
                textDisplays.setSelect(selet);
            } else if (null != textDisplays) {
                textDisplays.update(context, state);
                textDisplays.setSelect(selet);
            }
        } else if (state == 2) {
            if (null != textDisplays && !textDisplays.isShow) {
                textDisplays.show();
                textDisplays.update(context, state);
            } else if (null != textDisplays) {
                textDisplays.update(context, state);
            }
        }
    }

    private boolean isCanFacePay() {
        if (isHaveCamera() && (InstallApkUtils.checkApkExist(getContext(), InstallApkUtils.smilePkgName) || unionPayPreseter.isHave())) {
            return true;
        }
        return false;
    }

    private boolean isHaveCamera() {
        HashMap<String, UsbDevice> deviceHashMap = ((UsbManager) getActivity().getSystemService(getActivity().USB_SERVICE)).getDeviceList();
        Log.e(TAG, "isHaveCamera: " + deviceHashMap.size());
        for (Map.Entry entry : deviceHashMap.entrySet()) {
            UsbDevice usbDevice = (UsbDevice) entry.getValue();
            Log.e(TAG, "detectUsbDeviceWithUsbManager: " + entry.getKey() + "======== " + usbDevice);
            Log.e(TAG, "isHaveCamera: " + usbDevice.getInterface(0).getName());
            if (!TextUtils.isEmpty(usbDevice.getInterface(0).getName()) && usbDevice.getInterface(0).getName().contains("Orb")) {
                return true;
            }
            if (!TextUtils.isEmpty(usbDevice.getInterface(0).getName()) && usbDevice.getInterface(0).getName().contains("Astra")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (isShow) {
            return;
        }
        super.show(manager, tag);
        isShow = true;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        isShow = false;
        isPayComplete = true;
    }
}
