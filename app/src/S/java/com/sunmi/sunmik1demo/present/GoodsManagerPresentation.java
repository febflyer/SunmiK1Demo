package com.sunmi.sunmik1demo.present;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sunmi.sunmik1demo.BasePresentation;
import com.sunmi.sunmik1demo.R;
import com.sunmi.sunmik1demo.bean.MenusBean;
import com.sunmi.sunmik1demo.ui.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GoodsManagerPresentation extends BasePresentation {
    private static final String TAG = "GoodsManagerPresentation";

    private Button btnPrinterText;
    private Button btnPrinterImage;
    private Button btnPrinterCirculate;
    private CheckBox cbPrinterCutPaper;
    private CheckBox cbPrinterText;
    private CheckBox cbPrinterImage;
    private EditText etPrinterTimes;
    private EditText etPrinterInterval;

    private EditText etTextArea;
    private ImageView ivImageArea;
    private TextView tvNoteArea;
    private ScrollView svNoteArea;

    private static Runnable circulateRunnable;      //循环打印线程
    private int totalTextTimes;     //记录单次点击的总次数，优化界面显示
    private int totalImageTimes;

    public GoodsManagerPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_printer);

        initView();
        initData();
    }

    private void initView() {
        btnPrinterText = (Button) findViewById(R.id.btn_printer_text);
        btnPrinterImage = (Button) findViewById(R.id.btn_printer_image);
        btnPrinterCirculate = (Button) findViewById(R.id.btn_printer_circulate);
        cbPrinterCutPaper = (CheckBox) findViewById(R.id.cb_printer_cut_paper);
        cbPrinterText = (CheckBox) findViewById(R.id.cb_printer_text);
        cbPrinterImage = (CheckBox) findViewById(R.id.cb_printer_image);
        etPrinterTimes = (EditText) findViewById(R.id.et_printer_times);
        etPrinterInterval = (EditText) findViewById(R.id.et_printer_interval);

        etTextArea = (EditText) findViewById(R.id.et_text_area);
        ivImageArea = (ImageView) findViewById(R.id.iv_image_area);
        tvNoteArea = (TextView) findViewById(R.id.tv_note_area);
        svNoteArea = (ScrollView) findViewById(R.id.sv_note_area);
    }

    private void initData() {
        btnPrinterText.setOnClickListener(this);
        btnPrinterImage.setOnClickListener(this);
        btnPrinterCirculate.setOnClickListener(this);
        ivImageArea.setOnClickListener(this);

        totalTextTimes = 0;
        totalImageTimes = 0;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String strDate = dateFormat.format(date);
        switch (v.getId()){
            case R.id.btn_printer_text:
                tvNoteArea.append("text");
                openInputMethod(etPrinterTimes);
//                MainActivity.test("mimimi");
                if (MainActivity.kPrinterPresenter!=null) {
                    append(strDate + ":" + btnPrinterText.getText().toString() + "x" + ++totalTextTimes + "\r\n");
                    MainActivity.kPrinterPresenter.printText(etTextArea.getText().toString(),cbPrinterCutPaper.isChecked());
                }
                break;
            case R.id.btn_printer_image:
                if (MainActivity.kPrinterPresenter!=null) {
                    append(strDate + ":" + btnPrinterImage.getText().toString() + "x" + ++totalImageTimes + "\r\n");
                    MainActivity.kPrinterPresenter.printImage(((BitmapDrawable)ivImageArea.getDrawable()).getBitmap(), cbPrinterCutPaper.isChecked());
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
                boolean bCut = cbPrinterCutPaper.isChecked();
                boolean bText = cbPrinterText.isChecked();
                boolean bImage = cbPrinterImage.isChecked();

                circulateRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int i=0; i<times;i++){
                                if (bText)
                                    MainActivity.kPrinterPresenter.printText(text,bCut);
                                if (bImage)
                                    MainActivity.kPrinterPresenter.printImage(image,bCut);
                                Thread.sleep(interval);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                new Thread(circulateRunnable).start();
                break;
//            case R.id.iv_image_area:
//                Intent intent = new Intent(Intent.ACTION_PICK, null);
//                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                        "image/*");
//                //回调见onActivityResult
//                startActivityForResult(intent, 0x1);
//                break;
            default:
                break;
        }
    }

    public void update(List<MenusBean> menus, String json) {}

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void onSelect(boolean isShow) {

    }

    @Override
    public void onDisplayRemoved() {
        super.onDisplayRemoved();
//        player.onDestroy();
    }

    public void append(final String message) {
        getOwnerActivity().runOnUiThread(new Runnable() {
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

    public void openInputMethod(final EditText editText){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager inputManager = (InputMethodManager)editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }
        }, 200);
    }
}
