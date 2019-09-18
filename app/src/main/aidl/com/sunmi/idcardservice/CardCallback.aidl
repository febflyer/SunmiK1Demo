// CardCallback.aidl
package com.sunmi.idcardservice;

// Declare any non-default types here with import statements

import com.sunmi.idcardservice.IDCardInfo;

interface CardCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void getCardData(inout IDCardInfo info,int code);
}
