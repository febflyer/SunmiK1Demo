//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunmi.sunmik1demo.adapter;

import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.sunmi.sunmik1demo.R;

import de.hdodenhof.circleimageview.CircleImageView;
import java.util.List;
import java.util.Objects;

public class UnlockAllUserAdapter extends Adapter<ViewHolder> {
    private List<UnlockAllUserAdapter.UnlockUser> list;
    private UnlockAllUserAdapter.OnItemClickListener listener;

    public UnlockAllUserAdapter(List<UnlockAllUserAdapter.UnlockUser> var1) {
        this.list = var1;
    }

    public int getItemCount() {
        return this.list.size();
    }

    public void onBindViewHolder(ViewHolder var1, final int var2) {
        CircleImageView var3 = (CircleImageView)var1.itemView.findViewById(R.id.iv_user);
        TextView var4 = (TextView)var1.itemView.findViewById(R.id.tv_user_name);
        var3.setImageResource(((UnlockAllUserAdapter.UnlockUser)this.list.get(var2)).getIcon());
        var4.setText(((UnlockAllUserAdapter.UnlockUser)this.list.get(var2)).getName());
        if (this.listener != null) {
            var1.itemView.setOnClickListener(new OnClickListener() {
                public void onClick(View var1) {
                    UnlockAllUserAdapter.this.listener.onItemClick(var1, var2);
                }
            });
        }

    }

    public ViewHolder onCreateViewHolder(ViewGroup var1, int var2) {
        return new ViewHolder(LayoutInflater.from(var1.getContext()).inflate(R.layout.item_unlock_alluser, var1, false)) {
        };
    }

    public void setOnItemClickListener(UnlockAllUserAdapter.OnItemClickListener var1) {
        this.listener = var1;
    }

    public interface OnItemClickListener {
        void onItemClick(View var1, int var2);
    }

    public static class UnlockUser {
        public int icon;
        public String mac;
        public String name;

        public UnlockUser() {
        }

        public boolean equals(Object var1) {
            if (this != var1) {
                if (var1 != null && this.getClass() == var1.getClass()) {
                    UnlockAllUserAdapter.UnlockUser var2 = (UnlockAllUserAdapter.UnlockUser)var1;
                    if (this.icon == var2.icon && Objects.equals(this.name, var2.name) && Objects.equals(this.mac, var2.mac)) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }

        public int getIcon() {
            return this.icon;
        }

        public String getMac() {
            return this.mac;
        }

        public String getName() {
            return this.name;
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.name, this.icon, this.mac});
        }

        public void setIcon(int var1) {
            this.icon = var1;
        }

        public void setMac(String var1) {
            this.mac = var1;
        }

        public void setName(String var1) {
            this.name = var1;
        }
    }
}
