//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunmi.sunmik1demo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.InputFilter.LengthFilter;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.sunmi.sunmik1demo.R;
import com.sunmi.sunmik1demo.R.styleable;
import java.util.ArrayList;
import java.util.List;

public class PinEntryEditText extends LinearLayout implements TextWatcher, OnKeyListener {
    private static final String TAG = "PinEntryEditText";
    private static final String TYPE_NUMBER = "number";
    private static final String TYPE_PASSWORD = "password";
    private static final String TYPE_PHONE = "phone";
    private static final String TYPE_TEXT = "text";
    private int box = 4;
    private Drawable boxBgFocus = null;
    private Drawable boxBgNormal = null;
    private int boxHeight = 80;
    private int boxWidth = 80;
    private int childHPadding = 14;
    private int childVPadding = 14;
    private int currentPosition = 0;
    private boolean focus = false;
    private String inputType = "number";
    private PinEntryEditText.Listener listener;
    private List<EditText> mEditTextList = new ArrayList();

    public PinEntryEditText(Context var1, AttributeSet var2) {
        super(var1, var2);
        TypedArray a = var1.obtainStyledAttributes(var2, R.styleable.PinCodeInput);
        box = a.getInt(R.styleable.PinCodeInput_box, 4);

        childHPadding = (int) a.getDimension(R.styleable.PinCodeInput_child_h_padding, 0);
        childVPadding = (int) a.getDimension(R.styleable.PinCodeInput_child_v_padding, 0);
        boxBgFocus =  a.getDrawable(R.styleable.PinCodeInput_box_bg_focus);
        boxBgNormal = a.getDrawable(R.styleable.PinCodeInput_box_bg_normal);
        inputType = a.getString(R.styleable.PinCodeInput_inputType);
        boxWidth = (int) a.getDimension(R.styleable.PinCodeInput_child_width, boxWidth);
        boxHeight = (int) a.getDimension(R.styleable.PinCodeInput_child_height, boxHeight);
        this.initViews();
    }

    private void backFocus() {
        for(int var1 = this.getChildCount() - 1; var1 >= 0; --var1) {
            EditText var2 = (EditText)this.getChildAt(var1);
            if (var2.getText().length() == 1) {
                var2.requestFocus();
                this.setBg((EditText)this.mEditTextList.get(var1), true);
                var2.setSelection(1);
                break;
            }
        }

    }

    private void checkAndCommit() {
        StringBuilder var4 = new StringBuilder();
        boolean var3 = true;
        int var1 = 0;

        boolean var2;
        while(true) {
            var2 = var3;
            if (var1 >= this.box) {
                break;
            }

            String var5 = ((EditText)this.getChildAt(var1)).getText().toString();
            if (var5.length() == 0) {
                var2 = false;
                break;
            }

            var4.append(var5);
            ++var1;
        }

        if (var2 && this.listener != null) {
            this.listener.onComplete(var4.toString());
            this.setEnabled(false);
        }

    }

    private void focus() {
        int var2 = this.getChildCount();

        for(int var1 = 0; var1 < var2; ++var1) {
            EditText var3 = (EditText)this.getChildAt(var1);
            if (var3.getText().length() < 1) {
                var3.requestFocus();
                break;
            }
        }

    }

