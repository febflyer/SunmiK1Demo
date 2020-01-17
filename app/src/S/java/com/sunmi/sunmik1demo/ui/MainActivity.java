package com.sunmi.sunmik1demo.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Outline;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.sunmi.extprinterservice.ExtPrinterService;
import com.sunmi.idcardservice.IDCardServiceAidl;
import com.sunmi.idcardservice.MiFareCardAidl;
import com.sunmi.mifarecard.IMifareCard;
import com.sunmi.peripheral.printer.InnerPrinterCallback;
import com.sunmi.peripheral.printer.InnerPrinterException;
import com.sunmi.peripheral.printer.InnerPrinterManager;
import com.sunmi.peripheral.printer.SunmiPrinterService;
import com.sunmi.sunmik1demo.BaseActivity;
import com.sunmi.sunmik1demo.BasePresentationHelper;
import com.sunmi.sunmik1demo.R;
import com.sunmi.sunmik1demo.adapter.GoodsAdapter;
import com.sunmi.sunmik1demo.adapter.MenusAdapter;
import com.sunmi.sunmik1demo.bean.GoodsCode;
import com.sunmi.sunmik1demo.bean.GvBeans;
import com.sunmi.sunmik1demo.bean.MenusBean;
import com.sunmi.sunmik1demo.dialog.PayDialog;
import com.sunmi.sunmik1demo.eventbus.UpdateUnLockUserEvent;
import com.sunmi.sunmik1demo.fragment.GoodsManagerFragment;
import com.sunmi.sunmik1demo.fragment.PayModeSettingFragment;
import com.sunmi.sunmik1demo.model.AlipaySmileModel;
import com.sunmi.sunmik1demo.present.TextDisplay;
import com.sunmi.sunmik1demo.present.VideoDisplay;
import com.sunmi.sunmik1demo.present.VideoMenuDisplay;
import com.sunmi.sunmik1demo.presenter.AlipaySmilePresenter;
import com.sunmi.sunmik1demo.presenter.HCardSenderPresenter;
import com.sunmi.sunmik1demo.presenter.KCardReaderPresenter;
import com.sunmi.sunmik1demo.presenter.KCodeScannerPresenter2;
import com.sunmi.sunmik1demo.presenter.KPrinterPresenter;
import com.sunmi.sunmik1demo.presenter.P1POSPresenter;
import com.sunmi.sunmik1demo.presenter.PayMentPayPresenter;
import com.sunmi.sunmik1demo.presenter.PrinterPresenter;
import com.sunmi.sunmik1demo.presenter.ScalePresenter;
import com.sunmi.sunmik1demo.presenter.UnionPayPreseter;
import com.sunmi.sunmik1demo.presenter.CodeScannerPresenter;
import com.sunmi.sunmik1demo.unlock.UnlockServer;
import com.sunmi.sunmik1demo.utils.ByteUtils;
import com.sunmi.sunmik1demo.utils.ResourcesUtils;
import com.sunmi.sunmik1demo.utils.ScreenManager;
import com.sunmi.sunmik1demo.utils.SharePreferenceUtil;
import com.sunmi.sunmik1demo.utils.Utils;
import com.sunmi.sunmik1demo.view.CustomPopWindow;
import com.sunmi.sunmik1demo.view.Input2Dialog;
import com.sunmi.sunmik1demo.view.VipPayDialog;
import com.sunmi.widget.dialog.InputDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = "MainActivity";
    private ListView lvMenus;
    private MenusAdapter menusAdapter;
    private List<MenusBean> menus = new ArrayList<>();
    private RecyclerView reDrink;
    private RecyclerView reFruit;
    private RecyclerView re_snacks;
    private RecyclerView re_vegetables;
    private RecyclerView re_others;

    private FrameLayout flUnlockUser;
    private TextView tv_user_lock;
    private CircleImageView ivUserHeadIcon;
    private FrameLayout flShoppingCar;


    private LinearLayout scaleInfoRoot;
    private ImageView ivScaleStable;
    private ImageView ivScaleNet;
    private ImageView ivScaleZero;
    private TextView tvScaleNet;
    private TextView tvScalePnet;
    private ImageView ivScaleIcon;
    private TextView tvScalePrice;
    private TextView tvScaleTotal;
    private TextView tvScaleNetDescribe;
    private LinearLayout llScalePnet;
    private TextView tvScaleOverMax;
    private TextView btnScaleZero;
    private TextView btnScaleTare;
    private TextView btnScaleNumbTare;
    private TextView btnScaleClearTare;
    private ImageView ivScaleOverMax;

    private GoodsAdapter drinkAdapter;
    private GoodsAdapter fruitAdapter;
    private GoodsAdapter snackAdapter;
    private GoodsAdapter vegetableAdapter;
    private GoodsAdapter othersAdapter;

    private List<GvBeans> mDrinksBean;
    private List<GvBeans> mFruitsBean;
    private List<GvBeans> mSnacksBean;
    private List<GvBeans> mVegetablesBean;
    private List<GvBeans> mOthers;

    private TextView tvPrice;
    private TextView btnClear;
    private RelativeLayout rtlEmptyShopcar, rl_no_goods;
    private LinearLayout llyShopcar, ll_drinks, ll_snacks, ll_fruits, ll_vegetables, ll_others, main_ll_pay;

    private ImageView ivCar;
    private RelativeLayout rlCar;
    private TextView tvCar, tvCarMoeny;
    private TextView tvVipPay, tvVipK1Pay;

    private Button btnPay;//去付款
    private BottomSheetLayout bottomSheetLayout;
    private LinearLayout llK1ShoppingCar;


    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private Button btnMore;//更多功能
    private TextView tv_face_pay;//去付款
    private VideoDisplay videoDisplay = null;
    private ScreenManager screenManager = ScreenManager.getInstance();
    private VideoMenuDisplay videoMenuDisplay = null;
    public TextDisplay textDisplay = null;
    private PayDialog payDialog;
    private SunmiPrinterService woyouService = null;//商米标准打印 打印服务
    private ExtPrinterService extPrinterService = null;//k1 打印服务

    //add by mayflower--------------------------------------------------
    public static IDCardServiceAidl mIdCardService = null;   //身份证服务
    public static MiFareCardAidl mMifareCardService = null;  //非接服务，从mIdCardService.getMiFareCardService()得到
    private static IMifareCard mCardSenderService = null;        //收发卡机服务
