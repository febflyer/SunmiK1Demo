//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunmi.sunmik1demo.eventbus;

public class UnlockSingValueCalEvent {
    private String address;
    private int singValue;

    public UnlockSingValueCalEvent(int var1, String var2) {
        this.singValue = var1;
        this.address = var2;
    }

    public String getAddress() {
        return this.address;
    }

    public int getSingValue() {
        return this.singValue;
    }

    public void setSingValue(int var1) {
        this.singValue = var1;
    }
}
