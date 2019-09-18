//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunmi.sunmik1demo.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Shader.TileMode;
import android.support.annotation.Keep;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class CircularProgressView extends View {
    public static final int PROGRESS_FACTOR = -360;
    public static final String PROGRESS_PROPERTY = "progress";
    protected final RectF arcElements;
    protected final Paint paint;
    protected float progress;
    private Rect rec;
    protected int ringColor;
    protected int ringWidth;
    private LinearGradient shader;

    public CircularProgressView(Context var1) {
        this(var1, (AttributeSet)null);
    }

    public CircularProgressView(Context var1, AttributeSet var2) {
        super(var1, var2);
        this.ringColor = -1;
        this.ringWidth = 5;
        this.progress = 0.0F;
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.arcElements = new RectF();
        this.rec = new Rect();
    }

    private float dpToPx(int var1) {
        return TypedValue.applyDimension(1, (float)var1, this.getContext().getResources().getDisplayMetrics());
    }

    private float spToPx(int var1) {
        return TypedValue.applyDimension(2, (float)var1, this.getContext().getResources().getDisplayMetrics());
    }

    public float getProgress() {
        return this.progress / -360.0F;
    }

    public int getRingColor() {
        return this.ringColor;
    }

    public int getRingWidth() {
        return this.ringWidth;
    }

    public void onDraw(Canvas var1) {
        super.onDraw(var1);
        float var2 = (float)(Math.min(var1.getHeight(), var1.getWidth()) / 2 - this.ringWidth / 2);
        float var3 = ((float)var1.getWidth() - 2.0F * var2) / 2.0F;
        float var4 = ((float)var1.getHeight() - 2.0F * var2) / 2.0F;
        int var9 = this.ringWidth / 2;
        float var5 = (float)var9;
        float var6 = (float)var9;
        float var7 = (float)var9;
        float var8 = (float)var9;
        this.paint.setStyle(Style.STROKE);
        this.paint.setStrokeWidth((float)this.ringWidth);
        this.paint.setStrokeCap(Cap.ROUND);
        this.arcElements.set(var3 + var5, var4 + var6, 2.0F * var2 + var3 - var7, 2.0F * var2 + var4 - var8);
        this.paint.setColor(-7829368);
        var1.drawArc(this.arcElements, 0.0F, 360.0F, false, this.paint);
        if (this.ringColor != 0) {
            this.paint.setColor(this.ringColor);
            var1.drawArc(this.arcElements, -90.0F, -this.progress, false, this.paint);
        } else {
            if (this.shader == null) {
                var9 = Color.parseColor("#B4ED50");
                int var10 = Color.parseColor("#429321");
                TileMode var11 = TileMode.CLAMP;
                this.shader = new LinearGradient(0.0F, var4, 0.0F, var4 + 2.0F * var2, new int[]{var9, var10}, (float[])null, var11);
            }

            this.paint.setShader(this.shader);
            var1.drawArc(this.arcElements, -90.0F, -this.progress, false, this.paint);
        }

        var9 = -((int)((double)this.progress / 3.6D));
        String var12 = var9 + "%";
        this.paint.setShader((Shader)null);
        this.paint.setStyle(Style.FILL);
        this.paint.setColor(-1);
        this.paint.setTextSize(this.spToPx(30));
        this.paint.getTextBounds(var12, 0, var12.length(), this.rec);
    }

    @Keep
    public void setProgress(float var1) {
        this.progress = -360.0F * var1;
        this.invalidate();
    }

    public void setRingColor(int var1) {
        this.ringColor = var1;
        this.invalidate();
    }

    public void setRingWidth(int var1) {
        this.ringWidth = var1;
        this.invalidate();
    }

    public void startAnim(float var1, boolean var2, int var3, AnimatorListener var4) {
        AnimatorSet var7 = new AnimatorSet();
        ObjectAnimator var8 = ObjectAnimator.ofFloat(this, "progress", new float[]{0.0F, var1});
        long var5;
        if (var2) {
            var5 = (long)var3;
        } else {
            var5 = 0L;
        }

        var8.setDuration(var5);
        var8.setInterpolator(new DecelerateInterpolator());
        var7.addListener(var4);
        var7.play(var8);
        var7.start();
    }
}