//    public static       //灯服务
    //------------------------------------------------------------------

    private String goods_data;
    public static PrinterPresenter printerPresenter;
    public static KPrinterPresenter kPrinterPresenter;
    public UnlockServer.Proxy mProxy = null;
    CustomPopWindow popWindow;
    private boolean willwelcome;

    public static boolean isK1 = false;
    public static boolean isVertical = false;
    public static boolean isSecondIxist = false;
    public static boolean isSecondVertical = true;     //副屏
    SoundPool soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);


    private AlipaySmilePresenter alipaySmilePresenter;
    private AlipaySmileModel alipaySmileModel;
    private PayMentPayPresenter payMentPayPresenter;
    private ScalePresenter scalePresenter;
    private UnionPayPreseter unionPayPreseter;
    private P1POSPresenter p1POSPresenter;
    Input2Dialog mInputDialog;

    //add by MayFlower----------------------------------------
    public static KCodeScannerPresenter2 kCodeScannerPresenter;
    public static CodeScannerPresenter codeScannerPresenter;
    public static KCardReaderPresenter kCardReaderPresenter;
    public static HCardSenderPresenter hCardSenderPresenter;
    //--------------------------------------------------------

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        switchLanguage(1);  //切换语言系统

        setContentView(R.layout.activity_main);
        DisplayMetrics dm = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;// 屏幕宽度
        int height = dm.heightPixels;// 屏幕宽度
        Log.e("@@@", dm.densityDpi + "  " + dm.density);
        isK1 = Build.MODEL.equals("K1");

        //changed by mayflower，锁定为K1版
        isK1 = true;
        //-------------------------------

        isVertical = height > width;
        if (isK1) {
            connectKPrintService();
        }
//        else {
//        if(isSecondIxist && !isSecondVertical){   //反正都连上吧
            connectPrintService();
//        }

//        isVertical = false;


        EventBus.getDefault().register(this);
        menus.clear();
        initView();
        initData();
        initAction();

        //add by mayflower,for modules--------------------------------
