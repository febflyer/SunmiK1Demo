package com.sunmi.sunmik1demo.bean;

import android.text.TextUtils;
import android.util.Log;

import com.sunmi.sunmik1demo.MyApplication;
import com.sunmi.sunmik1demo.R;
import com.sunmi.sunmik1demo.bean.blescan.BraceletUserBean;
import com.sunmi.sunmik1demo.db.GoodsDbManager;
import com.sunmi.sunmik1demo.utils.ResourcesUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GoodsCode {
    public final static int MODE_0 = 0;
    public final static int MODE_1 = 1;
    public final static int MODE_2 = 2;
    public final static int MODE_3 = 3;
    public final static int MODE_4 = 4;
    public final static int MODE_5 = 5;


    //无需称重商品
    String[] code = {
            "6901939621257", "6928804014686", "6925303721367", "6921581596048",//drinks

//            "6948939635682", "6948939611542", "4895058313542", "4895058313531", "4895058313533", "1111",//snacks
            "6948939635686", "6948939611543", "4895058313549", "4895058313532",//snacks

            //称重商品
            "1", "2", "3", "4",
            "5", "6", "7", "8",//fruits

            "9", "10", "11", "12",//vegetables

            "6928804011142", "6902827110013", "6920202888883"//others
    };

    int[] icon = {
            R.drawable.goods_1, R.drawable.goods_2, R.drawable.goods_3, R.drawable.goods_4,//drinks

//            R.drawable.goods_union_1, R.drawable.goods_union_2, R.drawable.goods_union_3, R.drawable.goods_union_4, R.drawable.goods_union_5, R.drawable.goods_union_6,//snacks
            R.drawable.goods_5, R.drawable.goods_6, R.drawable.goods_7, R.drawable.goods_8,//snacks

            R.drawable.apple, R.drawable.pears, R.drawable.banana, R.drawable.pitaya,
            R.drawable.goods_sc_1, R.drawable.goods_sc_2, R.drawable.goods_sc_3, R.drawable.goods_sc_4,//fruits

            R.drawable.goods_scs_1, R.drawable.goods_scs_2, R.drawable.goods_scs_3, R.drawable.goods_scs_4,//vegetables

            0, 0, 0
//            R.drawable.coco, R.drawable.sprit, R.drawable.redbull,
    };

    Object[] string = {
            R.string.goods_1, R.string.goods_2, R.string.goods_3, R.string.goods_4,//drinks

//            "旺仔浪味仙-蔬菜味", "旺仔小馒头-特浓牛奶味", "旺旺黑白配-香草味", "旺仔QQ糖-葡萄味", "旺仔牛奶-原味", "旺仔QQ糖-水蜜桃味",
            R.string.goods_5, R.string.goods_6, R.string.goods_7, R.string.goods_8,//snacks

            R.string.goods_apple, R.string.goods_pear, R.string.goods_banana, R.string.goods_pitaya,
            R.string.goods_sc_1, R.string.goods_sc_2, R.string.goods_sc_3, R.string.goods_sc_4,//fruits

            R.string.goods_scs_1, R.string.goods_scs_2, R.string.goods_scs_3, R.string.goods_scs_4,//vegetables

            R.string.goods_coke, R.string.goods_sprite, R.string.goods_red_bull,//others


    };


    float[] price = {
            3.00f, 3.00f, 3.50f, 4.50f,//drinks

//            2.1f, 5.1f, 4.1f, 3.1f, 6.1f, 0.02f,
            6.80f, 6.80f, 6.60f, 6.60f,//snacks

            9.90f, 7.00f, 12.0f, 16.0f,
            13.0f, 20.0f, 12.0f, 8.00f,//fruits

            5.50f, 3.50f, 4.70f, 9.90f,//vegetables

            5.00f, 4.00f, 6.00f,//others

    };

    int[] species = {
            4,//mode = 0
            4,// mode = 1
            8,//mode = 2
            4,// mode = 3
            3,//other mode = 4
    };

    int[] dialog_logo = {

            R.drawable.apple_dialog, R.drawable.pears_dialog, R.drawable.banana_dialog, R.drawable.pitaya_dialog,
            R.drawable.goods_sc_icon_1, R.drawable.goods_sc_icon_2, R.drawable.goods_sc_icon_3, R.drawable.goods_sc_icon_4,

            R.drawable.goods_scs_icon_1, R.drawable.goods_scs_icon_2, R.drawable.goods_scs_icon_3, R.drawable.goods_scs_icon_4,
    };


    private Map<String, GvBeans> Goods = new HashMap<>();
    List<GvBeans> drinks = new ArrayList<>();
    List<GvBeans> snacks = new ArrayList<>();
    List<GvBeans> vegetables = new ArrayList<>();
    List<GvBeans> fruits = new ArrayList<>();
    List<GvBeans> others = new ArrayList<>();

    private static GoodsCode instance = null;

    private GoodsDbManager goodsDbManager;


    public static GoodsCode getInstance() {
        if (instance == null) {
            instance = new GoodsCode();
        }
        return instance;
    }


    private GoodsCode() {
        if (this.goodsDbManager == null) {
            this.goodsDbManager = new GoodsDbManager();
        }

        if (this.goodsDbManager.getAllGoods().size() == 0) {
            addDefaultGoods();
        }
        updateAllGoods();

    }

    private void updateAllGoods() {
        if (Goods.isEmpty()) {
            Iterator<GvBeans> var1 = this.goodsDbManager.getAllGoods().iterator();
            while (var1.hasNext()) {
                GvBeans value = var1.next();
                if (!TextUtils.isEmpty(value.getCode())) {
                    Goods.put(value.getCode(), value);
                    addToMode(value.getMode(), value);
                }
            }
        }

    }

    private void addDefaultGoods() {
        int j = 0;
        for (int i = 0; i < species.length; i++) {
            if (i >= 1) {
                j += species[i - 1];
            }
            for (int i1 = 0; i1 < species[i]; i1++) {
                if (string[i1 + j] instanceof Integer) {
                    add(code[i1 + j], icon[i1 + j], ResourcesUtils.getString(MyApplication.getInstance(), (Integer) string[i1 + j]), price[i1 + j], i);
                } else {
                    add(code[i1 + j], icon[i1 + j], (String) string[i1 + j], price[i1 + j], i);
                }
            }
        }
    }

    public List<GvBeans> getVegetables() {
        return vegetables;
    }

    public List<GvBeans> getFruits() {
        return fruits;
    }

    public Map<String, GvBeans> getGood() {
        return Goods;
    }

    public List<GvBeans> getSnacks() {

        return snacks;
    }

    public List<GvBeans> getDrinks() {
        return drinks;
    }

    public List<GvBeans> getOthers() {
        return others;
    }

    public void add(String code, int imageId, String resString, float price, int mode) {
        add(code, imageId, resString, price, 100, "", mode);
    }


    public void add(String code, String imageUrl, String resString, float price, int num, String unit, int mode) {
        GvBeans gvBeans = new GvBeans(imageUrl, resString, ResourcesUtils.getString(R.string.units_money) + price, code, num, unit, mode);
        goodsDbManager.addGoods(gvBeans);
        Goods.put(code, gvBeans);
        addToMode(mode, gvBeans);

    }

    public void add(String code, int imageId, String resString, float price, int num, String unit, int mode) {
        GvBeans gvBeans = new GvBeans(imageId, resString, ResourcesUtils.getString(R.string.units_money) + price, code, num, unit, mode);
        goodsDbManager.addGoods(gvBeans);
        Goods.put(code, gvBeans);
        addToMode(mode, gvBeans);

    }

    public void update(GvBeans gvBeans) {
        goodsDbManager.updateGoods(gvBeans);
    }

    public void deleteGoods(String code) {
        goodsDbManager.deleteGoods(Goods.get(code));
        deleteForMode(Goods.get(code).getMode(), Goods.get(code));
        Goods.remove(code);
    }

    private void deleteForMode(int mode, GvBeans gvBeans) {
        switch (mode) {
            case MODE_0:
                drinks.remove(gvBeans);
                break;
            case MODE_1:
                snacks.remove(gvBeans);
                break;
            case MODE_2:
                fruits.remove(gvBeans);
                break;
            case MODE_3:
                vegetables.remove(gvBeans);
                break;
            case MODE_4:
                break;
            case MODE_5:
                others.remove(gvBeans);
                break;
        }
    }

    private void addToMode(int mode, GvBeans gvBeans) {
        switch (mode) {
            case MODE_0:
                drinks.add(gvBeans);
                break;
            case MODE_1:
                snacks.add(gvBeans);
                break;
            case MODE_2:
                fruits.add(gvBeans);
                break;
            case MODE_3:
                vegetables.add(gvBeans);
                break;
            case MODE_4:
                break;
            case MODE_5:
                others.add(gvBeans);
                break;

        }
    }


    public GvBeans getGvBeansByCode(String code) {
        return Goods.get(code);
    }

}
