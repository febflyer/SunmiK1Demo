package com.sunmi.sunmik1demo.present;

import android.app.Presentation;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.TextAppearanceSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunmi.sunmik1demo.BasePresentation;
import com.sunmi.sunmik1demo.R;
import com.sunmi.sunmik1demo.adapter.GoodsAdapter;
import com.sunmi.sunmik1demo.adapter.MenusAdapter;
import com.sunmi.sunmik1demo.adapter.PresentMenusAdapter;
import com.sunmi.sunmik1demo.bean.GoodsCode;
import com.sunmi.sunmik1demo.bean.GvBeans;
import com.sunmi.sunmik1demo.bean.MenusBean;
import com.sunmi.sunmik1demo.player.IMPlayListener;
import com.sunmi.sunmik1demo.player.IMPlayer;
import com.sunmi.sunmik1demo.player.MPlayer;
import com.sunmi.sunmik1demo.player.MPlayerException;
import com.sunmi.sunmik1demo.player.MinimalDisplay;
import com.sunmi.sunmik1demo.ui.MainActivity;
import com.sunmi.sunmik1demo.utils.ResourcesUtils;
import com.sunmi.sunmik1demo.utils.ScreenManager;
import com.sunmi.sunmik1demo.view.CustomCarGoodsCounterView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by highsixty on 2018/3/7.
 * mail  gaolulin@sunmi.com
 */

public class VideoMenuDisplay extends BasePresentation {

    private SurfaceView mPlayerView;
    private MPlayer player;
    private final String TAG = "SUNMI";
    private String path;
    private LinearLayout container;

    private LinearLayout llyRight;
    private TextView mTitle;
    private ListView mLv;
    private PresentMenusAdapter menusAdapter;
    private List<MenusBean> menus = new ArrayList<>();


    private TextView tvMenusLeft1;
    private TextView tvMenusRight1;
    private TextView tvMenusLeft2;
    private TextView tvMenusRight2;

    private ImageView ivAddGoods;
    private LinearLayout llyAddGoods;
    private ImageView ivGoodsIcon;
    private int resIconID;
    private EditText etGoodsID;
    private EditText etGoodsName;
    private EditText etGoodsPrice;
    private ImageView ivSubNum;
    private EditText etGoodsNum;
    private ImageView ivAddNum;
    private Button btnAdd;
    private Button btnCancel;
    private TextView tvSecClear;

    //新需求，给副屏显示非标商品，功能同主屏
    private LinearLayout ll_non_standar;
    private RecyclerView re_non_standar;

    List<GvBeans> mNonStandarBean;
    GoodsAdapter nonStandarAdapter;


    public VideoMenuDisplay(Context context, Display display, String path) {
        super(context, display);
        this.path = path;
        Log.d(TAG, "VideoDisplay: ------------>" + path);
        File file = new File(path);
        Log.d(TAG, "VideoDisplay: --------->" + file.exists());
    }

    public void update(List<MenusBean> menus, String json) {
        this.menus.clear();
        this.menus.addAll(menus);
        initData(json);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        switchLanguage(1);  //设定语言

        if (ScreenManager.getInstance().isMinScreen()) {
            setContentView(R.layout.vice_video_menu_min_layout);

        } else {
            setContentView(R.layout.vice_video_menu_layout);
        }
        initView();
        initPlayer();
    }

