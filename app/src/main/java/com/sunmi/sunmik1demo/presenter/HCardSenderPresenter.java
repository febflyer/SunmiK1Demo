package com.sunmi.sunmik1demo.presenter;

import android.content.Context;
import android.os.RemoteException;

import com.sunmi.mifarecard.IMifareCard;
import com.sunmi.sunmik1demo.utils.ByteUtils;

/**
* created by mayflower on 19/07/30
* for cardSender on H1
* get me at jiangli@sunmi.com
**/
public class HCardSenderPresenter {
    private static final String TAG = "HCardSenderPresenter";

    private Context context;
    private IMifareCard mCardSender;

    //
    private static final String CMD_RS = "RS";
    private static final String CMD_DC = "DC";
    private static final String CMD_CP = "CP";

    public HCardSenderPresenter(Context context, IMifareCard iMifareCardService){
        this.context = context;
        this.mCardSender = iMifareCardService;
    }

    public void autoRunCard(){
        try {
            mCardSender.sendCmd(CMD_RS.getBytes(),2 );
//            mCardSender.sendCmd(CMD_DC.getBytes(),2 );
//            mCardSender.sendCmd(CMD_CP.getBytes(),2 );
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
