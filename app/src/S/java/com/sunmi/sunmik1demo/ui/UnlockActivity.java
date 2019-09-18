//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunmi.sunmik1demo.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

import com.sunmi.sunmik1demo.R;
import com.sunmi.sunmik1demo.adapter.LayoutTransitionAdapter;
import com.sunmi.sunmik1demo.adapter.UnlockAllUserAdapter;
import com.sunmi.sunmik1demo.adapter.LayoutTransitionAdapter.OnAnimationListener;
import com.sunmi.sunmik1demo.adapter.LayoutTransitionAdapter.OnItemClickListener;
import com.sunmi.sunmik1demo.adapter.UnlockAllUserAdapter.UnlockUser;
import com.sunmi.sunmik1demo.bean.blescan.BraceletUserBean;
import com.sunmi.sunmik1demo.dialog.ExclusiveModeDialog;
import com.sunmi.sunmik1demo.eventbus.UnlockSuccessEvent;
import com.sunmi.sunmik1demo.eventbus.UpdateUnlockStateEvent;
import com.sunmi.sunmik1demo.unlock.BraceletPresenter;
import com.sunmi.sunmik1demo.unlock.BLEUserModel;
import com.sunmi.sunmik1demo.utils.ResourcesUtils;
import com.sunmi.sunmik1demo.view.AutoWrapLinearLayout;
import com.sunmi.sunmik1demo.view.CircularProgressView;

