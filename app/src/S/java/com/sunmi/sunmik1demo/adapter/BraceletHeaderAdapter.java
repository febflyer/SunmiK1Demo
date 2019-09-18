//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunmi.sunmik1demo.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.sunmi.sunmik1demo.R;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import de.hdodenhof.circleimageview.CircleImageView;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BraceletHeaderAdapter extends Adapter<ViewHolder> implements StickyRecyclerHeadersAdapter<ViewHolder> {
    private List<BraceletHeaderAdapter.MyBluetoothDevice> items;
    private BraceletHeaderAdapter.OnAddListener listener;

    public BraceletHeaderAdapter(List<BraceletHeaderAdapter.MyBluetoothDevice> var1) {
        this.items = var1;
        this.setHasStableIds(true);
    }

    public void add(int var1, BraceletHeaderAdapter.MyBluetoothDevice var2) {
        this.items.add(var1, var2);
        this.notifyDataSetChanged();
    }

    public void add(BraceletHeaderAdapter.MyBluetoothDevice var1) {
        this.items.add(var1);
        this.notifyDataSetChanged();
    }

    public void addAll(Collection<? extends BraceletHeaderAdapter.MyBluetoothDevice> var1) {
        if (var1 != null) {
            this.items.addAll(var1);
            this.notifyDataSetChanged();
        }

    }

    public void addAll(BraceletHeaderAdapter.MyBluetoothDevice... var1) {
        this.addAll((Collection) Arrays.asList(var1));
    }

    public void clear() {
        this.items.clear();
        this.notifyDataSetChanged();
    }

    public long getHeaderId(int var1) {
        return this.getItem(var1).isAdd() ? 0L : 1L;
    }

    public BraceletHeaderAdapter.MyBluetoothDevice getItem(int var1) {
        return (BraceletHeaderAdapter.MyBluetoothDevice) this.items.get(var1);
    }

    public int getItemCount() {
        return this.items.size();
    }

    public long getItemId(int var1) {
        return (long) this.getItem(var1).hashCode();
    }

    public void onBindHeaderViewHolder(ViewHolder var1, int var2) {
        TextView var3 = (TextView) var1.itemView;
        if (this.getItem(var2).isAdd()) {
            var3.setText(R.string.more_bracelet_connected_devices);
            var3.setTextColor(Color.parseColor("#FF000000"));
        } else {
            var3.setText(R.string.more_bracelet_connect_devices);
            var3.setTextColor(Color.parseColor("#FFFF6900"));
        }
    }

    public void onBindViewHolder(final ViewHolder var1, int var2) {
        ((TextView) var1.itemView.findViewById(R.id.tv_ble_name)).setText(this.getItem(var2).getName());
        ((TextView) var1.itemView.findViewById(R.id.tv_ble_mac)).setText(this.getItem(var2).getAddress());
        if (this.getItem(var2).isCheck()) {
            var1.itemView.setBackgroundColor(Color.parseColor("#FFF6F7F9"));
        } else {
            var1.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        Button var3 = (Button) var1.itemView.findViewById(R.id.btn_ble_connect);
        var3.setTag(var2);
        if (this.getItem(var2).isAdd()) {
            var3.setText(R.string.more_bracelet_remove);
            var3.setBackgroundResource(R.drawable.more_btn_normal_bg);
            var3.setTextColor(Color.parseColor("#99000000"));
        } else {
            var3.setText(R.string.more_bracelet_add);
            var3.setBackgroundResource(R.drawable.more_btn_click_bg);
            var3.setTextColor(Color.parseColor("#FFFF6000"));
        }

        CircleImageView var4 = (CircleImageView) var1.itemView.findViewById(R.id.civ_head_icon);
        if (this.getItem(var2).getIcon() != 0) {
            var4.setImageResource(this.getItem(var2).getIcon());
        } else {
            var4.setImageResource(R.drawable.more_bracelet_default_icon_1);
        }

        var3.setOnClickListener(new OnClickListener() {
            public void onClick(View var1x) {
                BraceletHeaderAdapter.this.listener.onClick(var1.getLayoutPosition(), BraceletHeaderAdapter.this.getItem(var1.getLayoutPosition()).isAdd());
            }
        });
        var1.itemView.setOnClickListener(new OnClickListener() {
            public void onClick(View var1x) {
                BraceletHeaderAdapter.this.listener.onItemClick(var1.getLayoutPosition());
            }
        });
    }

    public ViewHolder onCreateHeaderViewHolder(ViewGroup var1) {
        return new ViewHolder(LayoutInflater.from(var1.getContext()).inflate(R.layout.header_bracelet, var1, false)) {
        };
    }

    public ViewHolder onCreateViewHolder(ViewGroup var1, int var2) {
        return new ViewHolder(LayoutInflater.from(var1.getContext()).inflate(R.layout.item_bracelet, var1, false)) {
        };
    }

    public void remove(BraceletHeaderAdapter.MyBluetoothDevice var1) {
        this.items.remove(var1);
        this.notifyDataSetChanged();
    }

    public void setOnAddClickListener(@NonNull BraceletHeaderAdapter.OnAddListener var1) {
        this.listener = var1;
    }

    public static class MyBluetoothDevice {
        private String address;
        private int icon;
        private boolean isAdd;
        private boolean isCheck;
        private String name;

        public MyBluetoothDevice() {
        }

        public MyBluetoothDevice(String var1, boolean var2) {
            this.address = var1;
            this.isAdd = var2;
        }

        public String getAddress() {
            return this.address;
        }

        public int getIcon() {
            return this.icon;
        }

        public String getName() {
            return this.name;
        }

        public boolean isAdd() {
            return this.isAdd;
        }

        public boolean isCheck() {
            return this.isCheck;
        }

        public void setAdd(boolean var1) {
            this.isAdd = var1;
        }

        public void setAddress(String var1) {
            this.address = var1;
        }

        public void setCheck(boolean var1) {
            this.isCheck = var1;
        }

        public void setIcon(int var1) {
            this.icon = var1;
        }

        public void setName(String var1) {
            this.name = var1;
        }
    }

    public interface OnAddListener {
        void onClick(int var1, boolean var2);

        void onItemClick(int var1);
    }
}
