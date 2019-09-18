package com.sunmi.sunmik1demo.fragment;

import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sunmi.idcardservice.CardCallback;
import com.sunmi.idcardservice.IDCardInfo;
import com.sunmi.sunmik1demo.BaseFragment;
import com.sunmi.sunmik1demo.presenter.CardReaderPresenter;
import com.sunmi.sunmik1demo.presenter.KCardReaderPresenter;
import com.sunmi.sunmik1demo.presenter.ScalePresenter;
import com.sunmi.sunmik1demo.ui.MainActivity;
import com.sunmi.sunmik1demo.ui.MoreActivity;
import com.sunmi.sunmik1demo.R;
import com.sunmi.sunmik1demo.utils.ResourcesUtils;

public class DeviceFragment extends BaseFragment implements View.OnClickListener{

    private static final String TAG = "DeviceFragment";

    private Button mBtnNet;
    private Button mBtnTare;

    private Button showText;
    private Button print;

    private Button btnReadCard;

    private TextView tvNote;
    private String divide = "------------------------------------------------------------------------ end ------------------------------------------------------------------------";
    private Button btnScan;
    private ScrollView scrollView;

    public boolean isShowScanResult = false;        //扫码器扫码
    public boolean isShowReadResult = false;       //非接和身份证读卡,这个暂时没用到

    @Override
    protected int setView() {
        return R.layout.fragment_device;
    }

