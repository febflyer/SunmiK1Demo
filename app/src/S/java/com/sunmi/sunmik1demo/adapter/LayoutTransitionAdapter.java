//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunmi.sunmik1demo.adapter;

import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.sunmi.sunmik1demo.R;
import com.sunmi.sunmik1demo.adapter.UnlockAllUserAdapter.UnlockUser;

import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;
import java.util.List;

public class LayoutTransitionAdapter {
    private static final String TAG = LayoutTransitionAdapter.class.getSimpleName();
    private int addChangeCount = 0;
    private List<UnlockUser> addIndex = new ArrayList();
    private int addTempChange = 0;
    public int count = 0;
    private long duration = 400L;
    private Handler handler = new Handler(Looper.getMainLooper());
    private List<UnlockUser> list;
    private LayoutTransitionAdapter.OnItemClickListener listener;
    private Context mContext;
    private LayoutTransitionAdapter.OnAnimationListener onAnimationListener;
    private int removeChangeCount = 0;
    private List<UnlockUser> removeIndex = new ArrayList();
    private int removeTempChange = 0;
    private long stagger = 100;
    private ViewGroup viewGroup;

    public LayoutTransitionAdapter(Context var1, ViewGroup var2, List<UnlockUser> var3) {
        this.mContext = var1;
        this.viewGroup = var2;
        this.list = var3;
    }

    private void addMore() {
        if (this.addTempChange == this.addChangeCount) {
            this.addTempChange = 0;
            if (this.onAnimationListener != null) {
                this.handler.postDelayed(new Runnable() {
                    public void run() {
                        onAnimationListener.end(2);
                    }
                }, this.duration);
            }

        } else {
            add(addIndex.get(addTempChange), count);
            addTempChange++;
            this.handler.postDelayed(new Runnable() {
                public void run() {
                    LayoutTransitionAdapter.this.addMore();
                }
            }, stagger);
        }
    }

    private void removeMore() {
        if (this.removeTempChange == this.removeChangeCount) {
            this.removeTempChange = 0;
            if (this.onAnimationListener != null) {
                this.handler.postDelayed(new Runnable() {
                    public void run() {
                        LayoutTransitionAdapter.this.onAnimationListener.end(2);
                    }
                }, this.duration);
            }

        } else {
            this.handler.postDelayed(new Runnable() {
                public void run() {
                    LayoutTransitionAdapter.this.remove((UnlockUser) LayoutTransitionAdapter.this.removeIndex.get(LayoutTransitionAdapter.this.removeTempChange));
                    removeTempChange++;
                    LayoutTransitionAdapter.this.removeMore();
                }
            }, (long) this.stagger);
        }
    }

    public void add(UnlockUser var1, int var2) {
        this.list.add(var2, var1);
        this.onBindView(var2);
    }

    public void addAll(List<UnlockUser> var1) {
        Log.e(TAG, "transitionListener==addAll== " + var1.size() + " " + this.count);
        if (this.onAnimationListener != null) {
            this.onAnimationListener.start(2);
        }

        this.addChangeCount = var1.size();
        this.addTempChange = 0;
        this.addIndex.clear();
        this.addIndex.addAll(var1);

        this.addMore();
    }

    public void clear() {
        this.list.clear();
        this.addChangeCount = 0;
        this.removeChangeCount = 0;
        this.count = 0;
        this.addTempChange = 0;
        this.viewGroup.removeAllViews();
        if (this.onAnimationListener != null) {
            this.onAnimationListener.end(1);
        }

    }

    public View findViewByPosition(int var1) {
        return this.viewGroup.getChildAt(var1);
    }

    public long getStagger() {
        return stagger;
    }

    public LayoutTransition initTransition(ViewGroup var1) {
        LayoutTransition var2 = new LayoutTransition();
        var1.setLayoutTransition(var2);
        return var2;
    }

    public void onBindView(final int var1) {
        View var2 = this.onCreateView(this.viewGroup);
        CircleImageView var3 = (CircleImageView) var2.findViewById(R.id.iv_user);
        TextView var4 = (TextView) var2.findViewById(R.id.tv_user_name);
        var3.setImageResource(((UnlockUser) this.list.get(var1)).getIcon());
        var4.setText(((UnlockUser) this.list.get(var1)).getName());
        var2.setTag(this.list.get(var1));
        if (this.listener != null) {
            var2.setOnClickListener(new OnClickListener() {
                public void onClick(View var1x) {
                    LayoutTransitionAdapter.this.listener.onItemClick(var1x, var1);
                }
            });
        }

        this.viewGroup.addView(var2);
        ++this.count;
    }

    public View onCreateView(ViewGroup var1) {
        View var2 = View.inflate(this.mContext, R.layout.item_unlock_user, (ViewGroup) null);
        var2.setScaleX(0.0F);
        var2.setScaleY(0.0F);
        return var2;
    }

    public void onRemoveView(int var1) {
        if (this.onAnimationListener != null) {
            this.onAnimationListener.start(3);
        }

        if (this.count >= 0 && var1 >= 0 && this.list.size() > 0) {
            this.list.remove(var1);
            this.viewGroup.removeViewAt(var1);
            --this.count;
        }

    }

    public void remove(UnlockUser var1) {
        this.onRemoveView(this.list.indexOf(var1));
    }

    public void removeAll(List<UnlockUser> var1) {
        Log.e(TAG, "transitionListener==removeAll== " + this.list.size());
        if (this.onAnimationListener != null) {
            this.onAnimationListener.start(2);
        }

        this.removeChangeCount = var1.size();
        this.removeIndex.clear();
        this.removeIndex.addAll(var1);
        this.removeTempChange = 0;
        this.remove((UnlockUser) this.removeIndex.get(this.removeTempChange));
        ++this.removeTempChange;
        this.removeMore();
    }

    public void setDuration(long var1) {
        this.duration = var1;
    }

    public void setOnAnimationListener(LayoutTransitionAdapter.OnAnimationListener var1) {
        this.onAnimationListener = var1;
    }

    public void setOnItemClickListener(LayoutTransitionAdapter.OnItemClickListener var1) {
        this.listener = var1;
    }

    public void setStagger(int var1) {
        this.stagger = var1;
    }

    public void setTransitioner(LayoutTransition var1) {
        var1.setDuration(this.duration);
        AnimatorSet var2 = new AnimatorSet();
        ObjectAnimator var3 = ObjectAnimator.ofFloat((Object) null, "scaleX", new float[]{0.0F, 1.0F});
        ObjectAnimator var4 = ObjectAnimator.ofFloat((Object) null, "scaleY", new float[]{0.0F, 1.0F});
        var2.play(var3).with(var4);
        var1.setAnimator(LayoutTransition.APPEARING, var2);
        var2 = new AnimatorSet();
        var3 = ObjectAnimator.ofFloat((Object) null, "scaleX", new float[]{1.0F, 0.0F});
        var4 = ObjectAnimator.ofFloat((Object) null, "scaleY", new float[]{1.0F, 0.0F});
        var2.play(var3).with(var4);
        var1.setAnimator(LayoutTransition.DISAPPEARING, var2);
    }

    public interface OnAnimationListener {
        void end(int var1);

        void start(int var1);
    }

    public interface OnItemClickListener {
        void onItemClick(View var1, int var2);
    }
}
