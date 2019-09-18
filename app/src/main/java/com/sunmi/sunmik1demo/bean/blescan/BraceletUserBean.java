package com.sunmi.sunmik1demo.bean.blescan;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class BraceletUserBean {
    private int icon;

    @Id(autoincrement = true)
    private Long id;
    private String mac;
    private String name;
    private int singValue;
    public BraceletUserBean(String name, String mac, int icon) {
        this.icon = icon;
        this.mac = mac;
        this.name = name;
    }

    @Generated(hash = 274388349)
    public BraceletUserBean(int icon, Long id, String mac, String name,
            int singValue) {
        this.icon = icon;
        this.id = id;
        this.mac = mac;
        this.name = name;
        this.singValue = singValue;
    }
    @Generated(hash = 1010645949)
    public BraceletUserBean() {
    }
    public int getIcon() {
        return this.icon;
    }
    public void setIcon(int icon) {
        this.icon = icon;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getMac() {
        return this.mac;
    }
    public void setMac(String mac) {
        this.mac = mac;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getSingValue() {
        return this.singValue;
    }
    public void setSingValue(int singValue) {
        this.singValue = singValue;
    }

   
}
