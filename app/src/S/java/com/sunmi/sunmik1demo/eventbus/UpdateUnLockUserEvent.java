//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunmi.sunmik1demo.eventbus;

public class UpdateUnLockUserEvent {
    int icon;
    String name;
    boolean showAnim;
    String user;

    public UpdateUnLockUserEvent(String var1, int var2, String var3, boolean var4) {
        this.user = var1;
        this.icon = var2;
        this.name = var3;
        this.showAnim = var4;
    }

    public int getIcon() {
        return this.icon;
    }

    public String getName() {
        return this.name;
    }

    public String getUser() {
        return this.user;
    }

    public boolean isShowAnim() {
        return this.showAnim;
    }

    public void setIcon(int var1) {
        this.icon = var1;
    }

    public void setName(String var1) {
        this.name = var1;
    }

    public void setShowAnim(boolean var1) {
        this.showAnim = var1;
    }

    public void setUser(String var1) {
        this.user = var1;
    }
}
