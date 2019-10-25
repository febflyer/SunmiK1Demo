package com.sunmi.sunmik1demo.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sunmi.scalelibrary.ScaleManager;
import com.sunmi.sunmik1demo.BaseActivity;
import com.sunmi.sunmik1demo.R;
import com.sunmi.sunmik1demo.fragment.BackgroundManagerFragment;
import com.sunmi.sunmik1demo.fragment.BraceletFragment;
import com.sunmi.sunmik1demo.fragment.DeviceFragment;
import com.sunmi.sunmik1demo.fragment.GoodsManagerFragment;
import com.sunmi.sunmik1demo.fragment.PayModeSettingFragment;
import com.sunmi.sunmik1demo.fragment.PrinterFragment;
import com.sunmi.sunmik1demo.present.VideoDisplay;
import com.sunmi.sunmik1demo.utils.ByteUtils;
import com.sunmi.sunmik1demo.utils.ScreenManager;


public class MoreActivity extends BaseActivity implements View.OnClickListener {

    private DrawerLayout drawerLayout;

    private FrameLayout ivBack;
    public ScaleManager mScaleManager;
    public ScreenManager screenManager = null;
    public Display[] displays;
    public VideoDisplay videoDisplay = null;


    private DeviceFragment deviceFragment;
    public GoodsManagerFragment goodsManagerFragment;
    private BraceletFragment braceletFragment;
    private PayModeSettingFragment payModeSettingFragment;
    private BackgroundManagerFragment backgroundManagerFragment;
    private PrinterFragment printerFragment;

    int openHand = 0;

    private TextView tv_device, tv_goods_manage, tv_pay_mode, tv_hand, tv_more;

    private FrameLayout fl_1, fl_2, fl_3, fl_4, fl_5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        initView();
        initAction();
        initData();
        connectScaleService();
    }

    @Override
    protected void onDestroy(){

        super.onDestroy();
    }




    private void connectScaleService() {
        mScaleManager = ScaleManager.getInstance(this);
        mScaleManager.connectService(new ScaleManager.ScaleServiceConnection() {
            @Override
            public void onServiceConnected() {
                Log.e("@@@@@@@@", "电子称连接成功");
            }

            @Override
            public void onServiceDisconnect() {
                Log.e("@@@@@@@@", "电子称连接失败");

            }
        });
    }

    private StringBuilder sb = new StringBuilder();
    private Handler myHandler = new Handler();

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        if (getFirstFragment() instanceof BackgroundManagerFragment || getFirstFragment() instanceof PrinterFragment) {
            return super.dispatchKeyEvent(event);
        }
        switch (action) {
            case KeyEvent.ACTION_DOWN:
                int unicodeChar = event.getUnicodeChar();
                sb.append((char) unicodeChar);
                if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
                    return super.dispatchKeyEvent(event);
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    return super.dispatchKeyEvent(event);
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    return super.dispatchKeyEvent(event);
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
                    return super.dispatchKeyEvent(event);
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_HOME) {
                    return super.dispatchKeyEvent(event);
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_POWER) {
                    return super.dispatchKeyEvent(event);
                }
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (sb.length() > 0) {
                            if (deviceFragment.isVisible() && deviceFragment.isShowScanResult) {
                                deviceFragment.append(sb.toString());
                            }

                            if (getFirstFragment() instanceof GoodsManagerFragment) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("id",sb.toString());
                                    backgroundManagerFragment.setArguments(bundle);
                                    replaceContent(backgroundManagerFragment, false);
                            }

                            sb.setLength(0);
                        }
                    }
                }, 300);
                return true;
            default:
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    private void initView() {
        ivBack = findViewById(R.id.iv_back);
        deviceFragment = new DeviceFragment();
        goodsManagerFragment = new GoodsManagerFragment();
        payModeSettingFragment = new PayModeSettingFragment();
        braceletFragment = new BraceletFragment();
        backgroundManagerFragment = new BackgroundManagerFragment();
        printerFragment = new PrinterFragment();
        tv_device = findViewById(R.id.tv_device);
        tv_goods_manage = findViewById(R.id.tv_goods_manage);
        tv_pay_mode = findViewById(R.id.tv_pay_mode);
        tv_hand = findViewById(R.id.tv_hand);
        tv_more = findViewById(R.id.tv_more);
        fl_1 = findViewById(R.id.fl_1);
        fl_2 = findViewById(R.id.fl_2);
        fl_3 = findViewById(R.id.fl_3);
        fl_4 = findViewById(R.id.fl_4);
        fl_5 = findViewById(R.id.fl_5);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        addContent(goodsManagerFragment, false);
        checkState(1);
//        addContent(printerFragment, false);
//        checkState(4);
    }

    private void initAction() {
        ivBack.setOnClickListener(this);
        fl_1.setOnClickListener(this);
        fl_2.setOnClickListener(this);
        fl_3.setOnClickListener(this);
        fl_4.setOnClickListener(this);
        fl_5.setOnClickListener(this);
//        tv_more.setOnClickListener(this);
    }

    private void initData() {
        screenManager = ScreenManager.getInstance();
        screenManager.init(this);
        displays = screenManager.getDisplays();
        videoDisplay = new VideoDisplay(this, displays[0], Environment.getExternalStorageDirectory().getPath() + "/video_01.mp4");
        fl_4.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_more:
                openHand++;
                if (openHand == 15) {
                    fl_4.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.fl_1:
                checkState(0);
                replaceContent(deviceFragment, false);
                break;
            case R.id.fl_2:
                checkState(1);
                replaceContent(goodsManagerFragment, false);
                break;
            case R.id.fl_3:
                checkState(2);
                replaceContent(payModeSettingFragment, false);
                break;
            case R.id.fl_4:
                checkState(3);
                this.replaceContent(braceletFragment, false);
                break;
            case R.id.fl_5:
                checkState(4);
                this.replaceContent(printerFragment, false);
                break;
            case R.id.iv_back:
                setResult(1);
                finish();
                break;
        }
        if (drawerLayout != null) {
            drawerLayout.closeDrawers();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (videoDisplay != null && videoDisplay.isShowing()) {
            videoDisplay.dismiss();
        }
    }

    private void checkState(int index) {
        fl_1.setBackgroundColor(Color.TRANSPARENT);
        fl_2.setBackgroundColor(Color.TRANSPARENT);
        fl_3.setBackgroundColor(Color.TRANSPARENT);
        fl_4.setBackgroundColor(Color.TRANSPARENT);
        fl_5.setBackgroundColor(Color.TRANSPARENT);
        switch (index) {
            case 0:
                fl_1.setBackgroundColor(Color.parseColor("#44ffffff"));
                break;
            case 1:
                fl_2.setBackgroundColor(Color.parseColor("#44ffffff"));
                break;
            case 2:
                fl_3.setBackgroundColor(Color.parseColor("#44ffffff"));
                break;
            case 3:
                fl_4.setBackgroundColor(Color.parseColor("#44ffffff"));
                break;
            case 4:
                fl_5.setBackgroundColor(Color.parseColor("#44ffffff"));
                break;
        }
    }


    //对返回键进行监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

}