    private void initViews() {
        for(int var1 = 0; var1 < this.box; ++var1) {
            EditText var2 = new EditText(this.getContext());
            LayoutParams var3 = new LayoutParams(this.boxWidth, this.boxHeight);
            var3.bottomMargin = this.childVPadding;
            var3.topMargin = this.childVPadding;
            var3.leftMargin = this.childHPadding;
            var3.rightMargin = this.childHPadding;
            var3.gravity = 17;
            var2.setOnKeyListener(this);
            if (var1 == 0) {
                this.setBg(var2, true);
            } else {
                this.setBg(var2, false);
            }

            var2.setTextColor(-16777216);
            var2.setLayoutParams(var3);
            var2.setGravity(17);
            var2.setInputType(3);
            var2.setFilters(new InputFilter[]{new LengthFilter(1)});
            if ("number".equals(this.inputType)) {
                var2.setInputType(2);
            } else if ("password".equals(this.inputType)) {
                var2.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else if ("text".equals(this.inputType)) {
                var2.setInputType(1);
            } else if ("phone".equals(this.inputType)) {
                var2.setInputType(3);
            }

            var2.setId(var1);
            var2.setEms(1);
            var2.addTextChangedListener(this);
            this.addView(var2, var1);
            this.mEditTextList.add(var2);
        }

    }

    private void setBg() {
        int var2 = this.getChildCount();

        for(int var1 = 0; var1 < var2; ++var1) {
            EditText var3 = (EditText)this.getChildAt(var1);
            if (this.boxBgNormal != null && !this.focus) {
                var3.setBackground(this.boxBgNormal);
            } else if (this.boxBgFocus != null && this.focus) {
                var3.setBackground(this.boxBgFocus);
            }
        }

    }

    private void setBg(EditText var1, boolean var2) {
        if (this.boxBgNormal != null && !var2) {
            if (TextUtils.isEmpty(var1.getText().toString())) {
                var1.setBackground(this.boxBgNormal);
                return;
            }

            var1.setBackground(this.boxBgFocus);
        } else if (this.boxBgFocus != null && var2) {
            var1.setBackground(this.boxBgFocus);
            return;
        }

    }

    public void afterTextChanged(Editable var1) {
        if (var1.length() != 0) {
            this.focus();
            this.checkAndCommit();
        }
    }

    public void beforeTextChanged(CharSequence var1, int var2, int var3, int var4) {
    }

    public void clear() {
        if (this.currentPosition != 0) {
            for(int var1 = 1; var1 < this.mEditTextList.size(); ++var1) {
                ((EditText)this.mEditTextList.get(var1)).setText("");
                this.setBg((EditText)this.mEditTextList.get(var1), false);
            }

            this.setBg((EditText)this.mEditTextList.get(0), true);
            ((EditText)this.mEditTextList.get(0)).requestFocus();
            this.currentPosition = 0;
        }

    }

    public LayoutParams generateLayoutParams(AttributeSet var1) {
        return new LayoutParams(this.getContext(), var1);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public boolean onKey(View var1, int var2, KeyEvent var3) {
        EditText var4 = (EditText)var1;
        if (var2 == 67 && var4.getText().length() == 0) {
            var2 = var3.getAction();
            if (this.currentPosition != 0 && var2 == 0) {
                --this.currentPosition;
                ((EditText)this.mEditTextList.get(this.currentPosition)).requestFocus();
                this.setBg((EditText)this.mEditTextList.get(this.currentPosition), true);
                this.setBg((EditText)this.mEditTextList.get(this.currentPosition + 1), false);
                ((EditText)this.mEditTextList.get(this.currentPosition)).setText("");
            }
        }

        return false;
    }

    protected void onLayout(boolean var1, int var2, int var3, int var4, int var5) {
        var3 = this.getChildCount();

        for(var2 = 0; var2 < var3; ++var2) {
            View var8 = this.getChildAt(var2);
            var8.setVisibility(VISIBLE);
            var4 = var8.getMeasuredWidth();
            var5 = var8.getMeasuredHeight();
            int var6 = var2 * (this.childHPadding + var4);
            int var7 = this.childVPadding;
            var8.layout(var6, var7, var6 + var4, var7 + var5);
        }

    }

    protected void onMeasure(int var1, int var2) {
        super.onMeasure(var1, var2);
        int var4 = this.getChildCount();

        int var3;
        for(var3 = 0; var3 < var4; ++var3) {
            this.measureChild(this.getChildAt(var3), var1, var2);
        }

        if (var4 > 0) {
            View var6 = this.getChildAt(0);
            var3 = var6.getMeasuredHeight();
            var4 = var6.getMeasuredWidth();
            int var5 = this.childVPadding;
            this.setMeasuredDimension(resolveSize((this.childHPadding + var4) * this.box + this.childHPadding, var1), resolveSize(var3 + var5 * 2, var2));
        }

    }

    public void onTextChanged(CharSequence var1, int var2, int var3, int var4) {
        if (var2 == 0 && var4 >= 1 && this.currentPosition != this.mEditTextList.size() - 1) {
            ++this.currentPosition;
            ((EditText)this.mEditTextList.get(this.currentPosition)).requestFocus();
            this.setBg((EditText)this.mEditTextList.get(this.currentPosition), true);
            this.setBg((EditText)this.mEditTextList.get(this.currentPosition - 1), false);
        }

    }

    public void setEnabled(boolean var1) {
        int var3 = this.getChildCount();

        for(int var2 = 0; var2 < var3; ++var2) {
            this.getChildAt(var2).setEnabled(var1);
        }

    }

    public void setOnCompleteListener(PinEntryEditText.Listener var1) {
        this.listener = var1;
    }

    public interface Listener {
        void onComplete(String var1);
    }
}
