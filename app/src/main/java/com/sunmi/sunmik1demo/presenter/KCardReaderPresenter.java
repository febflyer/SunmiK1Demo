package com.sunmi.sunmik1demo.presenter;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.sunmi.idcardservice.CardCallback;
import com.sunmi.idcardservice.IDCardInfo;
import com.sunmi.idcardservice.IDCardServiceAidl;
import com.sunmi.idcardservice.MiFareCardAidl;
import com.sunmi.sunmik1demo.utils.ByteUtils;

/**
 * Created by mayflower on 2019/6/20
 * address :jiangli@sunmi.com
 * description :非接和身份证阅读器
 */
public class KCardReaderPresenter {
    private Context context;
    private static final String TAG = "CardReaderPresenter";
    private IDCardServiceAidl mIDCard;
    private MiFareCardAidl mMiFareCard;
    private boolean threadStatus = false;       //线程状态，为了安全终止线程

    public KCardReaderPresenter(Context context,IDCardServiceAidl idCardServiceAidl,MiFareCardAidl miFareCardAidl){
        this.context = context;
        this.mIDCard = idCardServiceAidl;
        this.mMiFareCard = miFareCardAidl;
    }

    private class ReadThread extends Thread{

        @Override
        public void run(){
            super.run();
            //身份证
            IDCardInfo info = null;
            //非接
            int ret = 0;
            byte[] snr = new byte[10];      //卡片UID
            byte[] cardtype = new byte[8];  //卡片类型，0AH  TypeA卡;  0BH  TypeB卡
            int mode = 0;       //认证模式 0 KEYA 模式 1 KEYB 模式
            int addr = 0;       //绝对地址 = 扇区号 * 4 + 块地址，地址0是厂家信息，已固化只读
            byte[] key = new byte[10];      //默认密码：FFFFFFFFFFFF，这几个如果想要设定，可以放到成员属性
            for(int i=0;i<6;i++){ key[i] = -1;}
            key[6] = 0;
            byte[] rdata = new byte[32];    //读卡数据

            while (threadStatus){
                //身份证
                try {
                    info = mIDCard.readCard();
                    if(threadStatus) {      //防止读卡中途退出了
                        if(info != null)
                            CardCallback.onGetIDCardData(info, 10);
                        else
                            CardCallback.onGetIDCardData(info, -10);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                //非接
                try {
                    //寻卡，支持的卡片都能成功
                    ret = mMiFareCard.rfCard(cardtype, snr);
                    if(ret != 0){ continue; }
                    //卡片UID
                    if(threadStatus)
                        CardCallback.onGetMiCardData(ByteUtils.bytes2HexStr(snr,0,snr.length), 10);

                    //认证，密码正确才能成功认证
                    ret = mMiFareCard.rfAuthEntication(mode,addr,key);
                    if(ret != 0){ continue;}

                    //读卡，认证成功后才能成功读卡
                    ret = mMiFareCard.rfRead(addr,rdata);
                    if(ret != 0){ continue;}
                    //绝对地址读到的数据
                    if(threadStatus) {
                        Log.d(TAG,"data[bytes2str]:" + ByteUtils.bytes2Str(rdata,0,rdata.length));
                        CardCallback.onGetMiCardData(ByteUtils.bytes2HexStr(rdata, 0, rdata.length), 10);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static interface CardCallback{
        public void onGetMiCardData(String data, int code);
        public void onGetIDCardData(IDCardInfo data, int code);
    }

    private CardCallback CardCallback = null;

    public void readCardAuto(CardCallback cardDataListener){
        if(threadStatus)
            return;
        threadStatus = true;   //表示线程起来了
        new ReadThread().start();

        CardCallback = cardDataListener;
    }

    public void cancelAutoReading(){
        threadStatus = false;
    }
}