//        initScanner();                  //串口扫码器
        bindCardService();              //非接
        connectCardSenderService();     //收发卡机
        //------------------------------------------------------------
    }

    public void initScanner(){
        kCodeScannerPresenter = KCodeScannerPresenter2.getInstance();
        kCodeScannerPresenter.start(getApplicationContext());
        kCodeScannerPresenter.setMode(1,0,1000);   //方便测试
//        kCodeScannerPresenter.setKeyDown();
//        kCodeScannerPresenter.setSuffix("0D0A");
//        kCodeScannerPresenter.setBuzzer(true);

        kCodeScannerPresenter.setOnDataReceiveListener(new KCodeScannerPresenter2.OnDataReceiveListener() {
            @Override
            public void onDataReceive(String data) {

                Log.d(TAG,"扫码广播数据:" + data + "[" + ByteUtils.str2HexString(data)+ "]" +"len:"+data.length()+ "\n");
//                showToast("扫码广播数据:" + data);
            }

            @Override
            public void onResponseTimeout(){
                Log.d(TAG,"广播命令返回超时，请检查系统版本或硬件连接。");
                //这里给没有广播的旧系统直接开串口来扫码吧
                initSerialScanner();
            }
        });
    }

    //连接打印服务
    private void connectPrintService() {

        try {
            InnerPrinterManager.getInstance().bindService(this,
                    innerPrinterCallback);
        } catch (InnerPrinterException e) {
            e.printStackTrace();
        }
    }

    private InnerPrinterCallback innerPrinterCallback = new InnerPrinterCallback() {
        @Override
        protected void onConnected(SunmiPrinterService service) {
            woyouService = service;
            printerPresenter = new PrinterPresenter(MainActivity.this, woyouService);

        }

        @Override
        protected void onDisconnected() {
            woyouService = null;

        }
    };

    //连接K1打印服务
    private void connectKPrintService() {
        Intent intent = new Intent();
        intent.setPackage("com.sunmi.extprinterservice");
        intent.setAction("com.sunmi.extprinterservice.PrinterService");
        bindService(intent, connService, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection connService = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            extPrinterService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            extPrinterService = ExtPrinterService.Stub.asInterface(service);
            kPrinterPresenter = new KPrinterPresenter(MainActivity.this, extPrinterService);
        }
    };

    //非接服务----------------------------------------------------
    private ServiceConnection cardReaderCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mIdCardService = IDCardServiceAidl.Stub.asInterface(service);
                            if (mIdCardService != null) {
                                Log.d("idcardservice", "id card connect.");
                                mMifareCardService = mIdCardService.getMiFareCardService();     //这一步耗时3.3s左右
                                if(mMifareCardService != null) {
                                    Log.d("idcardservice", "mifare card connnect.");
                                    kCardReaderPresenter = new KCardReaderPresenter(MainActivity.this, mIdCardService,mMifareCardService);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();


        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("idcardservice", "service disconnect.");
            mMifareCardService = null;
            mIdCardService = null;
        }
    };

    private void bindCardService() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setPackage("com.sunmi.idcardservice");
                intent.setAction("com.sunmi.idcard");
                bindService(intent, cardReaderCon, Context.BIND_AUTO_CREATE);
            }
        });
        thread.start();
    }

    private void unbindCardService() {
        unbindService(cardReaderCon);
    }
    //----------------------------------------------------------

    //收发卡机服务-----------------------------------------------
    private ServiceConnection cardSenderCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCardSenderService = IMifareCard.Stub.asInterface(service);
            Log.d(TAG, "收发卡机连接成功！");
            hCardSenderPresenter = new HCardSenderPresenter(MainActivity.this, mCardSenderService);
            hCardSenderPresenter.autoRunCard();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "收发卡机断开连接成功！");
        }
    };
    private void connectCardSenderService() {
        Intent intent = new Intent();
        intent.setPackage("com.sunmi.mifarecard");
        intent.setAction("com.sunmi.MifareCardService");
        startService(intent);
        bindService(intent, cardSenderCon, BIND_AUTO_CREATE);
    }

    private void disconnectCardSenderService(){ unbindService(cardSenderCon);}
    //----------------------------------------------------------

    protected void onStop() {
        super.onStop();
        this.willwelcome = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (menus.size() == 0) {
            if (videoDisplay != null) {
                videoDisplay.show();
            }
        }
        initBleService();

        if ((Boolean) SharePreferenceUtil.getParam(this, "BLE_KEY", false)) {
            if (this.willwelcome) {
                this.welcomeUserAnim();
            }

            this.willwelcome = false;
            this.flUnlockUser.setVisibility(View.VISIBLE);
        } else {
            this.flUnlockUser.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        int goodsMode = (int) SharePreferenceUtil.getParam(this, GoodsManagerFragment.GOODSMODE_KEY, GoodsManagerFragment.defaultGoodsMode);
        switch (goodsMode) {
            case 0:
                ll_fruits.setVisibility(View.GONE);
                ll_vegetables.setVisibility(View.GONE);
                ll_snacks.setVisibility(View.GONE);
                ll_drinks.setVisibility(View.GONE);
                scaleInfoRoot.setVisibility(View.GONE);
                rl_no_goods.setVisibility(View.VISIBLE);
                break;
            case GoodsManagerFragment.Goods_1 | GoodsManagerFragment.Goods_2:
                ll_fruits.setVisibility(View.GONE);
                ll_vegetables.setVisibility(View.GONE);
                ll_drinks.setVisibility(View.VISIBLE);
                ll_snacks.setVisibility(View.VISIBLE);
                scaleInfoRoot.setVisibility(View.GONE);
                rl_no_goods.setVisibility(View.GONE);
                break;
            case GoodsManagerFragment.Goods_3 | GoodsManagerFragment.Goods_4:
                ll_snacks.setVisibility(View.GONE);
                ll_drinks.setVisibility(View.GONE);
                ll_fruits.setVisibility(View.VISIBLE);
                ll_vegetables.setVisibility(View.VISIBLE);
                scaleInfoRoot.setVisibility(View.VISIBLE);
                rl_no_goods.setVisibility(View.GONE);
                break;
            case GoodsManagerFragment.Goods_1 | GoodsManagerFragment.Goods_2 + GoodsManagerFragment.Goods_3 | GoodsManagerFragment.Goods_4:
                ll_drinks.setVisibility(View.VISIBLE);
                ll_snacks.setVisibility(View.VISIBLE);
                ll_fruits.setVisibility(View.VISIBLE);
                rl_no_goods.setVisibility(View.GONE);
                ll_vegetables.setVisibility(View.VISIBLE);
                scaleInfoRoot.setVisibility(View.VISIBLE);

                break;
        }
        ll_others.setVisibility(mOthers.isEmpty() ? View.GONE : View.VISIBLE);
        othersAdapter.notifyDataSetChanged();
        drinkAdapter.notifyDataSetChanged();
        fruitAdapter.notifyDataSetChanged();
        snackAdapter.notifyDataSetChanged();
        vegetableAdapter.notifyDataSetChanged();

        int payMode = (int) SharePreferenceUtil.getParam(this, PayDialog.PAY_MODE_KEY, 7);
        switch (payMode) {
            case PayDialog.PAY_FACE:
                tv_face_pay.setVisibility(View.VISIBLE);
                break;
            case PayDialog.PAY_FACE | PayDialog.PAY_CODE | PayDialog.PAY_CASH:
                tv_face_pay.setVisibility(View.GONE);
                break;
        }
        boolean vip = (boolean) SharePreferenceUtil.getParam(this, PayModeSettingFragment.VIP_PAY_KEY, false);
        tvVipPay.setVisibility(vip ? View.VISIBLE : View.GONE);
        tvVipK1Pay.setVisibility(vip ? View.VISIBLE : View.GONE);
    }


    private void initView() {
        TextView view = findViewById(R.id.app_name);
        view.setVisibility((!isK1 && isVertical) ? View.INVISIBLE : View.VISIBLE);
        lvMenus = (ListView) findViewById(R.id.lv_menus);
        tvPrice = (TextView) findViewById(R.id.main_tv_price);
        btnClear = (TextView) findViewById(R.id.main_btn_clear);
        llyShopcar = (LinearLayout) findViewById(R.id.lly_shopcar);
        rtlEmptyShopcar = (RelativeLayout) findViewById(R.id.rtl_empty_shopcar);
        flShoppingCar = (FrameLayout) findViewById(R.id.fl_shopping_car);
        tv_face_pay = findViewById(R.id.tv_face_pay);
        main_ll_pay = findViewById(R.id.main_ll_pay);


        btnMore = (Button) findViewById(R.id.main_btn_more);

        reDrink = findViewById(R.id.gv_drinks);
        reFruit = findViewById(R.id.gv_fruits);
        re_snacks = findViewById(R.id.gv_snacks);
        re_vegetables = findViewById(R.id.gv_vegetables);
        re_others = findViewById(R.id.gv_others);


        ll_drinks = findViewById(R.id.ll_drinks);
        ll_snacks = findViewById(R.id.ll_snacks);
        ll_fruits = findViewById(R.id.ll_fruits);
        ll_vegetables = findViewById(R.id.ll_vegetables);
        ll_others = findViewById(R.id.ll_others);
        rl_no_goods = findViewById(R.id.rl_no_goods);

        bottomSheetLayout = findViewById(R.id.bottomSheetLayout);
        btnPay = findViewById(R.id.main_k1_btn_pay);
        tvCarMoeny = findViewById(R.id.tv_car_money);
        tvCar = findViewById(R.id.tv_car_num);
        ivCar = findViewById(R.id.iv_car);
        rlCar = findViewById(R.id.main_btn_car);
        llK1ShoppingCar = (LinearLayout) findViewById(R.id.ll_k1_shopping_car);

        tvVipPay = findViewById(R.id.vip_pay);
        tvVipK1Pay = findViewById(R.id.vip_k1__pay);
        if (isVertical) {
            llK1ShoppingCar.setVisibility(View.VISIBLE);
            flShoppingCar.setVisibility(View.GONE);
        } else {
            llK1ShoppingCar.setVisibility(View.GONE);
            flShoppingCar.setVisibility(View.VISIBLE);
            llyShopcar.setVisibility(View.GONE);
            rtlEmptyShopcar.setVisibility(View.VISIBLE);
        }


        scaleInfoRoot = (LinearLayout) findViewById(R.id.scale_info_root);
        ivScaleStable = (ImageView) findViewById(R.id.iv_scale_stable);
        ivScaleNet = (ImageView) findViewById(R.id.iv_scale_net);
        ivScaleZero = (ImageView) findViewById(R.id.iv_scale_zero);
        tvScaleNet = (TextView) findViewById(R.id.tv_scale_net);
        tvScalePnet = (TextView) findViewById(R.id.tv_scale_pnet);
        ivScaleIcon = (ImageView) findViewById(R.id.iv_scale_icon);
        tvScalePrice = (TextView) findViewById(R.id.tv_scale_price);
        tvScaleTotal = (TextView) findViewById(R.id.tv_scale_total);
        ivScaleIcon.setClipToOutline(true);
        ivScaleIcon.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 15);
            }
        });
        tvScaleNetDescribe = (TextView) findViewById(R.id.tv_scale_net_describe);
        llScalePnet = (LinearLayout) findViewById(R.id.ll_scale_pnet);

        ivScaleOverMax = (ImageView) findViewById(R.id.iv_scale_over_max);
        tvScaleOverMax = (TextView) findViewById(R.id.tv_scale_over_max);

        btnScaleZero = (TextView) findViewById(R.id.btn_scale_zero);
        btnScaleTare = (TextView) findViewById(R.id.btn_scale_tare);
        btnScaleNumbTare = (TextView) findViewById(R.id.btn_scale_numb_tare);
        btnScaleClearTare = (TextView) findViewById(R.id.btn_scale_clear_tare);


        flUnlockUser = (FrameLayout) findViewById(R.id.fl_unlock_user);
        tv_user_lock = (TextView) findViewById(R.id.tv_user_lock);
        ivUserHeadIcon = (CircleImageView) findViewById(R.id.iv_user_head_icon);
        this.ivUserHeadIcon.setImageResource(UnlockActivity.SHOP_ICON);
        this.tv_user_lock.setText(Utils.getPmOrAm() + UnlockActivity.SHOPNAME);
        this.myHandler.postDelayed(new Runnable() {
            public void run() {
                MainActivity.this.welcomeUserAnim();
            }
        }, 1000L);
    }


    private void initAction() {
        scalePresenter = new ScalePresenter(this, new ScalePresenter.ScalePresenterCallback() {
            @Override
            public void getData(final int net, final int pnet, final int statu) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateScaleInfo(net, pnet, statu);
                    }
                });
            }

            @Override
            public void isScaleCanUse(boolean isCan) {
                if (!isCan) {
                    tvScaleNet.setText("---");
                    tvScaleNet.setTextColor(Color.RED);
                    tvScalePnet.setText("---");
                    tvScalePnet.setTextColor(Color.RED);

                }
            }
        });

        othersAdapter.setOnItemClickListener(new GoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                MenusBean bean = new MenusBean();
                bean.setId("" + (menus.size() + 1));
                bean.setMoney(mOthers.get(position).getPrice());
                bean.setName(mOthers.get(position).getName());
                bean.setType(0);
                bean.setCode(mOthers.get(position).getCode());
                menus.add(bean);
                float price = 0.00f;
                for (MenusBean bean1 : menus) {
                    price = price + Float.parseFloat(bean1.getMoney().substring(1));
                }
                tvPrice.setText(ResourcesUtils.getString(MainActivity.this, R.string.units_money_units) + decimalFormat.format(price));
                menusAdapter.update(menus);
                Log.e("@@@@", "code==" + mOthers.get(position).getCode());
                buildMenuJson(menus, decimalFormat.format(price));
            }

        });
        drinkAdapter.setOnItemClickListener(new GoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                MenusBean bean = new MenusBean();
                bean.setId("" + (menus.size() + 1));
                bean.setMoney(mDrinksBean.get(position).getPrice());
                bean.setName(mDrinksBean.get(position).getName());
                bean.setType(0);
                bean.setCode(mDrinksBean.get(position).getCode());
                menus.add(bean);
                float price = 0.00f;
                for (MenusBean bean1 : menus) {
                    price = price + Float.parseFloat(bean1.getMoney().substring(1));
                }
                tvPrice.setText(ResourcesUtils.getString(MainActivity.this, R.string.units_money_units) + decimalFormat.format(price));
                menusAdapter.update(menus);
                Log.e("@@@@", "code==" + mDrinksBean.get(position).getCode());
                buildMenuJson(menus, decimalFormat.format(price));
            }

        });
        snackAdapter.setOnItemClickListener(new GoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                MenusBean bean = new MenusBean();
                bean.setId("" + (menus.size() + 1));
                bean.setMoney(mSnacksBean.get(position).getPrice());
                bean.setName(mSnacksBean.get(position).getName());
                bean.setType(0);
                bean.setCode(mSnacksBean.get(position).getCode());
                menus.add(bean);
                float price = 0.00f;
                for (MenusBean bean1 : menus) {
                    price = price + Float.parseFloat(bean1.getMoney().substring(1));
                }
                tvPrice.setText(ResourcesUtils.getString(MainActivity.this, R.string.units_money_units) + decimalFormat.format(price));
                menusAdapter.update(menus);

                buildMenuJson(menus, decimalFormat.format(price));
            }

        });
        vegetableAdapter.setOnItemClickListener(new GoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                checkScaleGoods(position, 0);
            }

            @Override
            public void onItemCarClick(View view, int position) {
                super.onItemCarClick(view, position);
                addGoodsByScale(scalePresenter.formatTotalMoney(), scalePresenter.getGvBeans());
            }
        });
        fruitAdapter.setOnItemClickListener(new GoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                checkScaleGoods(position, 1);
            }

            @Override
            public void onItemCarClick(View view, int position) {
                super.onItemCarClick(view, position);
                addGoodsByScale(scalePresenter.formatTotalMoney(), scalePresenter.getGvBeans());
            }
        });




        btnClear.setOnClickListener(this);
        btnMore.setOnClickListener(this);
        main_ll_pay.setOnClickListener(this);
        flUnlockUser.setOnClickListener(this);

        btnPay.setOnClickListener(this);
        rlCar.setOnClickListener(this);

        tvVipPay.setOnClickListener(this);
        tvVipK1Pay.setOnClickListener(this);

        btnScaleZero.setOnClickListener(this);
        btnScaleTare.setOnClickListener(this);
        btnScaleNumbTare.setOnClickListener(this);
        btnScaleClearTare.setOnClickListener(this);
    }


    private void initData() {
        screenManager.init(this);
        Display[] displays = screenManager.getDisplays();
        Log.e(TAG, "屏幕数量" + displays.length);
        for (int i = 0; i < displays.length; i++) {
            Log.e(TAG, "屏幕" + displays[i]);
        }
        Display display = screenManager.getPresentationDisplays();

        //副屏
        if(display != null) {
            isSecondIxist = true;
            DisplayMetrics dm = new DisplayMetrics();
            display.getMetrics(dm);
            int width = dm.widthPixels;// 屏幕宽度
            int height = dm.heightPixels;// 屏幕宽度
            isSecondVertical = height > width;
        }
//        if (display != null && isVertical) {
        if (isSecondIxist && !isSecondVertical) {
            videoDisplay = new VideoDisplay(this, display, Environment.getExternalStorageDirectory().getPath() + "/video_03.mp4");
            videoMenuDisplay = new VideoMenuDisplay(this, display, Environment.getExternalStorageDirectory().getPath() + "/video_02.mp4");
            textDisplay = new TextDisplay(this, display);

            videoDisplay.setClickLinster(new VideoDisplay.OnMyClickLinster() {
                @Override
                public void onClick() {
                    if (videoMenuDisplay != null && !videoMenuDisplay.isShow)
                        videoMenuDisplay.show();
                }
            });
            videoMenuDisplay.setOnUpdateMenusListener(new VideoMenuDisplay.OnUpdateMenus() {
                @Override
                public void onMenusAdd(MenusBean bean) {
                    menus.add(bean);
                    float price = 0.00f;
                    for (MenusBean bean1 : menus) {
                        price = price + Float.parseFloat(bean1.getMoney().substring(1));
                    }
                    tvPrice.setText(ResourcesUtils.getString(MainActivity.this, R.string.units_money_units) + decimalFormat.format(price));
                    menusAdapter.update(menus);
                    buildMenuJson(menus, decimalFormat.format(price));
                }
                @Override
                public void onMenusClear(){
                    payCompleteToReMenu();
                }
            });
        }

        mDrinksBean = GoodsCode.getInstance().getDrinks();
        mFruitsBean = GoodsCode.getInstance().getFruits();
        mSnacksBean = GoodsCode.getInstance().getSnacks();
        mVegetablesBean = GoodsCode.getInstance().getVegetables();
        mOthers = GoodsCode.getInstance().getOthers();

        drinkAdapter = new GoodsAdapter(mDrinksBean, 1);
        reDrink.setLayoutManager(new GridLayoutManager(this, (isVertical && !isK1) ? 2 : 4));
        reDrink.setAdapter(drinkAdapter);


        fruitAdapter = new GoodsAdapter(mFruitsBean, 2);
        reFruit.setLayoutManager(new GridLayoutManager(this, (isVertical && !isK1) ? 2 : 4));
        reFruit.setAdapter(fruitAdapter);

        snackAdapter = new GoodsAdapter(mSnacksBean, 3);
        re_snacks.setLayoutManager(new GridLayoutManager(this, (isVertical && !isK1) ? 2 : 4));
        re_snacks.setAdapter(snackAdapter);


        vegetableAdapter = new GoodsAdapter(mVegetablesBean, 2);
        re_vegetables.setLayoutManager(new GridLayoutManager(this, (isVertical && !isK1) ? 2 : 4));
        re_vegetables.setAdapter(vegetableAdapter);

        othersAdapter = new GoodsAdapter(mOthers, 0);
        re_others.setLayoutManager(new GridLayoutManager(this, (isVertical && !isK1) ? 2 : 4));
        re_others.setAdapter(othersAdapter);

        menus.clear();
        tvPrice.setText(ResourcesUtils.getString(this, R.string.units_money_units) + "0.00");

        menusAdapter = new MenusAdapter(this, menus);
        lvMenus.setAdapter(menusAdapter);

        payDialog = new PayDialog();
        alipaySmileModel = new AlipaySmileModel();
        alipaySmilePresenter = new AlipaySmilePresenter(this, alipaySmileModel);
        payMentPayPresenter = new PayMentPayPresenter(this);
        unionPayPreseter = new UnionPayPreseter(this);
        p1POSPresenter = P1POSPresenter.getInstance(this);

        payDialog.setAlipaySmilePresenter(alipaySmilePresenter);
        payDialog.setPayMentPayPresenter(payMentPayPresenter);
        payDialog.setUnionPayPreseter(unionPayPreseter);
        payDialog.setP1POSPresenter(p1POSPresenter);

        payDialog.setCompleteListener(new PayDialog.OnCompleteListener() {
            @Override
            public void onCancel() {
                if (menus.size() > 0) {
                    if (videoMenuDisplay != null) {
                        videoMenuDisplay.show();
                    }

                }
            }

            @Override
            public void onSuccess(final int payMode) {
                playSound(payMode);
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        paySuccessToPrinter(payMode);
                        payDialog.setPhoneNumber("");

                    }
                }, 1000);

            }

            @Override
            public void onComplete(int payMode) {
                payCompleteToReMenu();
            }
        });

        soundPool.load(MainActivity.this, R.raw.audio, 1);// 1
        soundPool.load(MainActivity.this, isZh(this) ? R.raw.alipay : R.raw.alipay_en, 1);// 2
    }


    private void payCompleteToReMenu() {
        if(isSecondIxist && !isSecondVertical){
//        if (!isVertical) {
            llyShopcar.setVisibility(View.GONE);
            rtlEmptyShopcar.setVisibility(View.VISIBLE);
            tvPrice.setText(ResourcesUtils.getString(MainActivity.this, R.string.units_money_units) + "0.00");
            menus.clear();
            menusAdapter.update(menus);
            buildMenuJson(menus, "0.00");

            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    standByTime();

                }
            }, 1000);
