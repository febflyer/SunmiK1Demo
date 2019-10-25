package com.sunmi.sunmik1demo.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sunmi.sunmik1demo.R;

public class CustomCarGoodsCounterView extends FrameLayout implements View.OnClickListener {


    /**
     * 商品数量
     */
    int mGoodsNumber = 1;
    int mMaxCount;

    EditText etNumber;

    private UpdateGoodsNumberListener mUpdateGoodsNumberListener;

    public CustomCarGoodsCounterView(Context context) {
        this(context, null);
    }

    public CustomCarGoodsCounterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomCarGoodsCounterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.item_car_number_add_sub_layout, this, false);
        etNumber = rootView.findViewById(R.id.et_number);
        addView(rootView);
        findViewById(R.id.tv_add).setOnClickListener(this);
        findViewById(R.id.tv_sub).setOnClickListener(this);
        etNumber.addTextChangedListener(textWatcher);
        etNumber.setOnFocusChangeListener(new android.view.View.
                OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                } else {
                    // 此处为失去焦点时的处理内容
                    if (TextUtils.isEmpty(etNumber.getText().toString())) {
                        etNumber.setText(1 + "");
                    }
                }
            }
        });
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!TextUtils.isEmpty(s)) {
                mGoodsNumber = Integer.parseInt(s.toString());
            }
        }
    };

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add:
                addNumber();
                break;
            case R.id.tv_sub:
                subNumber();
                break;
        }
        updateGoodsNumber();
    }

    /**
     * 更新商品数量
     */
    public void updateGoodsNumber() {
        etNumber.setText(String.valueOf(mGoodsNumber));
        if (mUpdateGoodsNumberListener != null) {
            mUpdateGoodsNumberListener.updateGoodsNumber(mGoodsNumber);
        }
    }

    public void addNumber() {
        ++mGoodsNumber;
    }

    public void subNumber() {
        mGoodsNumber = (mGoodsNumber - 1 < 1) ? 1 : mGoodsNumber - 1;
    }

    /**
     * 获取商品数量
     *
     * @return
     */
    public int getGoodsNumber() {
        return mGoodsNumber;
    }

    public void setGoodsNumber(int goodsNumber) {
        mGoodsNumber = goodsNumber;
        updateGoodsNumber();
    }

    public void setUpdateGoodsNumberListener(UpdateGoodsNumberListener listener) {
        mUpdateGoodsNumberListener = listener;
    }

    /**
     * 更新商品数量监听器
     */
    public interface UpdateGoodsNumberListener {
        public void updateGoodsNumber(int number);
    }
}