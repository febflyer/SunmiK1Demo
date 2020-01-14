package com.sunmi.sunmik1demo.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sunmi.sunmik1demo.BaseFragment;
import com.sunmi.sunmik1demo.R;
import com.sunmi.sunmik1demo.ui.MainActivity;
import com.sunmi.sunmik1demo.ui.MoreActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.PrimitiveIterator;

public class PrinterFragment extends BaseFragment implements View.OnClickListener,MoreActivity.FragmentKeyeventListener {
    private static final String TAG = "PrinterFragment";

    private Button btnPrinterText;
    private Button btnPrinterImage;
    private Button btnPrinterCirculate;
    private CheckBox cbPrinterCutText;
    private CheckBox cbPrinterCutImage;
    private CheckBox cbPrinterText;
    private CheckBox cbPrinterImage;
    private EditText etPrinterTimes;
    private EditText etPrinterInterval;

    private EditText etTextArea;
    private ImageView ivImageArea;
    private TextView tvNoteArea;
    private ScrollView svNoteArea;

    //给测试---------------------------------------
//    private EditText etScannerTemp;     //临时功能
    private String strScannerTemp;
    private boolean bScannerTempFocus;

    private int iTotalScanTimes = 0;
    private int iErrScanTimes = 0;

    MoreActivity activity = null;
    //--------------------------------------------

    private static Runnable circulateRunnable;      //循环打印线程
    private int totalTextTimes;     //记录单次点击的总次数，优化界面显示
    private int totalImageTimes;

    @Override
    protected int setView() {
        return R.layout.fragment_printer;
    }

    @Override
    protected void init(View view){
        btnPrinterText = (Button) view.findViewById(R.id.btn_printer_text);
        btnPrinterImage = (Button) view.findViewById(R.id.btn_printer_image);
        btnPrinterCirculate = (Button) view.findViewById(R.id.btn_printer_circulate);
        cbPrinterCutText = (CheckBox) view.findViewById(R.id.cb_printer_cut_text);
        cbPrinterCutImage = (CheckBox) view.findViewById(R.id.cb_printer_cut_image);
        cbPrinterText = (CheckBox) view.findViewById(R.id.cb_printer_text);
        cbPrinterImage = (CheckBox) view.findViewById(R.id.cb_printer_image);
        etPrinterTimes = (EditText)view.findViewById(R.id.et_printer_times);
        etPrinterInterval = (EditText)view.findViewById(R.id.et_printer_interval);

        etTextArea = (EditText)view.findViewById(R.id.et_text_area);
        ivImageArea = (ImageView) view.findViewById(R.id.iv_image_area);
        tvNoteArea = (TextView) view.findViewById(R.id.tv_note_area);
        svNoteArea = (ScrollView) view.findViewById(R.id.sv_note_area);

//        etScannerTemp = view.findViewById(R.id.et_scanner_temp);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        btnPrinterText.setOnClickListener(this);
        btnPrinterImage.setOnClickListener(this);
        btnPrinterCirculate.setOnClickListener(this);
        ivImageArea.setOnClickListener(this);

        //测试---
        totalTextTimes = 0;
        totalImageTimes = 0;

        activity = (MoreActivity)getActivity();
        activity.setFragmentKeyeventListener(this);

//        etScannerTemp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                bScannerTempFocus = b;
//            }
//        });
//        etScannerTemp.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
////                Log.d("test1111","222");
//                strScannerTemp = etScannerTemp.getText().toString();
//                iTotalScanTimes = 0;
//                iErrScanTimes = 0;
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String strDate = dateFormat.format(date);
        switch (v.getId()){
            case R.id.btn_printer_text:
                if (MainActivity.kPrinterPresenter!=null) {
                    append(strDate + ":" + btnPrinterText.getText().toString() + "x" + ++totalTextTimes + "\r\n");
                    MainActivity.kPrinterPresenter.printText(etTextArea.getText().toString(),cbPrinterCutText.isChecked());
                }
                break;
            case R.id.btn_printer_image:
                if (MainActivity.kPrinterPresenter!=null) {
                    append(strDate + ":" + btnPrinterImage.getText().toString() + "x" + ++totalImageTimes + "\r\n");
                    MainActivity.kPrinterPresenter.printImage(((BitmapDrawable)ivImageArea.getDrawable()).getBitmap(), cbPrinterCutImage.isChecked());
                }
                break;
            case R.id.btn_printer_circulate:
                if (MainActivity.kPrinterPresenter == null)
                    break;
                append(strDate + ":" + btnPrinterCirculate.getText().toString()
                        + "[" + (cbPrinterText.isChecked()? btnPrinterText.getText().toString() : "") + ","
                        + (cbPrinterImage.isChecked()? btnPrinterImage.getText().toString() : "") + ","
                        + etPrinterTimes.getText().toString() + ","
                        + etPrinterInterval.getText().toString() + "]" + "\r\n");

                final int times = Integer.valueOf(etPrinterTimes.getText().toString());
                final int interval = Integer.valueOf(etPrinterInterval.getText().toString());
                String text = etTextArea.getText().toString();
                Bitmap image = ((BitmapDrawable)ivImageArea.getDrawable()).getBitmap();
                boolean bCutText = cbPrinterCutText.isChecked();
                boolean bCutImage = cbPrinterCutImage.isChecked();
                boolean bText = cbPrinterText.isChecked();
                boolean bImage = cbPrinterImage.isChecked();

                circulateRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int i=0; i<times;i++){
                                if (bText)
                                    MainActivity.kPrinterPresenter.printText(text,bCutText);
                                if (bImage)
                                    MainActivity.kPrinterPresenter.printImage(image,bCutImage);
                                Thread.sleep(interval);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                new Thread(circulateRunnable).start();
                break;
            case R.id.iv_image_area:
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*");
                //回调见onActivityResult
                startActivityForResult(intent, 0x1);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0x1 && data != null) {
            ivImageArea.setImageURI(data.getData());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void append(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvNoteArea.append(message);
                svNoteArea.post(new Runnable() {
                    @Override
                    public void run() {
                        svNoteArea.smoothScrollTo(0, tvNoteArea.getBottom());
                    }
                });
            }
        });
    }

    //191112.给测试的---------------------------------------------
    @Override
    public void onKeyEventListener(String key){
        if (bScannerTempFocus || strScannerTemp == null)
            return;

        iTotalScanTimes += 1;
        Log.d("test1111","str[" + strScannerTemp +"]  key[" + key + "]");
        if(!strScannerTemp.equals(key)) {
//            Log.d("test1111","str[" + strScannerTemp +"]  key[" + key + "]");
            iErrScanTimes += 1;
        }

        long curTime = System.currentTimeMillis();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int curTimes = iTotalScanTimes;
                while (true) {
                    if (curTimes != iTotalScanTimes)      //期间又扫了
                        return;
                    if (System.currentTimeMillis() - curTime > 5000) {
                        append("扫码测试元数据[" + strScannerTemp + "]\r\n"
                                + "总次数[" + iTotalScanTimes + "]\t异常次数[" + iErrScanTimes + "]\r\n");
                        return;
                    }
                }
            }
        });
        thread.start();


//        iTotalScanTimes += 1;
//        if(!strScannerTemp.equals(key)) {
//            iErrScanTimes += 1;
//            append("扫码测试元数据[" + strScannerTemp + "]\r\n"
//                    + "总次数[" + iTotalScanTimes + "]\t异常次数[" + iErrScanTimes + "]" + "异常数据[" + key + "]\r\n");
//        }

    }
    //---------------------------------------------
}
