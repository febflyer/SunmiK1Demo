//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunmi.sunmik1demo.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.sunmi.sunmik1demo.R;

public class CustomDialog extends Dialog {
    private View.OnClickListener listener;
    private TextView tvCancel;
    private TextView tvConfirm;

    public CustomDialog(@NonNull Context var1) {
        super(var1, R.style.DialogStyle);
    }

    public CustomDialog(@NonNull Context var1, int var2) {
        super(var1, var2);
    }

    protected void onCreate(Bundle var1) {
        super.onCreate(var1);
        this.setContentView(R.layout.dialog_custom);
        this.setCancelable(false);
        this.tvCancel = (TextView) this.findViewById(R.id.tv_cancel);
        this.tvConfirm = (TextView) this.findViewById(R.id.tv_confirm);
        if (this.listener != null) {
            this.tvCancel.setOnClickListener(listener);
            this.tvConfirm.setOnClickListener(listener);
        }

    }

    public void setOnClickListener(View.OnClickListener var1) {
        this.listener = var1;
    }
}