    private void initView() {
        mPlayerView = (SurfaceView) findViewById(R.id.mPlayerView);
        container = (LinearLayout) findViewById(R.id.playerContainer);
        llyRight = (LinearLayout) findViewById(R.id.lly_right);
        mTitle = (TextView) findViewById(R.id.title);
        mLv = (ListView) findViewById(R.id.lv);
        menusAdapter = new PresentMenusAdapter(getContext(), menus);
        mLv.setAdapter(menusAdapter);


        tvMenusLeft1 = (TextView) findViewById(R.id.tv_menus_left1);
        tvMenusRight1 = (TextView) findViewById(R.id.tv_menus_right1);
        tvMenusLeft2 = (TextView) findViewById(R.id.tv_menus_left2);
        tvMenusRight2 = (TextView) findViewById(R.id.tv_menus_right2);

        ivAddGoods = (ImageView) findViewById(R.id.iv_add_goods);
        llyAddGoods = (LinearLayout) findViewById(R.id.ll_add_goods);
        ivGoodsIcon = (ImageView) findViewById(R.id.iv_icon);
        etGoodsID = (EditText) findViewById(R.id.et_goods_id);
        etGoodsName = (EditText) findViewById(R.id.et_goods_name);
        etGoodsPrice = (EditText) findViewById(R.id.et_goods_price);
        ivSubNum = (ImageView) findViewById(R.id.iv_sub_num);
        etGoodsNum = (EditText) findViewById(R.id.et_goods_num);
        ivAddNum = (ImageView) findViewById(R.id.iv_add_num);
        btnAdd = (Button) findViewById(R.id.btn_add);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        tvSecClear = findViewById(R.id.tv_sec_clear);

        ll_non_standar = findViewById(R.id.ll_non_standar);
        re_non_standar = findViewById(R.id.gv_non_standar);

        ivAddGoods.setOnClickListener(this);
        ivSubNum.setOnClickListener(this);
        ivAddNum.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        tvSecClear.setOnClickListener(this);

        ivGoodsIcon.setOnClickListener(this);
        etGoodsID.setOnClickListener(this);
        etGoodsName.setOnClickListener(this);   //这几位，可以点一下随机一个值
        etGoodsPrice.setOnClickListener(this);

        mNonStandarBean = GoodsCode.getInstance().getNonStandar();
        nonStandarAdapter = new GoodsAdapter(mNonStandarBean,4);    //4的单位是"份"
        re_non_standar.setLayoutManager(new GridLayoutManager(getContext(),4));     //每行显示4个
        re_non_standar.setAdapter(nonStandarAdapter);

        nonStandarAdapter.notifyDataSetChanged();   //mNonStandarBean有变化则自动更新
        nonStandarAdapter.setOnItemClickListener(new GoodsAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                MenusBean bean = new MenusBean();
                bean.setId("" + (menus.size() + 1));
                bean.setMoney(mNonStandarBean.get(position).getPrice());
                bean.setName(mNonStandarBean.get(position).getName());
                bean.setType(0);
                bean.setCode(mNonStandarBean.get(position).getCode());
                menus.add(bean);

                onUpdateMenus.onMenusAdd(bean);
            }
        });

        ivGoodsIcon.setImageResource(randIcon());
    }

    private void initData(String jsonStr) {
        Log.d(TAG, "initData: ----------->" + jsonStr);
        try {
            JSONObject json = new JSONObject(jsonStr);
            JSONObject head = json.getJSONObject("head");
            setHeadview(head);
            JSONArray lists = json.getJSONArray("list");
            setlistView(lists);
            JSONArray statement = json.getJSONArray("KVPList");
            setSMView(statement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v){
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_add_goods:
                if (llyAddGoods.getVisibility() == View.GONE) {
                    ivGoodsIcon.setImageResource(randIcon());
                    etGoodsID.setText(randomID());
                    etGoodsName.setText(randomName());
                    etGoodsPrice.setText(randomPrice());
                    etGoodsNum.setText("1");
                    llyAddGoods.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.iv_sub_num:
                int i = Integer.parseInt(etGoodsNum.getText().toString());
                if (i>1)
                    i--;
                etGoodsNum.setText(i+"");
                break;
            case R.id.iv_add_num:
                int j = Integer.parseInt(etGoodsNum.getText().toString());
                if (j<10000)
                    j++;
                etGoodsNum.setText(j+"");
                break;
            case R.id.btn_add:
                String strGoodsID = etGoodsID.getText().toString();
                String strGoodsName = etGoodsName.getText().toString();
                float fGoodsPrice = Float.parseFloat(etGoodsPrice.getText().toString());
                int iGoodsNum  = Integer.parseInt(etGoodsNum.getText().toString());
                int iIcon = resIconID;

                //加入到库存，后面的打印会调库存参数
                if (!GoodsCode.getInstance().getGood().containsKey(strGoodsID))
                    GoodsCode.getInstance().add(strGoodsID, iIcon,
                            strGoodsName, fGoodsPrice, iGoodsNum, getResources().getString(R.string.units_each), GoodsCode.MODE_5);

                //加到购物车
                MenusBean bean = new MenusBean();
                bean.setId("" + (menus.size() + 1));
                bean.setMoney(ResourcesUtils.getString(R.string.units_money_units) + String.valueOf(fGoodsPrice * iGoodsNum));
                bean.setName(strGoodsName);
                bean.setType(0);        //库存
                bean.setCode(strGoodsID);

                onUpdateMenus.onMenusAdd(bean);
                llyAddGoods.setVisibility(View.GONE);
                nonStandarAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_cancel:
                if (llyAddGoods.getVisibility() == View.VISIBLE) {
                    llyAddGoods.setVisibility(View.GONE);
                }
                break;

            case R.id.tv_sec_clear:
                onUpdateMenus.onMenusClear();
                break;

            case R.id.iv_icon:
                //这里用setBackgroundResource的话,就可以通过反射获取图片id了
                ivGoodsIcon.setImageResource(randIcon());
                break;
            case R.id.et_goods_id:
                etGoodsID.setText(randomID());
                break;
            case R.id.et_goods_name:
                etGoodsName.setText(randomName());
                break;
            case R.id.et_goods_price:
                etGoodsPrice.setText(randomPrice());
                break;
        }
    }

    private OnUpdateMenus onUpdateMenus = null;

    public static interface OnUpdateMenus{
        public void onMenusAdd(MenusBean bean);
        public void onMenusClear();
    }

    public void setOnUpdateMenusListener(OnUpdateMenus updateMenus){
        onUpdateMenus = updateMenus;
    }

    private void initPlayer() {
        player = new MPlayer();
        player.setDisplay(new MinimalDisplay(mPlayerView));
        player.setPlayListener(new IMPlayListener() {
            @Override
            public void onStart(IMPlayer player) {

            }

            @Override
            public void onPause(IMPlayer player) {

            }

            @Override
            public void onResume(IMPlayer player) {

            }

            @Override
            public void onComplete(IMPlayer player) {
                try {
                    player.setSource(path, 0);
                } catch (MPlayerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setHeadview(JSONObject json) {
        List<String> valueLists = getValueListByJsonObject(json);
    }

    /**
     * 动态解析jsonObject获取值列表
     *
     * @param json
     * @return
     */
    private ArrayList<String> getValueListByJsonObject(JSONObject json) {
        ArrayList<String> valueLists = new ArrayList<String>();
        try {
            Iterator it = json.keys();
            while (it.hasNext()) {
                String value = json.getString(it.next().toString());
                valueLists.add(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return valueLists;
    }

    /**
     * 设置表内容
     *
     * @param jsonArray
     */
    private void setlistView(JSONArray jsonArray) {
        if (menusAdapter != null) {
            menusAdapter.notifyDataSetChanged();
            mLv.setSelection(menusAdapter.getCount() - 1);
            mLv.smoothScrollToPosition(menusAdapter.getCount() - 1);
        }
    }

    /**
     * 设置结算
     */
    private void setSMView(JSONArray KVPList) {

        for (int i = 0; i < KVPList.length(); i++) {
            try {
                JSONObject json = KVPList.getJSONObject(i);

                switch (i) {
                    case 0:
                        tvMenusRight1.setText(json.getString("name") + ":    " + ResourcesUtils.getString(R.string.units_money_units) + json.getString("value"));
                        break;
                    case 1:
                        break;
                    case 2:
                        tvMenusLeft1.setText(json.getString("name") + ":    " + json.getString("value"));
                        break;
                    case 3:
                        tvMenusLeft2.setText(json.getString("name") + ":    ");
                        SpannableString ss1 = new SpannableString(ResourcesUtils.getString(R.string.units_money_units) + json.getString("value"));
                        ss1.setSpan(new RelativeSizeSpan(0.65f), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE); // set size
                        tvMenusRight2.setText(ss1);

                        break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onSelect(boolean isShow) {
        if(player != null){
            if (isShow) {
                try {
                    player.setSource(path, VideoDisplay.positon);
                    player.onResume();
                } catch (MPlayerException e) {
                    e.printStackTrace();
                    Log.d(TAG, "onCreate: ---------->" + e.getMessage());
                }
            }else {
                if(player.getPosition()!=0)
                    VideoDisplay.positon = player.getPosition();
            }
        }
    }

    @Override
    public void onDisplayRemoved() {
        super.onDisplayRemoved();
//        player.onDestroy();
    }

    private int randomImageID(){
        int[] images = {R.drawable.frame001,2};
        int img = images[(int)(0 + Math.random()*(10-1+1))];
        return img;
    }

    private String randomID(){
        String[] strs = {"00000000", "11111111", "22222222", "33333333", "44444444",
                "55555555", "66666666", "77777777", "88888888", "99999999", };
        String str = strs[(int)(0 + Math.random()*(10-1+1))];
        return str;
    }
    private String randomName(){
        String[] strs = {"老兽的温泉", "不屈的花园", "娜娜奇的秘密基地", "巨人之杯", "诱惑的森林",
                "树公寓的化石群", "尽头的漩涡", "黎明的箱庭", "绝界的祭坛", "奈落之底"};
        String str = strs[(int)(0 + Math.random()*(10-1+1))];
        return str;
    }
    private String randomPrice(){
        String[] strs = {"8.00", "18.00", "28.00", "38.00", "48.00", "58.00", "68.00", "78.00", "88.00", "98.00"};
        String str = strs[(int)(0 + Math.random()*(10-1+1))];
        return str;
    }

    private int randIcon(){
        int icons[] = {R.drawable.sec_0,R.drawable.sec_1,R.drawable.sec_2,R.drawable.sec_3,R.drawable.sec_4,
                R.drawable.sec_5,R.drawable.sec_6,R.drawable.sec_7,R.drawable.sec_8,R.drawable.sec_9};
        int icon = icons[(int)(0 + Math.random()*(10-1+1))];
        resIconID = icon;
        return icon;

    }

    public void switchLanguage(int lang){
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
}