    @Override
    protected void init(View view) {
        mBtnNet = view.findViewById(R.id.btn_net);
        mBtnTare = view.findViewById(R.id.btn_tare);
        print = (Button) view.findViewById(R.id.btn_print);
        showText = (Button) view.findViewById(R.id.btn_show_video);
        tvNote = (TextView) view.findViewById(R.id.tv_note);
        btnScan = (Button) view.findViewById(R.id.btn_scan);
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        view.findViewById(R.id.btn_clear).setOnClickListener(this);
        if(!Build.MODEL.contains("S2")){
            mBtnNet.setVisibility(View.GONE);
            mBtnTare.setVisibility(View.GONE);
            view.findViewById(R.id.tv_more_scale).setVisibility(View.GONE);
            view.findViewById(R.id.btn_clear).setVisibility(View.GONE);
        }

        btnReadCard = view.findViewById(R.id.btn_read_card);
        if(false){ btnReadCard.setVisibility(View.GONE); }

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        divide = ResourcesUtils.getString(getContext(), R.string.divide);
        mBtnNet.setOnClickListener(this);
        mBtnTare.setOnClickListener(this);
        print.setOnClickListener(this);
        showText.setOnClickListener(this);
        btnScan.setOnClickListener(this);

        btnReadCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_show_video:
                append(ResourcesUtils.getString(getContext(), R.string.more_show_thevideo) + "\n\n" + divide + "\n\n");
                Log.d("SUNMI", "onClick: ---------->" + ((MoreActivity)getActivity()).videoDisplay.isShowing());
                ((MoreActivity)getActivity()).videoDisplay.show();
                Log.d("SUNMI", "onClick: ------------>playing");
                break;
            case R.id.btn_print:
                append(ResourcesUtils.getString(getContext(), R.string.more_print) + "\n\n" + divide + "\n\n");
                if (MainActivity.kPrinterPresenter!=null) {
                        MainActivity.kPrinterPresenter.print(null, -99);
                }

                break;
            case R.id.btn_scan:     //数据监听在MoreActivity中
                isShowScanResult = true;
                append(ResourcesUtils.getString(getContext(), R.string.more_camera_qrcode) + "\n\n" + divide + "\n\n");
                break;

            case R.id.btn_net:
                append(ResourcesUtils.getString(getContext(), R.string.more_get_weight_result) + ":" + ScalePresenter.net + "\n\n" + divide + "\n\n");
                break;
            case R.id.btn_clear:
                try {
                    if (((MoreActivity)getActivity()).mScaleManager != null) {
                        ((MoreActivity)getActivity()).mScaleManager.zero();
                    }
                    append(ResourcesUtils.getString(getContext(), R.string.more_clear) + "：" + "\n\n" + divide + "\n\n");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_tare:

                try {
                    if (((MoreActivity)getActivity()).mScaleManager != null) {
                        ((MoreActivity)getActivity()).mScaleManager.tare();
                    }
                    append(ResourcesUtils.getString(getContext(), R.string.more_peeled) + "：" + "\n\n" + divide + "\n\n");
                } catch (RemoteException e) {
                    e.printStackTrace();
                    append(ResourcesUtils.getString(getContext(), R.string.more_unpeeled) + "：" + e.getMessage() + "\n\n" + divide + "\n\n");
                }
                break;
            case R.id.btn_read_card:
                isShowReadResult = true;
                append(ResourcesUtils.getString(getContext(), R.string.more_read_card) + ":" + "\n\n" + divide + "\n\n");

                if (MainActivity.kCardReaderPresenter == null)
                    break;
                MainActivity.kCardReaderPresenter.readCardAuto(new KCardReaderPresenter.CardCallback() {
                    @Override
                    public void onGetIDCardData(IDCardInfo data, int code) {
                        if(code == 10){
                            Log.i(TAG, "onGetIDCardData:" + data.toString());
                            append("[" + data.getName() + "]欢迎你！" + "\n\n");
                        }
                        else if(code == -10){
                            Log.i(TAG, "onGetIDCardData:error get data failed. ");
                        }
                        else {
                            Log.i(TAG, "onGetIDCardData:error unknown. ");
                        }
                    }

                    @Override
                    public void onGetMiCardData(String data, int code) {
                        if(code == 10){
                            Log.i(TAG, "onGetMiCardData:" + data.toString());
                            append("[" + data + "]也欢迎你ya！" + "\n\n");
                        }
                    }
                });
//                try {     //也可以直接用服务的readCardAuto
//                    MainActivity.mIdCardService.readCardAuto(new CardCallback.Stub() {
//                        @Override
//                        public void getCardData(IDCardInfo info, int code) throws RemoteException {
//                            if(code == 10){
//                                Log.i(TAG, "getCardData:" + info.toString());
//                                append(info.getName() + "欢迎你！" + "\n\n");
//                            }
//                            else if(code == -10){
//                                Log.i(TAG, "getCardData:error get data failed. ");
//                            }
//                            else {
//                                Log.i(TAG, "getCardData:error unknown. ");
//                            }
//                        }
//                    });
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
    //百度小票
    public static byte[] getBaiduTestBytes() {
        return new byte[]{
                0x1b, 0x40, 0x1b, 0x4d, 0x00, 0x1b, 0x61, 0x00, 0x1d, 0x21, 0x11, 0x1b, 0x45, 0x00, 0x1b, 0x47, 0x00, 0x1b, 0x61, 0x00, 0x1b, 0x45, 0x01, 0x1b, 0x47, 0x01, (byte) 0xb1, (byte) 0xbe
                , (byte) 0xb5, (byte) 0xea, (byte) 0xc1, (byte) 0xf4, (byte) 0xb4, (byte) 0xe6, 0x0a, 0x1b, 0x4d, 0x00, 0x1b, 0x61, 0x00, 0x1d, 0x21, 0x00, 0x1b, 0x45, 0x00, 0x1b, 0x47, 0x00, 0x1b, 0x61, 0x00, 0x2a, 0x2a, 0x2a
                , 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a
                , 0x2a, 0x0a
                , 0x1b, 0x40, 0x1b, 0x4d, 0x00, 0x1b, 0x61, 0x00, 0x1d, 0x21, 0x11, 0x1b, 0x45, 0x00, 0x1b, 0x47, 0x00, 0x1b, 0x61, 0x00, 0x1b, 0x45, 0x01, 0x1b, 0x47, 0x01, 0x1b, 0x61
                , 0x01, 0x23, 0x31, 0x35, 0x20, (byte) 0xb0, (byte) 0xd9, (byte) 0xb6, (byte) 0xc8, (byte) 0xcd, (byte) 0xe2, (byte) 0xc2, (byte) 0xf4, 0x0a, 0x5b, (byte) 0xbb, (byte) 0xf5, (byte) 0xb5, (byte) 0xbd, (byte) 0xb8, (byte) 0xb6, (byte) 0xbf, (byte) 0xee, 0x5d, 0x0a, 0x1b, 0x4d, 0x00
                , 0x1b, 0x61, 0x00, 0x1d, 0x21, 0x00, 0x1b, 0x45, 0x00, 0x1b, 0x47, 0x00, 0x1b, 0x61, 0x00, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a
                , 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x0a
                , 0x1b, 0x40, 0x1b, 0x4d, 0x00, 0x1b, 0x61, 0x00, 0x1d, 0x21, 0x01, 0x1b, 0x45, 0x00, 0x1b, 0x47, 0x00, 0x1b, 0x61, 0x00, (byte) 0xc6, (byte) 0xda, (byte) 0xcd, (byte) 0xfb, (byte) 0xcb, (byte) 0xcd, (byte) 0xb4, (byte) 0xef
                , (byte) 0xca, (byte) 0xb1, (byte) 0xbc, (byte) 0xe4, (byte) 0xa3, (byte) 0xba, (byte) 0xc1, (byte) 0xa2, (byte) 0xbc, (byte) 0xb4, (byte) 0xc5, (byte) 0xe4, (byte) 0xcb, (byte) 0xcd, 0x0a, (byte) 0xb6, (byte) 0xa9, (byte) 0xb5, (byte) 0xa5, (byte) 0xb1, (byte) 0xb8, (byte) 0xd7, (byte) 0xa2, (byte) 0xa3, (byte) 0xba, (byte) 0xc7, (byte) 0xeb, (byte) 0xcb
                , (byte) 0xcd, (byte) 0xb5, (byte) 0xbd, (byte) 0xbf, (byte) 0xfc, (byte) 0xbf, (byte) 0xc6, (byte) 0xce, (byte) 0xf7, (byte) 0xc3, (byte) 0xc5, 0x2c, (byte) 0xb2, (byte) 0xbb, (byte) 0xd2, (byte) 0xaa, (byte) 0xc0, (byte) 0xb1, 0x0a, (byte) 0xb7, (byte) 0xa2, (byte) 0xc6, (byte) 0xb1, (byte) 0xd0, (byte) 0xc5, (byte) 0xcf, (byte) 0xa2, (byte) 0xa3
                , (byte) 0xba, (byte) 0xb0, (byte) 0xd9, (byte) 0xb6, (byte) 0xc8, (byte) 0xcd, (byte) 0xe2, (byte) 0xc2, (byte) 0xf4, (byte) 0xb7, (byte) 0xa2, (byte) 0xc6, (byte) 0xb1, 0x0a, 0x1b, 0x4d, 0x00, 0x1b, 0x61, 0x00, 0x1d, 0x21, 0x00, 0x1b, 0x45, 0x00, 0x1b, 0x47
                , 0x00, 0x1b, 0x61, 0x00, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a
                , 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x0a
                , 0x1b, 0x40, 0x1b, 0x4d, 0x00, 0x1b, 0x61, 0x00, 0x1d, 0x21, 0x00, 0x1b, 0x45, 0x00, 0x1b, 0x47, 0x00, 0x1b, 0x61, 0x00, (byte) 0xb6, (byte) 0xa9, (byte) 0xb5, (byte) 0xa5, (byte) 0xb1, (byte) 0xe0, (byte) 0xba, (byte) 0xc5
                , (byte) 0xa3, (byte) 0xba, 0x31, 0x34, 0x31, 0x38, 0x37, 0x31, 0x38, 0x36, 0x39, 0x31, 0x31, 0x36, 0x38, 0x39, 0x0a, (byte) 0xcf, (byte) 0xc2, (byte) 0xb5, (byte) 0xa5, (byte) 0xca, (byte) 0xb1, (byte) 0xbc, (byte) 0xe4, (byte) 0xa3, (byte) 0xba, 0x32
                , 0x30, 0x31, 0x34, 0x2d, 0x31, 0x32, 0x2d, 0x31, 0x36, 0x20, 0x31, 0x36, 0x3a, 0x33, 0x31, 0x0a, 0x1b, 0x4d, 0x00, 0x1b, 0x61, 0x00, 0x1d, 0x21, 0x00, 0x1b, 0x45, 0x00
                , 0x1b, 0x47, 0x00, 0x1b, 0x61, 0x00, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a
                , 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x0a
                , 0x1b, 0x40, 0x1b, 0x4d, 0x00, 0x1b, 0x61, 0x00, 0x1d, 0x21, 0x01, 0x1b, 0x45, 0x00, 0x1b, 0x47, 0x00, 0x1b, 0x61, 0x00, (byte) 0xb2, (byte) 0xcb, (byte) 0xc6, (byte) 0xb7, (byte) 0xc3, (byte) 0xfb, (byte) 0xb3, (byte) 0xc6
                , 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, (byte) 0xca, (byte) 0xfd, (byte) 0xc1, (byte) 0xbf, 0x20, 0x20, 0x20, 0x20, 0x20, (byte) 0xbd, (byte) 0xf0, (byte) 0xb6, (byte) 0xee, 0x0a, 0x1b, 0x4d, 0x00, 0x1b, 0x61
                , 0x00, 0x1d, 0x21, 0x00, 0x1b, 0x45, 0x00, 0x1b, 0x47, 0x00, 0x1b, 0x61, 0x00, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d
                , 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x0a, 0x1b, 0x4d, 0x00, 0x1b, 0x61, 0x00, 0x1d, 0x21, 0x01, 0x1b
                , 0x45, 0x00, 0x1b, 0x47, 0x00, 0x1b, 0x61, 0x00, (byte) 0xcf, (byte) 0xe3, (byte) 0xc0, (byte) 0xb1, (byte) 0xc3, (byte) 0xe6, (byte) 0xcc, (byte) 0xd7, (byte) 0xb2, (byte) 0xcd, 0x1b, 0x24, (byte) 0xf2, 0x00, 0x31, 0x1b, 0x24, 0x25, 0x01, (byte) 0xa3
                , (byte) 0xa4, 0x34, 0x30, 0x2e, 0x30, 0x30, 0x0a, 0x1b, 0x4d, 0x00, 0x1b, 0x61, 0x00, 0x1d, 0x21, 0x00, 0x1b, 0x45, 0x00, 0x1b, 0x47, 0x00, 0x1b, 0x61, 0x00, 0x1b, 0x4d, 0x00
                , 0x1b, 0x61, 0x00, 0x1d, 0x21, 0x00, 0x1b, 0x45, 0x00, 0x1b, 0x47, 0x00, 0x1b, 0x61, 0x00, 0x1b, 0x4d, 0x00, 0x1b, 0x61, 0x00, 0x1d, 0x21, 0x01, 0x1b, 0x45, 0x00, 0x1b
                , 0x47, 0x00, 0x1b, 0x61, 0x00, (byte) 0xcb, (byte) 0xd8, (byte) 0xca, (byte) 0xb3, (byte) 0xcc, (byte) 0xec, (byte) 0xcf, (byte) 0xc2, (byte) 0xba, (byte) 0xba, (byte) 0xb1, (byte) 0xa4, 0x1b, 0x24, (byte) 0xf2, 0x00, 0x31, 0x1b, 0x24, 0x25, 0x01, (byte) 0xa3, (byte) 0xa4
                , 0x33, 0x38, 0x2e, 0x30, 0x30, 0x0a, 0x1b, 0x4d, 0x00, 0x1b, 0x61, 0x00, 0x1d, 0x21, 0x00, 0x1b, 0x45, 0x00, 0x1b, 0x47, 0x00, 0x1b, 0x61, 0x00, 0x1b, 0x4d, 0x00, 0x1b
                , 0x61, 0x00, 0x1d, 0x21, 0x00, 0x1b, 0x45, 0x00, 0x1b, 0x47, 0x00, 0x1b, 0x61, 0x00
                , 0x1b, 0x40, 0x1b, 0x4d, 0x00, 0x1b, 0x61, 0x00, 0x1d, 0x21, 0x00, 0x1b, 0x45, 0x00, 0x1b, 0x47, 0x00, 0x1b, 0x61, 0x00, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d
                , 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x0a
                , 0x1b, 0x40, 0x1b, 0x4d, 0x00, 0x1b, 0x61, 0x00, 0x1d, 0x21, 0x00, 0x1b, 0x45, 0x00, 0x1b, 0x47, 0x00, 0x1b, 0x61, 0x00, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a
                , 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x0a, 0x1b, 0x4d, 0x00
                , 0x1b, 0x61, 0x00, 0x1d, 0x21, 0x01, 0x1b, 0x45, 0x00, 0x1b, 0x47, 0x00, 0x1b, 0x61, 0x00, (byte) 0xd0, (byte) 0xd5, (byte) 0xc3, (byte) 0xfb, (byte) 0xa3, (byte) 0xba, (byte) 0xb0, (byte) 0xd9, (byte) 0xb6, (byte) 0xc8, (byte) 0xb2, (byte) 0xe2, (byte) 0xca
                , (byte) 0xd4, 0x0a, (byte) 0xb5, (byte) 0xd8, (byte) 0xd6, (byte) 0xb7, (byte) 0xa3, (byte) 0xba, (byte) 0xbf, (byte) 0xfc, (byte) 0xbf, (byte) 0xc6, (byte) 0xbf, (byte) 0xc6, (byte) 0xbc, (byte) 0xbc, (byte) 0xb4, (byte) 0xf3, (byte) 0xcf, (byte) 0xc3, 0x0a, (byte) 0xb5, (byte) 0xe7, (byte) 0xbb, (byte) 0xb0, (byte) 0xa3, (byte) 0xba, 0x31
                , 0x38, 0x37, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x0a
                , 0x1b, 0x40, 0x1b, 0x4d, 0x00, 0x1b, 0x61, 0x00, 0x1d, 0x21, 0x00, 0x1b, 0x45, 0x00, 0x1b, 0x47, 0x00, 0x1b, 0x61, 0x00, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a
                , 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x0a, (byte) 0xb0, (byte) 0xd9, (byte) 0xb6
                , (byte) 0xc8, (byte) 0xb2, (byte) 0xe2, (byte) 0xca, (byte) 0xd4, (byte) 0xc9, (byte) 0xcc, (byte) 0xbb, (byte) 0xa7, 0x0a, 0x31, 0x38, 0x37, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x0a, 0x1b, 0x4d, 0x00, 0x1b, 0x61, 0x00, 0x1d
                , 0x21, 0x00, 0x1b, 0x45, 0x00, 0x1b, 0x47, 0x00, 0x1b, 0x61, 0x00, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a
                , 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x2a, 0x0a, 0x1b, 0x4d, 0x00, 0x1b, 0x61, 0x00, 0x1d, 0x21, 0x00, 0x1b, 0x45, 0x00
                , 0x1b, 0x47, 0x00, 0x1b, 0x61, 0x00, 0x1b, 0x61, 0x01, 0x23, 0x31, 0x35, 0x20, (byte) 0xb0, (byte) 0xd9, (byte) 0xb6, (byte) 0xc8, (byte) 0xcd, (byte) 0xe2, (byte) 0xc2, (byte) 0xf4, 0x20, 0x20, 0x31, 0x31, (byte) 0xd4, (byte) 0xc2, 0x30
                , 0x39, (byte) 0xc8, (byte) 0xd5, 0x20, 0x31, 0x37, 0x3a, 0x35, 0x30, 0x3a, 0x33, 0x30, 0x0a, 0x0a, 0x0a, 0x0a, 0x0a
        };
    }

    public void append(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvNote.append(message);
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.smoothScrollTo(0, tvNote.getBottom());
                    }
                });
            }
        });
    }
}
