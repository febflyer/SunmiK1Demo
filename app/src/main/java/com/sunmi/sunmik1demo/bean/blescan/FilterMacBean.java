package com.sunmi.sunmik1demo.bean.blescan;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by thinkpad on 2018/1/9.
 */

@Entity
public class FilterMacBean {
    @Id(autoincrement = true)
    private Long id;

    private String address;

    private int type;

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FilterMacBean(String address, int type) {
        this.address = address;
        this.type = type;
    }

    @Generated(hash = 1799892514)
    public FilterMacBean(Long id, String address, int type) {
        this.id = id;
        this.address = address;
        this.type = type;
    }

    @Generated(hash = 342532712)
    public FilterMacBean() {
    }


}
