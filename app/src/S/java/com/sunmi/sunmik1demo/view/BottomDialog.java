package com.sunmi.sunmik1demo.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.sunmi.sunmik1demo.R;

/**
 * 2 * Copyright (C), 2018-2019, 商米科技有限公司
 * 3 * FileName: BottomDialog
 * 4 * Author: liuzhicheng
 * 5 * Date: 2019/1/15 13:47
 * 6 * Description: 底部弹出dialog
 * 7 * History:
 */
public class BottomDialog extends Dialog {
    public BottomDialog(Context context) {
        super(context, R.style.DialogStyle);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);//点击外部不可dismiss
    }
    @Override
    public void show() {
        super.show();
        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
    }
}

