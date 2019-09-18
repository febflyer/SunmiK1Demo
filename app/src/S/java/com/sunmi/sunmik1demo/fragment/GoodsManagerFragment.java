package com.sunmi.sunmik1demo.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.sunmi.sunmik1demo.BaseFragment;
import com.sunmi.sunmik1demo.ui.MoreActivity;
import com.sunmi.sunmik1demo.R;
import com.sunmi.sunmik1demo.utils.SharePreferenceUtil;

public class GoodsManagerFragment extends BaseFragment {

    Switch scale, face;
    public final static int Goods_1 = 1;
    public final static int Goods_2 = 2;
    public final static int Goods_3 = 4;
    public final static int Goods_4 = 8;

    public static int defaultGoodsMode = Goods_1 | Goods_2;

    private int Goods_Mode = 15;

    public final static String GOODSMODE_KEY = "GOODSMODE_KEY";

    @Override
    protected int setView() {
        return R.layout.fragment_goods_setting;
    }

    @Override
    protected void init(View view) {
        scale = view.findViewById(R.id.sw_scale);
        face = view.findViewById(R.id.sw_face);
        int goodsMode = (int) SharePreferenceUtil.getParam(getContext(), GOODSMODE_KEY, defaultGoodsMode);

        switch (goodsMode) {
            case 0:
                face.setChecked(false);
                scale.setChecked(false);
                break;
            case Goods_1 | Goods_2:
                face.setChecked(true);
                scale.setChecked(false);
                break;
            case Goods_3 | Goods_4:
                face.setChecked(false);
                scale.setChecked(true);
                break;
            case Goods_1 | Goods_2 | Goods_3 | Goods_4:
                break;
        }
        Goods_Mode = goodsMode;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        face.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Goods_Mode += Goods_1 | Goods_2;

                } else {
                    Goods_Mode -= Goods_1 | Goods_2;
                }
                SharePreferenceUtil.setParam(getContext(), GOODSMODE_KEY, Goods_Mode);
            }
        });

        scale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Goods_Mode += Goods_3 | Goods_4;
                } else {
                    Goods_Mode -= Goods_3 | Goods_4;
                }
                SharePreferenceUtil.setParam(getContext(), GOODSMODE_KEY, Goods_Mode);
            }
        });
    }
}
