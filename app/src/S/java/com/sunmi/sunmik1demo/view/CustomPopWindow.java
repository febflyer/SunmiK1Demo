//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunmi.sunmik1demo.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

public class CustomPopWindow {
    private int mAnimationStyle;
    private boolean mClippEnable;
    private View mContentView;
    private Context mContext;
    private int mHeight;
    private boolean mIgnoreCheekPress;
    private int mInputMode;
    private boolean mIsFocusable;
    private boolean mIsOutside;
    private OnDismissListener mOnDismissListener;
    private OnTouchListener mOnTouchListener;
    private PopupWindow mPopupWindow;
    private int mResLayoutId;
    private int mSoftInputMode;
    private boolean mTouchable;
    private int mWidth;

    private CustomPopWindow(Context var1) {
        this.mIsFocusable = true;
        this.mIsOutside = true;
        this.mResLayoutId = -1;
        this.mAnimationStyle = -1;
        this.mClippEnable = true;
        this.mIgnoreCheekPress = false;
        this.mInputMode = -1;
        this.mSoftInputMode = -1;
        this.mTouchable = true;
        this.mContext = var1;
    }

    private void apply(PopupWindow var1) {
        var1.setClippingEnabled(this.mClippEnable);
        if (this.mIgnoreCheekPress) {
            var1.setIgnoreCheekPress();
        }

        if (this.mInputMode != -1) {
            var1.setInputMethodMode(this.mInputMode);
        }

        if (this.mSoftInputMode != -1) {
            var1.setSoftInputMode(this.mSoftInputMode);
        }

        if (this.mOnDismissListener != null) {
            var1.setOnDismissListener(this.mOnDismissListener);
        }

        if (this.mOnTouchListener != null) {
            var1.setTouchInterceptor(this.mOnTouchListener);
        }

        var1.setTouchable(this.mTouchable);
    }

    private PopupWindow build() {
        if (this.mContentView == null) {
            this.mContentView = LayoutInflater.from(this.mContext).inflate(this.mResLayoutId, (ViewGroup)null);
        }

        if (this.mWidth != 0 && this.mHeight != 0) {
            this.mPopupWindow = new PopupWindow(this.mContentView, this.mWidth, this.mHeight);
        } else {
            this.mPopupWindow = new PopupWindow(this.mContentView, -2, -2);
        }

        if (this.mAnimationStyle != -1) {
            this.mPopupWindow.setAnimationStyle(this.mAnimationStyle);
        }

        this.apply(this.mPopupWindow);
        this.mPopupWindow.setFocusable(this.mIsFocusable);
        this.mPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
        this.mPopupWindow.setOutsideTouchable(this.mIsOutside);
        if (this.mWidth == 0 || this.mHeight == 0) {
            this.mPopupWindow.getContentView().measure(0, 0);
            this.mWidth = this.mPopupWindow.getContentView().getMeasuredWidth();
            this.mHeight = this.mPopupWindow.getContentView().getMeasuredHeight();
        }

        this.mPopupWindow.update();
        return this.mPopupWindow;
    }

    public void dissmiss() {
        if (this.mPopupWindow != null) {
            this.mPopupWindow.dismiss();
        }

    }

    public int getHeight() {
        return this.mHeight;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public CustomPopWindow showAsDropDown(View var1) {
        if (this.mPopupWindow != null) {
            this.mPopupWindow.showAsDropDown(var1);
        }

        return this;
    }

    public CustomPopWindow showAsDropDown(View var1, int var2, int var3) {
        if (this.mPopupWindow != null) {
            this.mPopupWindow.showAsDropDown(var1, var2, var3);
        }

        return this;
    }

    public CustomPopWindow showAsDropDown(View var1, int var2, int var3, int var4) {
        if (this.mPopupWindow != null) {
            this.mPopupWindow.showAsDropDown(var1, var2, var3, var4);
        }

        return this;
    }

    public CustomPopWindow showAtLocation(View var1, int var2, int var3, int var4) {
        if (this.mPopupWindow != null) {
            this.mPopupWindow.showAtLocation(var1, var2, var3, var4);
        }

        return this;
    }

    public static class PopupWindowBuilder {
        private CustomPopWindow mCustomPopWindow;

        public PopupWindowBuilder(Context var1) {
            this.mCustomPopWindow = new CustomPopWindow(var1);
        }

        public CustomPopWindow create() {
            this.mCustomPopWindow.build();
            return this.mCustomPopWindow;
        }

        public CustomPopWindow.PopupWindowBuilder setAnimationStyle(int var1) {
            this.mCustomPopWindow.mAnimationStyle = var1;
            return this;
        }

        public CustomPopWindow.PopupWindowBuilder setClippingEnable(boolean var1) {
            this.mCustomPopWindow.mClippEnable = var1;
            return this;
        }

        public CustomPopWindow.PopupWindowBuilder setFocusable(boolean var1) {
            this.mCustomPopWindow.mIsFocusable = var1;
            return this;
        }

        public CustomPopWindow.PopupWindowBuilder setIgnoreCheekPress(boolean var1) {
            this.mCustomPopWindow.mIgnoreCheekPress = var1;
            return this;
        }

        public CustomPopWindow.PopupWindowBuilder setInputMethodMode(int var1) {
            this.mCustomPopWindow.mInputMode = var1;
            return this;
        }

        public CustomPopWindow.PopupWindowBuilder setOnDissmissListener(OnDismissListener var1) {
            this.mCustomPopWindow.mOnDismissListener = var1;
            return this;
        }

        public CustomPopWindow.PopupWindowBuilder setOutsideTouchable(boolean var1) {
            this.mCustomPopWindow.mIsOutside = var1;
            return this;
        }

        public CustomPopWindow.PopupWindowBuilder setSoftInputMode(int var1) {
            this.mCustomPopWindow.mSoftInputMode = var1;
            return this;
        }

        public CustomPopWindow.PopupWindowBuilder setTouchIntercepter(OnTouchListener var1) {
            this.mCustomPopWindow.mOnTouchListener = var1;
            return this;
        }

        public CustomPopWindow.PopupWindowBuilder setTouchable(boolean var1) {
            this.mCustomPopWindow.mTouchable = var1;
            return this;
        }

        public CustomPopWindow.PopupWindowBuilder setView(int var1) {
            this.mCustomPopWindow.mResLayoutId = var1;
            this.mCustomPopWindow.mContentView = null;
            return this;
        }

        public CustomPopWindow.PopupWindowBuilder setView(View var1) {
            this.mCustomPopWindow.mContentView = var1;
            this.mCustomPopWindow.mResLayoutId = -1;
            return this;
        }

        public CustomPopWindow.PopupWindowBuilder size(int var1, int var2) {
            this.mCustomPopWindow.mWidth = var1;
            this.mCustomPopWindow.mHeight = var2;
            return this;
        }
    }
}