//            standByTime();

        }
//        else {
        if (isVertical) {
            menus.clear();
            tvCarMoeny.setText("");
            tvCar.setText("");
            tvCar.setVisibility(View.GONE);
            ivCar.setImageResource(R.drawable.car_gray);
            bottomSheetLayout.dismissSheet();
            btnPay.setBackgroundColor(Color.parseColor("#999999"));
        }
    }


    private void paySuccessToPrinter(int payMode) {
        if (isK1) {
            if (kPrinterPresenter != null) {
                kPrinterPresenter.print(goods_data, payMode);
            }
        }
//        else {
        if(isSecondIxist && !isSecondVertical){
            if (printerPresenter != null) {
                printerPresenter.print(goods_data, payMode);
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_unlock_user:
                this.showDomainLock();
                return;
            case R.id.main_btn_clear:
                if (isVertical) {
                    menus.clear();
                    tvCarMoeny.setText("");
                    tvCar.setText("");
                    tvCar.setVisibility(View.GONE);
                    ivCar.setImageResource(R.drawable.car_gray);
                    bottomSheetLayout.dismissSheet();
                    btnPay.setBackgroundColor(Color.parseColor("#999999"));
                }
//                else {        //changed by mayflower on 191022,给K2 MINI的副屏
                if(isSecondIxist && !isSecondVertical){
                    llyShopcar.setVisibility(View.GONE);
                    rtlEmptyShopcar.setVisibility(View.VISIBLE);
                    menus.clear();
                    tvPrice.setText(ResourcesUtils.getString(this, R.string.units_money_units) + "0.00");
                    menusAdapter.update(menus);           //主屏购物车清空
                    buildMenuJson(menus, "0.00");   //副屏购物车清空

                    if (videoDisplay != null) {

                        videoDisplay.show();
                    }
                }
                break;
            case R.id.main_btn_more:
                Intent intent = new Intent(MainActivity.this, MoreActivity.class);
                startActivity(intent);
                break;
            case R.id.vip_k1__pay:
                if (menus.size() > 0) {
                    vipPay();
                }
                break;
            case R.id.vip_pay:
                vipPay();
                break;
            case R.id.main_ll_pay:
                Bundle bundle = new Bundle();
                bundle.putString("MONEY", tvPrice.getText().toString());
                bundle.putString("GOODS", goods_data);

                int payMode = (int) SharePreferenceUtil.getParam(this, PayDialog.PAY_MODE_KEY, 6);

                bundle.putInt("PAYMODE", payMode);
                payDialog.setArguments(bundle);
                payDialog.show(getSupportFragmentManager(), "payDialog");

                break;

            case R.id.main_btn_car:
                if (menus.size() > 0) {
                    if (!bottomSheetLayout.isSheetShowing()) {
                        bottomSheetLayout.showWithSheetView(createBottomSheetView());
                    } else {
                        bottomSheetLayout.dismissSheet();
                    }
                }
                break;
            case R.id.main_k1_btn_pay:
                if (menus.size() > 0) {
//                    if (scannerPresenter.isOpened())
//                        scannerPresenter.setKeyDown();

                    if (!isK1) {
                        payDialog.completeListener.onSuccess(PayDialog.PAY_MODE_0);
                        payDialog.completeListener.onComplete(PayDialog.PAY_MODE_0);
                        return;
                    }
                    if (bottomSheetLayout.isSheetShowing()) {
                        bottomSheetLayout.dismissSheet();
                    }
                    int payModes = (int) SharePreferenceUtil.getParam(this, PayDialog.PAY_MODE_KEY, 6);
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("MONEY", tvCarMoeny.getText().toString());
                    bundle1.putString("GOODS", goods_data);
                    bundle1.putInt("PAYMODE", payModes);

                    payDialog.setArguments(bundle1);
                    payDialog.show(getSupportFragmentManager(), "payDialog");
                }
                break;

            case R.id.tv_domain_lock:
                if ((Boolean) SharePreferenceUtil.getParam(this, "BLE_KEY", false)) {
                    this.mProxy.startLockDomain();
                }

                this.popWindow.dissmiss();

            case R.id.btn_scale_zero:
                scalePresenter.zero();
                break;
            case R.id.btn_scale_tare:
                scalePresenter.tare();
                break;
            case R.id.btn_scale_numb_tare:
                if (scalePresenter.getPnet() > 0) {
                    showToast(R.string.scale_tips_havenumpent);
                } else {
                    showPresetTare();
                }
                break;
            case R.id.btn_scale_clear_tare:
                scalePresenter.clearTare();
                break;
            default:
                break;
        }
    }

    VipPayDialog vipPayDialog;

    private void vipPay() {
        vipPayDialog = new VipPayDialog(this).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_cancel:
                        payDialog.setPhoneNumber("");
                        break;
                    case R.id.tv_confirm:
                        payDialog.setPhoneNumber(vipPayDialog.getNum());
                        main_ll_pay.performClick();
                        break;
                }
                vipPayDialog.dismiss();
            }
        });

        vipPayDialog.show();
    }

    public void updateMenus(List<MenusBean> menus, String price){
    }

    private void buildMenuJson(List<MenusBean> menus, String price) {
        try {
            JSONObject data = new JSONObject();
            data.put("title", "Sunmi " + ResourcesUtils.getString(this, R.string.menus_title));
            JSONObject head = new JSONObject();
            head.put("param1", ResourcesUtils.getString(this, R.string.menus_number));
            head.put("param2", ResourcesUtils.getString(this, R.string.menus_goods_name));
            head.put("param3", ResourcesUtils.getString(this, R.string.menus_unit_price));
            data.put("head", head);
            data.put("flag", "true");
            JSONArray list = new JSONArray();
            for (int i = 0; i < menus.size(); i++) {
                JSONObject listItem = new JSONObject();
                listItem.put("param1", "" + (i + 1));
                listItem.put("param2", menus.get(i).getName());
                listItem.put("param3", menus.get(i).getMoney());
                listItem.put("type", menus.get(i).getType());
                listItem.put("code", menus.get(i).getCode());
                listItem.put("net", menus.get(i).getNet());
                list.put(listItem);
            }
            data.put("list", list);
            JSONArray KVPList = new JSONArray();
            JSONObject KVPListOne = new JSONObject();
            KVPListOne.put("name", ResourcesUtils.getString(this, R.string.shop_car_total) + " ");
            KVPListOne.put("value", price);
            JSONObject KVPListTwo = new JSONObject();
            KVPListTwo.put("name", ResourcesUtils.getString(this, R.string.shop_car_offer) + " ");
            KVPListTwo.put("value", "0.00");
            JSONObject KVPListThree = new JSONObject();
            KVPListThree.put("name", ResourcesUtils.getString(this, R.string.shop_car_number) + " ");
            KVPListThree.put("value", "" + menus.size());
            JSONObject KVPListFour = new JSONObject();
            KVPListFour.put("name", ResourcesUtils.getString(this, R.string.shop_car_receivable) + " ");
            KVPListFour.put("value", price);
            KVPList.put(0, KVPListOne);
            KVPList.put(1, KVPListTwo);
            KVPList.put(2, KVPListThree);
            KVPList.put(3, KVPListFour);
            data.put("KVPList", KVPList);
            Log.d("HHHH", "onClick: ---------->" + data.toString());
            goods_data = data.toString();
            Log.d(TAG, "buildMenuJson: ------->" + (videoMenuDisplay != null));
//            if (payDialog.isVisible()) {
            if (payDialog.isVisible() && menus.size()>0) {  //结账完成时.副屏要清空购物车,此时menus.size()=0
                return;
            }
            if (videoMenuDisplay != null && !videoMenuDisplay.isShow) {
                videoMenuDisplay.show();
                videoMenuDisplay.update(menus, data.toString());
            } else if (null != videoMenuDisplay) {
                videoMenuDisplay.update(menus, data.toString());
            }
            // 购物车有东西

            if (isVertical) {
                tvCarMoeny.setText(ResourcesUtils.getString(R.string.units_money_units) + price);
                tvCar.setText(menus.size() + "");
                tvCar.setVisibility(View.VISIBLE);
                ivCar.setImageResource(R.drawable.car_white);
                btnPay.setBackgroundColor(Color.parseColor("#FC5436"));
                if (bottomSheetLayout.isSheetShowing()) {
                    menusAdapter.notifyDataSetChanged();
                    lvMenus.setSelection(menusAdapter.getCount() - 1);
                    TextView tvPrice = bottomSheetLayout.findViewById(R.id.main_tv_price);
                    tvPrice.setText(tvCarMoeny.getText().toString());
                }
            } else {
                llyShopcar.setVisibility(View.VISIBLE);
                rtlEmptyShopcar.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private StringBuilder sb = new StringBuilder();
    private Handler myHandler = new Handler();

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
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
//                Log.i("扫码键盘数据","--"+sb.toString() +"--"+ByteUtils.str2HexString(sb.toString()));

                final int len = sb.length();
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (len != sb.length()) return;
                        Log.e(TAG, "isQRcode");
                        if (sb.length() > 0) {
                            if (payDialog.isVisible()) {
                                Log.e(TAG, "支付中");
                            } else {
                                Log.i(TAG,"扫码键盘数据:"+sb.toString() +"["+ByteUtils.str2HexString(sb.toString())+"]len:" + sb.toString().length());
                                addDrink(sb.toString());
                            }
                            sb.setLength(0);
                        }
                    }
                }, 200);
                return true;
            default:
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    private void addDrink(String code) {
        code = code.replaceAll("[^0-9a-zA-Z]", "");
        Log.e(TAG, "扫码===" + code + "   " + GoodsCode.getInstance().getGood().containsKey(code));
        if (GoodsCode.getInstance().getGood().containsKey(code)) {
            GvBeans gvBeans = GoodsCode.getInstance().getGood().get(code);
            MenusBean bean = new MenusBean();
            bean.setId("" + (menus.size() + 1));
            bean.setMoney(gvBeans.getPrice());
            bean.setName(gvBeans.getName());
            menus.add(bean);
        } else {
            return;
        }

        float price = 0.00f;
        for (MenusBean bean1 : menus) {
            price = price + Float.parseFloat(bean1.getMoney().substring(1));
        }
        tvPrice.setText(ResourcesUtils.getString(this, R.string.units_money_units) + decimalFormat.format(price));
        menusAdapter.update(menus);

        buildMenuJson(menus, decimalFormat.format(price));

        if (isVertical) {
            if (menus.size() > 0 && !bottomSheetLayout.isSheetShowing()) {
                if (!bottomSheetLayout.isSheetShowing()) {
                    bottomSheetLayout.showWithSheetView(createBottomSheetView());
                }
            }
        }
    }

    private void checkScaleGoods(int position, int type) {
        GvBeans gvBeans = null;
        switch (type) {
            case 0:
                gvBeans = mVegetablesBean.get(position);
                scalePresenter.setGvBeans(mVegetablesBean.get(position));
                fruitAdapter.setSelectPosition(-1);
                vegetableAdapter.setSelectPosition(position);
                break;
            case 1:
                gvBeans = mFruitsBean.get(position);
                scalePresenter.setGvBeans(mFruitsBean.get(position));
                fruitAdapter.setSelectPosition(position);
                vegetableAdapter.setSelectPosition(-1);
                break;
        }

        ivScaleIcon.setImageResource(gvBeans.getLogo());
        tvScalePrice.setText(scalePresenter.getPrice() + "");

        vegetableAdapter.notifyDataSetChanged();
        fruitAdapter.notifyDataSetChanged();
    }

    private void addGoodsByScale(String total, GvBeans gvBeans) {
        if (!scalePresenter.isStable() || scalePresenter.net <= 0) {
            return;
        }
        MenusBean bean = new MenusBean();
        bean.setId("" + (menus.size() + 1));
        bean.setMoney(ResourcesUtils.getString(MainActivity.this, R.string.units_money_units) + total);
        bean.setName(gvBeans.getName());
        bean.setType(1);
        bean.setCode(gvBeans.getCode());
        bean.setNet(scalePresenter.net);
        menus.add(bean);
        float price = 0.00f;
        for (MenusBean bean1 : menus) {
            price = price + Float.parseFloat(bean1.getMoney().substring(1));
        }
        tvPrice.setText(ResourcesUtils.getString(MainActivity.this, R.string.units_money_units) + decimalFormat.format(price));
        menusAdapter.update(menus);

        buildMenuJson(menus, decimalFormat.format(price));
    }

    /**
     * 更新秤显示信息
     *
     * @param net
     * @param pnet
     * @param statu
     */
    private void updateScaleInfo(final int net, int pnet, int statu) {
        if (pnet == 0) {
            tvScaleNetDescribe.setText(R.string.scale_net_nopnet);
        } else {
            tvScaleNetDescribe.setText(R.string.scale_net_kg);
        }
        tvScaleNet.setText(scalePresenter.formatQuality(net));
        tvScalePnet.setText(scalePresenter.formatQuality(pnet));
        tvScaleTotal.setText(scalePresenter.formatTotalMoney(net));


        ivScaleZero.setActivated(net == 0);
        ivScaleStable.setActivated((statu & 1) == 1);
        ivScaleNet.setActivated(pnet > 0);
        if ((statu & 1) != 1) {
            //重量不稳定
            ivScaleStable.setActivated(false);
            ivScaleNet.setActivated(false);
            ivScaleZero.setActivated(false);
        }
        //超载
        if ((statu & 4) == 4) {
            ivScaleStable.setActivated(false);
            ivScaleNet.setActivated(false);
            ivScaleZero.setActivated(false);
            if (tvScaleOverMax.getVisibility() == View.GONE) {
                tvScaleOverMax.setVisibility(View.VISIBLE);
                tvScaleOverMax.setSelected(true);

                ivScaleOverMax.setVisibility(View.VISIBLE);
                ivScaleOverMax.setSelected(true);

                tvScaleNet.setVisibility(View.GONE);


                tvScaleOverMax.setText(R.string.scale_over_max);
            }
        } else if ((pnet + net) < 0) {
//            欠载
            ivScaleStable.setActivated(false);
            ivScaleNet.setActivated(false);
            ivScaleZero.setActivated(false);
            if (tvScaleOverMax.getVisibility() == View.GONE) {
                tvScaleOverMax.setVisibility(View.VISIBLE);
                tvScaleOverMax.setSelected(false);

                ivScaleOverMax.setVisibility(View.VISIBLE);
                ivScaleOverMax.setSelected(false);

                tvScaleNet.setVisibility(View.GONE);

                tvScaleOverMax.setText(R.string.scale_over_mix);
            }
        } else {
            if (tvScaleOverMax.getVisibility() == View.VISIBLE) {
                tvScaleOverMax.setVisibility(View.GONE);
                ivScaleOverMax.setVisibility(View.GONE);
            }
            if (tvScaleNet.getVisibility() == View.GONE) {
                tvScaleNet.setVisibility(View.VISIBLE);
            }
        }


    }

    private void showPresetTare() {

        mInputDialog = new Input2Dialog.Builder(this)
                .setTitle(ResourcesUtils.getString(R.string.more_num_peele) + "/kg")
                .setLeftText(ResourcesUtils.getString(R.string.cancel))
                .setRightText(ResourcesUtils.getString(R.string.confrim))
                .setHint("0.00kg")
                .setCallBack(new InputDialog.DialogOnClickCallback() {
                    @Override
                    public void onSure(String text) {
                        if (!TextUtils.isEmpty(text)) {
                            float pnet = Float.parseFloat(text);
                            scalePresenter.setNumPnet((int) (pnet * 1000.0f));
                        }
                        mInputDialog.dismiss();
                    }

                    @Override
                    public void onCancel() {
                        mInputDialog.dismiss();
                    }
                })
                .build();
        mInputDialog.show();
    }

    //待机
    private void standByTime() {
        if (videoDisplay != null && !videoDisplay.isShow) {
            videoDisplay.show();
        }
    }


    private void playSound(final int payMode) {
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                soundPool.play(1, 1, 1, 10, 0, 1);
                if (payMode == 2) {
                    soundPool.play(2, 1, 1, 10, 0, 1);
                }
            }
        }, 200);
    }

    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

    //add by mayflower on 191204
    private void switchLanguage(int lang){
        Resources resources = getResources();//获得res资源对象
        Configuration config = resources.getConfiguration();//获得设置对象
        DisplayMetrics dm = resources.getDisplayMetrics();//获得屏幕参数：主要是分辨率，像素等

        switch (lang){
            case 0:
                config.locale = Locale.SIMPLIFIED_CHINESE;
                break;
            case 1:
                config.locale = Locale.ENGLISH;
                break;
            case 2:
                break;
        }
        resources.updateConfiguration(config, dm);
    }
    ///////////////////////////////////////////////

    private View createBottomSheetView() {
        View bottomSheet = LayoutInflater.from(this).inflate(R.layout.sheet_layout, bottomSheetLayout, false);
        lvMenus = bottomSheet.findViewById(R.id.lv_menus);
        TextView tvPrice = bottomSheet.findViewById(R.id.main_tv_price);
        TextView btnClear = bottomSheet.findViewById(R.id.main_btn_clear);
        btnClear.setOnClickListener(this);
        menusAdapter = new MenusAdapter(this, menus);
        lvMenus.setAdapter(menusAdapter);
        lvMenus.setSelection(menusAdapter.getCount() - 1);
        tvPrice.setText(tvCarMoeny.getText().toString());
        return bottomSheet;
    }


    @Subscribe(
            threadMode = ThreadMode.MAIN
    )
    public void Event(UpdateUnLockUserEvent var1) {
        this.ivUserHeadIcon.setImageResource(var1.getIcon());
        this.willwelcome = true;
        this.tv_user_lock.setAlpha(0.0F);
        this.tv_user_lock.setText(Utils.getPmOrAm() + var1.getName());
        if (var1.isShowAnim()) {
            this.tv_user_lock.post(new Runnable() {
                public void run() {
                    MainActivity.this.welcomeUserAnim();
                }
            });
        }

    }


    private void welcomeUserAnim() {
        this.tv_user_lock.setAlpha(1.0F);
        int var1 = this.tv_user_lock.getMeasuredWidth() + 17;
        AnimatorSet var2 = new AnimatorSet();
        ObjectAnimator var3 = ObjectAnimator.ofFloat(this.tv_user_lock, "translationX", new float[]{(float) var1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, (float) var1, (float) var1, 0.0F});
        var3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator var1) {
                if (var1.getCurrentPlayTime() > 4200L && MainActivity.this.tv_user_lock.getText().toString().contains(Utils.getPmOrAm())) {
                    MainActivity.this.tv_user_lock.setText(MainActivity.this.tv_user_lock.getText().toString().replace(Utils.getPmOrAm(), ""));
                }

            }
        });
        var2.setDuration(5000L);
        var2.setInterpolator(new LinearInterpolator());
        var2.play(var3);
        var2.start();
    }

    private void initBleService() {
        boolean isOpen = (Boolean) SharePreferenceUtil.getParam(this, "BLE_KEY", false);
        if (isOpen && mProxy == null) {
            this.bindService(new Intent(this, UnlockServer.class), this.mServiceConnection, Service.BIND_AUTO_CREATE);
        }
        if (mProxy != null) {
            if (isOpen) {
                mProxy.updateAllUser();
            } else {
                mProxy.close();
            }
        }
    }

    private void showDomainLock() {
        if (this.popWindow == null) {
            View var1 = LayoutInflater.from(this).inflate(R.layout.pop_lock, (ViewGroup) null);
            var1.setOnClickListener(this);
            this.popWindow = (new CustomPopWindow.PopupWindowBuilder(this)).setView(var1).create();
        }

        this.popWindow.showAsDropDown(this.ivUserHeadIcon, -133, 15);
    }


    @Override
    protected void onDestroy() {
        soundPool.release();

        //add by mayflower--------------
        p1POSPresenter.closePOSReceiver();

        kCodeScannerPresenter.stop();
        kCodeScannerPresenter = null;

        if(mIdCardService != null)
            unbindCardService();
        kCardReaderPresenter = null;

        if (mCardSenderService != null)
            disconnectCardSenderService();
        hCardSenderPresenter = null;
        //------------------------------

        if (extPrinterService != null) {
            unbindService(connService);
        }
        if (woyouService != null) {
            try {
                InnerPrinterManager.getInstance().unBindService(this,
                        innerPrinterCallback);
            } catch (InnerPrinterException e) {
                e.printStackTrace();
            }
        }
        if (mProxy != null) {
            unbindService(mServiceConnection);
        }

        if (scalePresenter != null && scalePresenter.isScaleSuccess()) {
            scalePresenter.onDestroy();
        }

        if (payMentPayPresenter != null) {
            payMentPayPresenter.destoryReceiver();
        }

        if (alipaySmilePresenter != null) {
            alipaySmilePresenter.destory();
        }
        if (unionPayPreseter != null) {
            unionPayPreseter.unBindService();
        }
        printerPresenter = null;
        kPrinterPresenter = null;
        EventBus.getDefault().unregister(this);
        BasePresentationHelper.getInstance().dismissAll();
        super.onDestroy();
    }

    //退出时的时间
    private long mExitTime;

    //对返回键进行监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(MainActivity.this, ResourcesUtils.getString(this, R.string.tips_exit), Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName var1, IBinder var2) {
            MainActivity.this.mProxy = (UnlockServer.Proxy) var2;
        }

        public void onServiceDisconnected(ComponentName var1) {
        }
    };


    //add by mayflower,给无服务的串口扫码器
    private String mBuffer = null;      //扫码数据
    private Handler handler = new Handler();

    public void initSerialScanner(){
        codeScannerPresenter = new CodeScannerPresenter(MainActivity.this);
        if(!codeScannerPresenter.isOpened()) { return; }

//        scannerPresenter.setMode(0,60000,0);
        codeScannerPresenter.setMode(1,1000,1000);

        codeScannerPresenter.setOnDataReceiveListener(new CodeScannerPresenter.OnDataReceiveListener(){
            @Override
            public void onDataReceive(byte[] buffer, int size){
                mBuffer = ByteUtils.bytes2Str(buffer,0,size);
                Log.d(TAG,"进入数据监听事件中。。。" + mBuffer);

                handler.post(runnable);
            }
            //开线程更新UI
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG,"serial:" + mBuffer);
                    if(mBuffer.length() > 0){
                        if(payDialog.isVisible()){
                            Log.e(TAG, "支付中");
                            payDialog.payByCode(mBuffer);
                        }else{
                            addDrink(mBuffer);
                        }
                    }
                }
            };
        });
    }
}