import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class UnlockActivity extends Activity implements OnItemClickListener, OnClickListener {
    public static final int LOCK = 0;
    public static final int UNLOCK = 1;
    public static final int UNLOCK_LIST = 2;
    public static int state = 1;
    public static int oldstate = -1;
    public static final int UNLOCK_PWD = -1;


    public static final String SHOPNAME = ResourcesUtils.getString(R.string.more_bracelet_shop);
    public static final int SHOP_ICON = R.drawable.bracelet_shop;
    private static final String TAG = UnlockActivity.class.getSimpleName();

    public boolean isChangeUser;
    public static boolean isUnlocking;
    private UnlockAllUserAdapter allUserAdapter;
    private List<UnlockUser> allUserList = new ArrayList();
    private AutoWrapLinearLayout autoWrapLinearLayout;
    private BLEUserModel bleUserModel;
    private CircularProgressView cirUnlockAnim;
    private UnlockUser domainUser;
    private int[] domainView = new int[3];
    private int[] duration = new int[]{500, 500, 500, 500, 500};
    private FrameLayout flCheckUser;
    private FrameLayout flDomainUnlock;
    private FrameLayout flLock;
    private Handler handler = new Handler(Looper.getMainLooper());
    private ImageView ivUnlockAnim;
    private ImageView ivUnlockAnim2;
    private CircleImageView ivUnlockLogo;
    private LayoutTransitionAdapter layoutTransitionAdapter;
    private LinearLayout llUnlockEmpty;
    private boolean noUser = false;

    com.sunmi.sunmik1demo.adapter.UnlockAllUserAdapter.OnItemClickListener onItemClickListener = new com.sunmi.sunmik1demo.adapter.UnlockAllUserAdapter.OnItemClickListener() {
        public void onItemClick(View var1, int var2) {
            UnlockActivity.this.domainUser = (UnlockUser) UnlockActivity.this.allUserList.get(var2);
            var1 = var1.findViewById(R.id.iv_user);
            int[] var4 = new int[2];
            var1.getLocationOnScreen(var4);
            var2 = var4[0];
            int var3 = var4[1];
            UnlockActivity.this.domainView[0] = var2;
            UnlockActivity.this.domainView[1] = var3;
            UnlockActivity.this.domainView[2] = var1.getMeasuredHeight();
            UnlockActivity.this.flDomainUnlock.setVisibility(View.GONE);
            UnlockActivity.this.startActivityForResult(new Intent(UnlockActivity.this, ExclusiveModeDialog.class), 0);
        }
    };
    private RecyclerView recyclerUnlockAll;
    private TextView tvDomainUnlockCancel;
    private TextView tvTips;
    private TextView tvUnlockLocking;
    private TextView tvUnlockPwd;
    private TextView tvUnlockUserName;
    private BraceletUserBean userBean;
    private List<UnlockUser> userList = new ArrayList();//发送过来的使用者列表
    private List<UnlockUser> userNewList = new ArrayList();//显示的使用者列表

    public UnlockActivity() {
    }

    private void addUser(List<UnlockUser> var1) {
        ArrayList var3 = new ArrayList();

        for (int var2 = 0; var2 < var1.size(); ++var2) {
            UnlockUser var4 = (UnlockUser) var1.get(var2);
            if (!this.userNewList.contains(var4)) {
                var3.add(var4);
            }
        }

        Log.e(TAG, "showListUnlock==add" + var3.size());
        if (var3.size() != 0) {
            this.layoutTransitionAdapter.addAll(var3);
        }

    }

    private void initAction() {
        this.layoutTransitionAdapter.setOnItemClickListener(this);
        this.tvUnlockPwd.setOnClickListener(this);
        this.tvDomainUnlockCancel.setOnClickListener(this);
        this.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {
            public void onSystemUiVisibilityChange(int var1) {
//                UnlockActivity.this.hideNavigationBar();
            }
        });
    }

    private void initData() {
        if (this.layoutTransitionAdapter == null) {
            this.layoutTransitionAdapter = new LayoutTransitionAdapter(this, this.autoWrapLinearLayout, this.userNewList);
            LayoutTransition var3 = this.layoutTransitionAdapter.initTransition(this.autoWrapLinearLayout);
            this.layoutTransitionAdapter.setTransitioner(var3);
            layoutTransitionAdapter.setOnAnimationListener(new OnAnimationListener() {
                @Override
                public void end(int var1) {
                    Log.e(TAG, "setOnAnimationListener==end");

                }

                @Override
                public void start(int var1) {

                }
            });
        }

        if (this.bleUserModel == null) {
            this.bleUserModel = new BLEUserModel();
        }

        if (this.userBean == null) {
            this.userBean = new BraceletUserBean();
        }

        Log.e(TAG, "UNLOCK===" + this.getIntent().getIntExtra("mode", 0) + " " + state + isChangeUser);
        int mode = this.getIntent().getIntExtra("mode", 0);
        Bundle bundle = this.getIntent().getBundleExtra("params");

        this.userList.clear();
        switch (mode) {
            case LOCK:
                if (!TextUtils.isEmpty(bundle.getString("address"))) {
                    BraceletUserBean braceletUserBean = this.bleUserModel.getBraceletUserByMac(bundle.getString("address"));
                    UnlockUser var10 = new UnlockUser();
                    var10.setIcon(braceletUserBean.getIcon());
                    var10.setName(braceletUserBean.getName());
                    var10.setMac(braceletUserBean.getMac());
                    this.userList.add(var10);
                    this.userBean = braceletUserBean;
                }
                break;
            case UNLOCK:
                this.userBean = bleUserModel.getBraceletUserByMac(bundle.getString("address"));
                break;
            case UNLOCK_LIST:
                List<String> list = bundle.getStringArrayList("addresses");
                for (String s : list) {
                    BraceletUserBean braceletUserBean1 = this.bleUserModel.getBraceletUserByMac(s);
                    UnlockUser var6 = new UnlockUser();
                    var6.setIcon(braceletUserBean1.getIcon());
                    var6.setName(braceletUserBean1.getName());
                    var6.setMac(s);
                    if (list.size() == 1) {
                        this.userBean = bleUserModel.getBraceletUserByMac(list.get(0));
                    }
                    this.userList.add(var6);
                }
                break;
        }

        updateState(mode);
    }

    private void updateState(int mode) {
        if (isChangeUser || isUnlocking || state == UNLOCK_PWD) {
            return;
        }
        switch (mode) {
            case LOCK:
                this.updateTips(3);
                if (this.userList.size() == 1) {
                    this.showListUnlock(this.userList);
                    return;
                }
                this.showLock();
                return;
            case UNLOCK:
                EventBus.getDefault().post(new UnlockSuccessEvent(this.userBean.getMac()));
                this.showSingleUnlock();
                return;
            case UNLOCK_LIST:
                if (BraceletPresenter.isDomainlock) {
                    this.updateTips(2);
                    showListUnlock(this.userList);
                } else if (userList.size() > 1) {
                    updateTips(0);
                    showListUnlock(userList);
                } else if (userList.size() == 1) {
                    for (UnlockUser unlockUser : userNewList) {
                        if (unlockUser.getMac().equals(userList.get(0).getMac())) {
                            moveUserToCenterAndUnlock(unlockUser.getMac());
                            return;
                        }
                    }
                }
                return;
        }
    }

    private void initView() {
        this.llUnlockEmpty = (LinearLayout) this.findViewById(R.id.ll_unlock_empty);
        this.tvUnlockPwd = (TextView) this.findViewById(R.id.tv_unlock_pwd);
        this.ivUnlockLogo = (CircleImageView) this.findViewById(R.id.iv_unlock_logo);
        this.tvUnlockUserName = (TextView) this.findViewById(R.id.tv_unlock_user_name);
        this.tvUnlockLocking = (TextView) this.findViewById(R.id.tv_unlock_locking);
        this.flCheckUser = (FrameLayout) this.findViewById(R.id.fl_check_user);
        this.autoWrapLinearLayout = (AutoWrapLinearLayout) this.findViewById(R.id.parent);
        this.recyclerUnlockAll = (RecyclerView) this.findViewById(R.id.recycler_unlock_all);
        this.flLock = (FrameLayout) this.findViewById(R.id.fl_lock);
        this.flDomainUnlock = (FrameLayout) this.findViewById(R.id.fl_domain_unlock);
        this.tvDomainUnlockCancel = (TextView) this.findViewById(R.id.tv_domain_unlock_cancel);
        this.cirUnlockAnim = (CircularProgressView) this.findViewById(R.id.cir_unlock_anim);
        this.ivUnlockAnim = (ImageView) this.findViewById(R.id.iv_unlock_anim);
        this.ivUnlockAnim2 = (ImageView) this.findViewById(R.id.iv_unlock_anim2);
        this.tvTips = (TextView) this.findViewById(R.id.tv_tips);
    }

    private void pwdUnlock() {
        oldstate = state;
        state = UNLOCK_PWD;
        if (this.allUserAdapter == null) {
            for (BraceletUserBean braceletUserBean : bleUserModel.getAllFilterBracelet()) {
                UnlockUser var4 = new UnlockUser();
                var4.setIcon(braceletUserBean.getIcon());
                var4.setName(braceletUserBean.getName());
                var4.setMac(braceletUserBean.getMac());
                this.allUserList.add(var4);
            }

            if (this.allUserList.size() == 0) {
                this.noUser = true;
                UnlockUser var5 = new UnlockUser();
                var5.setIcon(SHOP_ICON);
                var5.setName(SHOPNAME);
                var5.setMac(SHOPNAME);
                this.allUserList.add(var5);
            }

            this.allUserAdapter = new UnlockAllUserAdapter(this.allUserList);
            this.recyclerUnlockAll.setAdapter(this.allUserAdapter);
            int var1;
            if (this.allUserList.size() >= 5) {
                var1 = 5;
            } else {
                var1 = this.allUserList.size();
            }

            GridLayoutManager var6 = new GridLayoutManager(this, var1);
            this.recyclerUnlockAll.setLayoutManager(var6);
            this.allUserAdapter.setOnItemClickListener(this.onItemClickListener);
        }

        this.flLock.setVisibility(View.INVISIBLE);
        this.flDomainUnlock.setVisibility(View.VISIBLE);
    }

    private void removeOtherUser(List<UnlockUser> var1) {
        Iterator<UnlockUser> iterator = this.userNewList.iterator();
        ArrayList<String> macs = new ArrayList<>();
        //首先删除已存在的手环
        for (UnlockUser unlockUser : var1) {
            macs.add(unlockUser.mac);
        }
        while (iterator.hasNext()) {
            UnlockUser unlockUser = iterator.next();
            if (!macs.contains(unlockUser.mac)) {
                this.layoutTransitionAdapter.remove(unlockUser);
                this.removeOtherUser(var1);
                return;
            }
        }
        Log.e(TAG, "showListUnlock==removeOtherUser==" + var1.size() + "  " + this.userNewList.size());
        //添加其他手环
        if (this.userNewList.size() < var1.size()) {
            Iterator<UnlockUser> iterator1 = var1.iterator();
            macs.clear();
            for (UnlockUser unlockUser : userNewList) {
                macs.add(unlockUser.mac);
            }
            ArrayList<UnlockUser> addlist = new ArrayList<>();

            while (iterator1.hasNext()) {
                UnlockUser unlockUser = iterator.next();
                if (!macs.contains(unlockUser.mac)) {
                    addlist.add(unlockUser);
                }
            }
            this.addUser(addlist);
            return;
        }

    }

    private void removeUser(List<UnlockUser> var1) {
        ArrayList var2 = new ArrayList();
        Iterator var3 = this.userNewList.iterator();

        while (var3.hasNext()) {
            UnlockUser var4 = (UnlockUser) var3.next();
            if (!var1.contains(var4)) {
                var2.add(var4);
            }
        }

        Log.e(TAG, "showListUnlock==remove" + var2.size());
        if (var2.size() != 0) {
            this.layoutTransitionAdapter.removeAll(var2);
        }

    }

    private void setTempView(final int var1, final int var2, final int var3) {
        this.flLock.setVisibility(View.VISIBLE);
        this.llUnlockEmpty.setVisibility(View.VISIBLE);

        this.flCheckUser.setVisibility(View.GONE);
        this.llUnlockEmpty.setAlpha(0.0F);
        this.tvUnlockPwd.setVisibility(View.GONE);
        this.tvUnlockLocking.setVisibility(View.GONE);
        this.ivUnlockLogo.setImageResource(this.userBean.getIcon());
        this.tvUnlockUserName.setText(this.userBean.getName());
        LayoutParams var4 = (LayoutParams) this.ivUnlockLogo.getLayoutParams();
        var4.height = var3;
        var4.width = var3;
        this.ivUnlockLogo.setLayoutParams(var4);
        this.tvUnlockUserName.setTextSize(15.66F);
        android.widget.LinearLayout.LayoutParams var5 = (android.widget.LinearLayout.LayoutParams) this.tvUnlockUserName.getLayoutParams();
        var5.topMargin = 17;
        this.tvUnlockUserName.setLayoutParams(var5);
        this.llUnlockEmpty.post(new Runnable() {
            public void run() {
                int[] var5 = new int[2];
                UnlockActivity.this.ivUnlockLogo.getLocationOnScreen(var5);
                int var1x = var5[0];
                int var2x = var5[1];
                int var3x = var1;
                int var4 = var2;
                UnlockActivity.this.unlockAnimList(UnlockActivity.this.llUnlockEmpty, var3, (float) (var3x - var1x), (float) (var4 - var2x));
            }
        });
    }

    private void showListUnlock(final List<UnlockUser> list) {
        Log.e(TAG, "setOnAnimationListener==showListUnlock");
        isChangeUser = true;
        this.flCheckUser.setVisibility(View.VISIBLE);
        this.tvUnlockPwd.setVisibility(View.VISIBLE);
        this.llUnlockEmpty.setVisibility(View.GONE);
        if (this.userNewList.size() > 0) {
            Log.e(TAG, "showListUnlock==size==" + list.size() + "  " + this.userNewList.size());
            if (list.size() > this.userNewList.size()) {
                this.addUser(list);
            } else if (list.size() < this.userNewList.size()) {
                this.removeUser(list);
            } else {
                this.removeOtherUser(list);
            }
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "showListUnlock==size==addAll" + list.size() + "  " +userNewList.size());
                    UnlockActivity.this.layoutTransitionAdapter.addAll(list);
                }
            },300);

        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isChangeUser = false;
            }
        },1000);
    }

    private void showLock() {
        this.layoutTransitionAdapter.clear();
        this.userList.clear();
        this.flLock.setVisibility(View.VISIBLE);
        this.llUnlockEmpty.setVisibility(View.VISIBLE);
        this.tvUnlockPwd.setVisibility(View.VISIBLE);
        this.flCheckUser.setVisibility(View.GONE);

        this.ivUnlockLogo.setImageResource(SHOP_ICON);
        this.tvUnlockUserName.setText(ResourcesUtils.getString(R.string.unlock_app_lock));
        this.tvUnlockLocking.setText(ResourcesUtils.getString(R.string.unlock_lock_tips));
    }

    private void showSingleUnlock() {
        isUnlocking = true;
        this.flCheckUser.setVisibility(View.GONE);
        this.llUnlockEmpty.setVisibility(View.VISIBLE);
        this.tvUnlockPwd.setVisibility(View.GONE);
        this.ivUnlockLogo.setImageResource(SHOP_ICON);
        this.unlockAnim1();
    }

    private void unlockAnim1() {
        AnimatorSet var1 = new AnimatorSet();
        ObjectAnimator var2 = ObjectAnimator.ofFloat(this.tvUnlockUserName, "alpha", new float[]{1.0F, 0.0F});
        ObjectAnimator var3 = ObjectAnimator.ofFloat(this.tvUnlockLocking, "alpha", new float[]{1.0F, 0.0F});
        var1.addListener(new AnimatorListener() {
            public void onAnimationCancel(Animator var1) {
            }

            public void onAnimationEnd(Animator var1) {
                UnlockActivity.this.tvUnlockUserName.setText(ResourcesUtils.getString(R.string.unlock_hello) + UnlockActivity.this.userBean.getName());
                UnlockActivity.this.unlockAnim2();
            }

            public void onAnimationRepeat(Animator var1) {
            }

            public void onAnimationStart(Animator var1) {
            }
        });
        var1.setDuration((long) this.duration[0]);
        var1.playTogether(new Animator[]{var2, var3});
        var1.start();
    }

    private void unlockAnim2() {
        this.ivUnlockLogo.setImageResource(this.userBean.getIcon());
        AnimatorSet var1 = new AnimatorSet();
        ObjectAnimator var2 = ObjectAnimator.ofFloat(this.ivUnlockLogo, "scaleX", new float[]{0.0F, 1.0F});
        ObjectAnimator var3 = ObjectAnimator.ofFloat(this.ivUnlockLogo, "scaleY", new float[]{0.0F, 1.0F});
        ObjectAnimator var4 = ObjectAnimator.ofFloat(this.tvUnlockUserName, "alpha", new float[]{0.0F, 1.0F});
        var1.addListener(new AnimatorListener() {
            public void onAnimationCancel(Animator var1) {
            }

            public void onAnimationEnd(Animator var1) {
                UnlockActivity.this.tvUnlockUserName.setText(ResourcesUtils.getString(R.string.unlock_unlocking));
                UnlockActivity.this.unlockAnim3();
            }

            public void onAnimationRepeat(Animator var1) {
            }

            public void onAnimationStart(Animator var1) {
            }
        });
        var1.setDuration((long) this.duration[1]);
        var1.setInterpolator(new DecelerateInterpolator());
        var1.play(var2).with(var3).with(var4);
        var1.start();
    }

    private void unlockAnim3() {
        this.cirUnlockAnim.setVisibility(View.VISIBLE);
        this.ivUnlockLogo.setBorderWidth(2);
        this.ivUnlockLogo.setBorderColor(Color.parseColor("#88ffffff"));
        this.cirUnlockAnim.startAnim(1.0F, true, this.duration[2], new AnimatorListener() {
            public void onAnimationCancel(Animator var1) {
            }

            public void onAnimationEnd(Animator var1) {
                UnlockActivity.this.cirUnlockAnim.setRingColor(-16711936);
                UnlockActivity.this.ivUnlockLogo.setBorderWidth(0);
                ObjectAnimator var2 = ObjectAnimator.ofFloat(this, "alpha", new float[]{1.0F, 0.0F});
                var2.setInterpolator(new DecelerateInterpolator());
                var2.setDuration(200L);
                var2.start();
                UnlockActivity.this.handler.postDelayed(new Runnable() {
                    public void run() {
                        UnlockActivity.this.unlockAnim4();
                    }
                }, 200L);
            }

            public void onAnimationRepeat(Animator var1) {
            }

            public void onAnimationStart(Animator var1) {
            }
        });
    }

    private void unlockAnim4() {
        this.tvUnlockUserName.setText(ResourcesUtils.getString(R.string.unlock_success));
        AnimatorSet var1 = new AnimatorSet();
        ObjectAnimator var2 = ObjectAnimator.ofFloat(this.tvUnlockUserName, "alpha", new float[]{0.0F, 1.0F});
        this.ivUnlockAnim.setPivotX(0.0F);
        this.ivUnlockAnim.setPivotY((float) this.ivUnlockAnim.getHeight());
        ObjectAnimator var3 = ObjectAnimator.ofFloat(this.ivUnlockAnim, "rotation", new float[]{0.0F, -45.0F});
        var1.addListener(new AnimatorListener() {
            public void onAnimationCancel(Animator var1) {
            }

            public void onAnimationEnd(Animator var1) {
                UnlockActivity.this.unlockAnim5();
            }

            public void onAnimationRepeat(Animator var1) {
            }

            public void onAnimationStart(Animator var1) {
            }
        });
        var1.setDuration((long) this.duration[3]);
        var1.play(var2).with(var3);
        var1.start();
    }

    private void unlockAnim5() {
        AnimatorSet var1 = new AnimatorSet();
        ObjectAnimator var2 = ObjectAnimator.ofFloat(this.ivUnlockLogo, "alpha", new float[]{1.0F, 0.0F});
        ObjectAnimator var3 = ObjectAnimator.ofFloat(this.tvUnlockUserName, "alpha", new float[]{1.0F, 0.0F});
        ObjectAnimator var4 = ObjectAnimator.ofFloat(this.ivUnlockAnim, "alpha", new float[]{1.0F, 0.0F});
        ObjectAnimator var5 = ObjectAnimator.ofFloat(this.ivUnlockAnim2, "alpha", new float[]{1.0F, 0.0F});
        ObjectAnimator var6 = ObjectAnimator.ofFloat(this.cirUnlockAnim, "alpha", new float[]{1.0F, 0.0F});
        var1.addListener(new AnimatorListener() {
            public void onAnimationCancel(Animator var1) {
            }

            public void onAnimationEnd(Animator var1) {
                UnlockActivity.this.finish();
            }

            public void onAnimationRepeat(Animator var1) {
            }

            public void onAnimationStart(Animator var1) {
            }
        });
        var1.setDuration((long) this.duration[4]);
        var1.playTogether(new Animator[]{var2, var3, var4, var5, var6});
        var1.start();
    }

    private void unlockAnimList(View var1, int var2, float var3, float var4) {
        float var5 = 265.0F / (float) var2;
        AnimatorSet var6 = new AnimatorSet();
        this.llUnlockEmpty.setAlpha(1.0F);
        ObjectAnimator var7 = ObjectAnimator.ofFloat(var1, "translationX", new float[]{var3, 0.0F});
        ObjectAnimator var12 = ObjectAnimator.ofFloat(var1, "translationY", new float[]{var4, 0.0F});
        ObjectAnimator var8 = ObjectAnimator.ofFloat(this.ivUnlockLogo, "scaleX", new float[]{1.0F, var5});
        ObjectAnimator var9 = ObjectAnimator.ofFloat(this.ivUnlockLogo, "scaleY", new float[]{1.0F, var5});
        ObjectAnimator var10 = ObjectAnimator.ofFloat(this.tvUnlockUserName, "textSize", new float[]{15.66F, 21.33F});
        ValueAnimator var11 = ValueAnimator.ofInt(new int[]{17, 31});
        var11.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator var1) {
                int var2 = Integer.valueOf(var1.getAnimatedValue() + "");
                android.widget.LinearLayout.LayoutParams var3 = (android.widget.LinearLayout.LayoutParams) UnlockActivity.this.tvUnlockUserName.getLayoutParams();
                var3.topMargin = var2;
                UnlockActivity.this.tvUnlockUserName.setLayoutParams(var3);
            }
        });
        var11.setTarget(this.tvUnlockUserName);
        var6.setDuration((long) this.duration[1]);
        var6.play(var7).with(var12).with(var10).with(var11).with(var8).with(var9);
        var6.addListener(new AnimatorListener() {
            public void onAnimationCancel(Animator var1) {
            }

            public void onAnimationEnd(Animator var1) {
                UnlockActivity.this.unlockAnim3();
            }

            public void onAnimationRepeat(Animator var1) {
            }

            public void onAnimationStart(Animator var1) {
            }
        });
        var6.start();
    }


    private void moveUserToCenterAndUnlock(String mac) {
        isUnlocking = true;

        EventBus.getDefault().post(new UnlockSuccessEvent(this.userBean.getMac()));
        int index = 0;
        for (int i = 0; i < userNewList.size(); i++) {
            if (mac.equals(userNewList.get(i).getMac())) {
                index = i;
                break;
            }
        }

        View var4 = this.layoutTransitionAdapter.findViewByPosition(index).findViewById(R.id.iv_user);
        int[] Location = new int[2];
        var4.getLocationOnScreen(Location);
        Log.e(TAG, "setTempView==" + Location[0] + "  " + Location[1]);
        this.setTempView(Location[0], Location[1], var4.getMeasuredHeight());
    }

    private void updateTips(int var1) {
        this.tvTips.setAlpha(1.0F);
        switch (var1) {
            case 0:
                this.tvTips.setText("点击头像，登录账户");
                return;
            case 1:
                this.tvTips.setText("点击自己的头像");
                return;
            case 2:
                this.tvTips.setText("已锁定，点击解锁");
                return;
            case 3:
                this.tvTips.setAlpha(0.0F);
                this.tvTips.setText("点击头像，登录账户");
                return;
            default:
        }
    }

    @Subscribe(
            threadMode = ThreadMode.MAIN
    )
    public void Event(UpdateUnlockStateEvent var1) {
        this.setIntent(var1.getIntent());
        this.initData();
    }

    public void hideNavigationBar() {
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE;
        this.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
    }

    protected void onActivityResult(int var1, int var2, Intent var3) {
        super.onActivityResult(var1, var2, var3);
        switch (var2) {
            case RESULT_OK:
                BraceletPresenter.isDomainlock = false;
                BraceletPresenter.isDomainUnlock = true;
                isUnlocking = true;
                this.updateTips(3);
                if (this.noUser) {
                    BraceletPresenter.updateUser(null, null, 0);
                } else {
                    BraceletUserBean braceletUserBean = this.bleUserModel.getBraceletUserByMac(this.domainUser.getMac());
                    BraceletPresenter.updateUser(braceletUserBean.getMac(), braceletUserBean.getName(), braceletUserBean.getIcon());
                }

                this.userBean.setIcon(this.domainUser.icon);
                Log.e(TAG, "onActivityResult==" + this.domainUser.icon + "  " + this.domainUser.name);
                this.userBean.setName(this.domainUser.name);
                this.domainUser = null;
                this.setTempView(this.domainView[0], this.domainView[1], this.domainView[2]);
                return;
            case RESULT_CANCELED:
                state = oldstate;
                this.flLock.setVisibility(View.VISIBLE);
                this.flDomainUnlock.setVisibility(View.GONE);
                return;
            default:
        }
    }

    public void onClick(View var1) {
        switch (var1.getId()) {
            case R.id.tv_domain_unlock_cancel:
                state = oldstate;
                this.flLock.setVisibility(View.VISIBLE);
                this.flDomainUnlock.setVisibility(View.GONE);
                return;
            case R.id.tv_unlock_pwd:
                this.pwdUnlock();
                return;
            default:
        }
    }

    protected void onCreate(Bundle var1) {
        super.onCreate(var1);
        this.setContentView(R.layout.activity_unlock);
        this.hideNavigationBar();
        this.initView();
        this.initData();
        this.initAction();
        EventBus.getDefault().register(this);
    }

    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        UnlockActivity.state = UNLOCK;
        isUnlocking = false;
        super.onDestroy();
    }

    public void onItemClick(View var1, int var2) {
        isUnlocking = true;
        BraceletPresenter.isDomainlock = false;
        BraceletUserBean braceletUserBean = this.bleUserModel.getBraceletUserByMac(((UnlockUser) var1.getTag()).getMac());
        BraceletPresenter.updateUser(braceletUserBean.getMac(), braceletUserBean.getName(), braceletUserBean.getIcon());
        this.userBean.setIcon(((UnlockUser) var1.getTag()).icon);
        this.userBean.setName(((UnlockUser) var1.getTag()).name);
        var1 = var1.findViewById(R.id.iv_user);
        int[] var5 = new int[2];
        var1.getLocationOnScreen(var5);
        var2 = var5[0];
        int var3 = var5[1];
        Log.e(TAG, "setTempView==" + var2 + "  " + var3);
        this.setTempView(var2, var3, var1.getMeasuredHeight());
    }

    public boolean onKeyDown(int var1, KeyEvent var2) {
        return var1 == 4 ? true : super.onKeyDown(var1, var2);
    }

    protected void onNewIntent(Intent var1) {
        super.onNewIntent(var1);
        this.setIntent(var1);
        this.initData();
    }


}
