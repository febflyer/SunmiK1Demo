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
import com.sunmi.sunmik1demo.view.PinEntryEditText.Listener;

public class PinEntryDialog extends Dialog {
    private View.OnClickListener listener;
    private String pin;
    private PinEntryEditText pinEntry;
    private TextView tvCancel;
    private TextView tvConfirm;

    public PinEntryDialog(@NonNull Context var1) {
        super(var1, R.style.DialogStyle);
    }

    public PinEntryDialog(@NonNull Context var1, int var2) {
        super(var1, var2);
    }

    private void initAction() {
        this.pinEntry.setOnCompleteListener(new Listener() {
            public void onComplete(String var1) {
                PinEntryDialog.this.pin = var1;
            }
        });
    }

    private void initView() {
        this.pinEntry = (PinEntryEditText) this.findViewById(R.id.pin_entry);
        this.tvCancel = (TextView) this.findViewById(R.id.tv_cancel);
        this.tvConfirm = (TextView) this.findViewById(R.id.tv_confirm);
        if (this.listener != null) {
            this.tvCancel.setOnClickListener(listener);
            this.tvConfirm.setOnClickListener(listener);
        }

    }

    public String getPin() {
        return this.pin;
    }

    protected void onCreate(Bundle var1) {
        super.onCreate(var1);
        this.setContentView(R.layout.dialog_pin_entry);
        this.setCancelable(false);
        this.initView();
        this.initAction();
    }

    public void setOnClickListener(View.OnClickListener var1) {
        this.listener = var1;
    }

    public void show() {
        super.show();
        this.pin = "";
    }
}
