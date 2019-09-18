// idcardServiceAidl.aidl
package com.sunmi.idcardservice;

// Declare any non-default types here with import statements
import com.sunmi.idcardservice.CardCallback;
import com.sunmi.idcardservice.IDCardInfo;
import com.sunmi.idcardservice.MiFareCardAidl;

interface IDCardServiceAidl {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     *
     */

     IDCardInfo readCard();

     void readCardAuto(in CardCallback callback);

     void cancelAutoReading();

     MiFareCardAidl getMiFareCardService();
}
