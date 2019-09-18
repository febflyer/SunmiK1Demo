//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunmi.sunmik1demo.eventbus;

import android.content.Intent;

public class UpdateUnlockStateEvent {
    private Intent intent;

    public UpdateUnlockStateEvent(Intent var1) {
        this.intent = var1;
    }

    public Intent getIntent() {
        return this.intent;
    }

    public void setIntent(Intent var1) {
        this.intent = var1;
    }
}
