//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunmi.sunmik1demo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Map;

public class AutoWrapLinearLayout extends LinearLayout {
    int mBottom;
    private int mHorizontalGap = 133;
    int mLeft;
    int mRight;
    int mTop;
    Map map = new HashMap();
    private int maxCountSingleline = 5;

    public AutoWrapLinearLayout(Context var1) {
        super(var1);
    }

    public AutoWrapLinearLayout(Context var1, AttributeSet var2) {
        super(var1, var2);
    }

    public int getPosition(int var1, int var2) {
        return var1 > 0 ? this.getPosition(var1 - 1, var2 - 1) + this.getChildAt(var2 - 1).getMeasuredWidth() + this.mHorizontalGap : this.getPaddingLeft();
    }

    protected void onLayout(boolean var1, int var2, int var3, int var4, int var5) {
        var3 = this.getChildCount();

        for (var2 = 0; var2 < var3; ++var2) {
            View var6 = this.getChildAt(var2);
            AutoWrapLinearLayout.Position var7 = (AutoWrapLinearLayout.Position) this.map.get(var6);
            if (var7 != null) {
                var6.layout(var7.left, var7.top, var7.right, var7.bottom);
            } else {
                Log.e("@@@@", "onLayout==" + var2);
            }
        }

    }

    protected void onMeasure(int var1, int var2) {
        super.onMeasure(var1, var2);
        int var10 = this.getChildCount();
        int var7 = 0;
        this.mLeft = 0;
        this.mRight = 0;
        this.mTop = 5;
        this.mBottom = 0;
        int var8 = 0;
        int var5 = 0;
        int var4 = 0;

        int var9;
        for (int var3 = 0; var3 < var10; var4 = var9) {
            View var11 = this.getChildAt(var3);
            this.measureChild(var11, var1, var2);
            var9 = var11.getMeasuredHeight();
            AutoWrapLinearLayout.Position var12 = new AutoWrapLinearLayout.Position();
            this.mLeft = this.getPosition(var3 - var8, var3);
            this.mRight = this.mLeft + var11.getMeasuredWidth();
            int var6 = var8;
            if (var3 % this.maxCountSingleline == 0) {
                var6 = var8;
                if (var3 != 0) {
                    var6 = var3;
                    this.mLeft = 0;
                    this.mRight = this.mLeft + var11.getMeasuredWidth();
                    this.mTop = var7 + var9 + 5;
                }
            }

            this.mBottom = this.mTop + var11.getMeasuredHeight();
            var7 = this.mTop;
            var12.left = this.mLeft;
            var12.top = this.mTop + 5;
            var12.right = this.mRight;
            var12.bottom = this.mBottom;
            this.map.put(var11, var12);
            if (var3 == 0) {
                var5 = this.mLeft;
            }

            var9 = var4;
            if (this.mRight > var4) {
                var9 = this.mRight;
            }

            ++var3;
            var8 = var6;
        }

        this.setMeasuredDimension(var4 - var5, this.mBottom);
    }

    public void setHorizontalGap(int var1) {
        this.mHorizontalGap = var1;
    }

    public void setMaxCountSingleline(int var1) {
        this.maxCountSingleline = var1;
    }

    private class Position {
        int bottom;
        int left;
        int right;
        int top;

        private Position() {
        }
    }
}
