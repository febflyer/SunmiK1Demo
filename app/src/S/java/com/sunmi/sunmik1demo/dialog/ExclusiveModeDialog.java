//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunmi.sunmik1demo.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sunmi.sunmik1demo.R;
import com.sunmi.sunmik1demo.dialog.SunmiCheckPwd.SunmiUpdateInterface;

public class ExclusiveModeDialog extends Activity implements SunmiUpdateInterface, OnClickListener {
    private LinearLayout mAnimation;
    private Button mBT0;
    private Button mBT1;
    private Button mBT2;
    private Button mBT3;
    private Button mBT4;
    private Button mBT5;
    private Button mBT6;
    private Button mBT7;
    private Button mBT8;
    private Button mBT9;
    private Button mBTND;
    private Button mBack;
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message var1) {
            if (var1 != null && var1.what == 241) {
                ExclusiveModeDialog.this.update_pwd_dot(ExclusiveModeDialog.this.mSunmiCheckPwd.getPwdInputNum());
            }

        }
    };
    private ImageView mPwdDotA;
    private ImageView mPwdDotB;
    private ImageView mPwdDotC;
    private ImageView mPwdDotD;
    private SunmiCheckPwd mSunmiCheckPwd;
    CountDownTimer timer = new CountDownTimer(30000L, 1000L) {
        public void onFinish() {
            ExclusiveModeDialog.this.setResult(0);
            ExclusiveModeDialog.this.dismiss();
        }

        public void onTick(long var1) {
            ExclusiveModeDialog.this.tvPwdUnlockTime.setText(var1 / 1000L + "s");
        }
    };
    private TextView tvPwdUnlockTime;

    public ExclusiveModeDialog() {
    }

    private void initView() {
        this.mSunmiCheckPwd = new SunmiCheckPwd(this, this);
        mBT1 = (Button)findViewById(R.id.m_n1);
        mBT1.setOnClickListener(this);
        mBT2 = (Button)findViewById(R.id.m_n2);
        mBT2.setOnClickListener(this);
        mBT3 = (Button)findViewById(R.id.m_n3);
        mBT3.setOnClickListener(this);

        mBT4 = (Button)findViewById(R.id.m_n4);
        mBT4.setOnClickListener(this);
        mBT5 = (Button)findViewById(R.id.m_n5);
        mBT5.setOnClickListener(this);
        mBT6 = (Button)findViewById(R.id.m_n6);
        mBT6.setOnClickListener(this);


        mBT7 = (Button)findViewById(R.id.m_n7);
        mBT7.setOnClickListener(this);
        mBT8 = (Button)findViewById(R.id.m_n8);
        mBT8.setOnClickListener(this);
        mBT9 = (Button)findViewById(R.id.m_n9);
        mBT9.setOnClickListener(this);

        mBT0 = (Button)findViewById(R.id.m_n0);
        mBT0.setOnClickListener(this);


        mBTND = (Button)findViewById(R.id.m_nd);
        mBTND.setOnClickListener(this);
        mBack = (Button)findViewById(R.id.m_back_bt);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dismiss();
            }
        });
        mPwdDotA = (ImageView)findViewById(R.id.m_pwd_dot_a);
        mPwdDotB = (ImageView)findViewById(R.id.m_pwd_dot_b);
        mPwdDotC = (ImageView)findViewById(R.id.m_pwd_dot_c);
        mPwdDotD = (ImageView)findViewById(R.id.m_pwd_dot_d);
        mAnimation = (LinearLayout)findViewById(R.id.m_pwd_dot_line);
        ((TextView)this.findViewById(R.id.tv_cancel)).setOnClickListener(new OnClickListener() {
            public void onClick(View var1) {
                ExclusiveModeDialog.this.setResult(0);
                ExclusiveModeDialog.this.dismiss();
            }
        });

        this.tvPwdUnlockTime = (TextView)this.findViewById(R.id.tv_pwd_unlock_time);
    }

    private void update_pwd_dot(int var1) {
        switch(var1) {
            case 0:
            {
                mPwdDotA.setBackgroundResource(R.drawable.pwd_dot_empty);
                mPwdDotB.setBackgroundResource(R.drawable.pwd_dot_empty);
                mPwdDotC.setBackgroundResource(R.drawable.pwd_dot_empty);
                mPwdDotD.setBackgroundResource(R.drawable.pwd_dot_empty);
                break;
            }
            case 1:
            {
                mPwdDotA.setBackgroundResource(R.drawable.pwd_dot_normal);
                mPwdDotB.setBackgroundResource(R.drawable.pwd_dot_empty);
                mPwdDotC.setBackgroundResource(R.drawable.pwd_dot_empty);
                mPwdDotD.setBackgroundResource(R.drawable.pwd_dot_empty);
                break;
            }
            case 2:
            {
                mPwdDotA.setBackgroundResource(R.drawable.pwd_dot_normal);
                mPwdDotB.setBackgroundResource(R.drawable.pwd_dot_normal);
                mPwdDotC.setBackgroundResource(R.drawable.pwd_dot_empty);
                mPwdDotD.setBackgroundResource(R.drawable.pwd_dot_empty);
                break;
            }
            case 3:
            {
                mPwdDotA.setBackgroundResource(R.drawable.pwd_dot_normal);
                mPwdDotB.setBackgroundResource(R.drawable.pwd_dot_normal);
                mPwdDotC.setBackgroundResource(R.drawable.pwd_dot_normal);
                mPwdDotD.setBackgroundResource(R.drawable.pwd_dot_empty);
                break;
            }
            case 4: {
                mPwdDotA.setBackgroundResource(R.drawable.pwd_dot_normal);
                mPwdDotB.setBackgroundResource(R.drawable.pwd_dot_normal);
                mPwdDotC.setBackgroundResource(R.drawable.pwd_dot_normal);
                mPwdDotD.setBackgroundResource(R.drawable.pwd_dot_normal);
                break;
            }
            default:
        }
    }

    public void dismiss() {
        this.timer.cancel();
        this.finish();
    }

    public void hideNavigationBar() {
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE;
        try {
            this.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        } catch (Exception var3) {
            ;
        }
    }

    public void onClick(View var1) {
        this.mSunmiCheckPwd.onClick(var1);
    }

    protected void onCreate(@Nullable Bundle var1) {
        super.onCreate(var1);
        this.getWindow().setFlags(1024, 1024);
        this.setContentView(R.layout.exclusive_dialog);
        this.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {
            public void onSystemUiVisibilityChange(int var1) {
                ExclusiveModeDialog.this.hideNavigationBar();
            }
        });
        this.initView();
        this.timer.start();
    }

    public void updatepwd(int var1, int var2) {
        this.update_pwd_dot(var1);
        if (var2 == 1) {
            this.update_pwd_dot(0);
            this.setResult(RESULT_OK);
            this.dismiss();
        } else if (var2 == 2) {
            Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
            TranslateAnimation var3 = new TranslateAnimation(20.0F, -20.0F, 0.0F, 0.0F);
            var3.setInterpolator(new OvershootInterpolator());
            var3.setDuration(20L);
            var3.setRepeatCount(5);
            var3.setRepeatMode(2);
            var3.setAnimationListener(new AnimationListener() {
                public void onAnimationEnd(Animation var1) {
                    ExclusiveModeDialog.this.mHandler.sendEmptyMessageDelayed(241, 300L);
                }

                public void onAnimationRepeat(Animation var1) {
                }

                public void onAnimationStart(Animation var1) {
                    mPwdDotA.setBackgroundResource(R.drawable.pwd_dot_add);
                    mPwdDotB.setBackgroundResource(R.drawable.pwd_dot_add);
                    mPwdDotC.setBackgroundResource(R.drawable.pwd_dot_add);
                    mPwdDotD.setBackgroundResource(R.drawable.pwd_dot_add);
                }
            });
            this.mAnimation.startAnimation(var3);
            return;
        }

    }
}
