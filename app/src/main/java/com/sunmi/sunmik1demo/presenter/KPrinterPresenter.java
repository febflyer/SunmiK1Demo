package com.sunmi.sunmik1demo.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.sunmi.extprinterservice.ExtPrinterService;
import com.sunmi.sunmik1demo.R;
import com.sunmi.sunmik1demo.bean.MenuBean;
import com.sunmi.sunmik1demo.dialog.PayDialog;
import com.sunmi.sunmik1demo.model.AlipaySmileModel;
import com.sunmi.sunmik1demo.utils.BitmapUtils;
import com.sunmi.sunmik1demo.utils.ResourcesUtils;
import com.sunmi.sunmik1demo.utils.Utils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by zhicheng.liu on 2018/4/4
 * address :liuzhicheng@sunmi.com
 * description :
 */

public class KPrinterPresenter {
    private Context context;
    private static final String TAG = "KPrinterPresenter";
    private ExtPrinterService mPrinter;
    String unic = "GBK";

    public KPrinterPresenter(Context context, ExtPrinterService printerService) {
        this.context = context;
        this.mPrinter = printerService;
    }

    public void print(String json, int payMode) {
        MenuBean menuBean = JSON.parseObject(json, MenuBean.class);
        int fontsizeTitle = 1;
        int fontsizeContent = 0;
        int fontsizeFoot = 1;
        String divide = "************************************************" + "";
        String divide2 = "-----------------------------------------------" + "";
        try {
            if (mPrinter.getPrinterStatus() != 0) {
                return;
            }
            mPrinter.sendRawData(lineSpacing(25));

            mPrinter.lineWrap(1);
            int width = divide2.length() * 5 / 12;
            String goods = formatTitle(width);
            mPrinter.setAlignMode(1);
            mPrinter.setFontZoom(fontsizeTitle, fontsizeTitle);
            mPrinter.sendRawData(boldOn());
            mPrinter.printText(ResourcesUtils.getString(context, R.string.menus_title) + "" + ResourcesUtils.getString(context, R.string.print_proofs) + "");
            mPrinter.flush();
            mPrinter.setAlignMode(0);
            mPrinter.setFontZoom(fontsizeContent, fontsizeContent);
            mPrinter.sendRawData(boldOff());
            mPrinter.printText(divide);

            mPrinter.printText(ResourcesUtils.getString(context, R.string.print_order_number) + SystemClock.uptimeMillis() + "");
            mPrinter.flush();
            mPrinter.printText(ResourcesUtils.getString(context, R.string.print_order_time) + formatData(new Date()) + "");
            mPrinter.flush();

            switch (payMode) {
                case PayDialog.PAY_MODE_0:
                    mPrinter.printText(ResourcesUtils.getString(context, R.string.print_payment_method) + ResourcesUtils.getString(context, R.string.pay_money) + "");
                    break;
                case PayDialog.PAY_MODE_5:
                case PayDialog.PAY_MODE_2:
                    mPrinter.printText(ResourcesUtils.getString(context, R.string.print_payment_method) + ResourcesUtils.getString(context, R.string.pay_face) + "");
                    break;
                case PayDialog.PAY_MODE_1:
                case PayDialog.PAY_MODE_3:
                case PayDialog.PAY_MODE_4:
                    mPrinter.printText(ResourcesUtils.getString(context, R.string.print_payment_method) + ResourcesUtils.getString(context, R.string.pay_code) + "");
                    break;
                default:
                    break;
            }

            mPrinter.flush();

            mPrinter.printText(divide);
            mPrinter.flush();

            mPrinter.printText(goods + "");
            mPrinter.flush();

            mPrinter.printText(divide2);
            mPrinter.flush();

            //changed by mayflower,-99用于DeviceFragment测试打印机
            if(payMode != -99)
                printGoods(menuBean, fontsizeContent, divide2, payMode, width);
            mPrinter.printText(divide);
            mPrinter.flush();

//           setCharSize(fontsizeFoot,fontsizeFoot);
//            mPrinter.setEmphasizedMode(255);
            if (payMode != 0 && payMode != 1) {
                mPrinter.printText(ResourcesUtils.getString(context, R.string.print_tips_havemoney));
            } else {
                mPrinter.printText(ResourcesUtils.getString(context, R.string.print_tips_nomoney));
            }
            mPrinter.flush();

            mPrinter.lineWrap(1);
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.print_logo);
            if (bitmap.getWidth() > 384) {
                int newHeight = (int) (1.0 * bitmap.getHeight() * 384 / bitmap.getWidth());
                bitmap = BitmapUtils.scale(bitmap, 384, newHeight);
            }
            mPrinter.printBitmap(bitmap, 2);
            mPrinter.lineWrap(1);
            mPrinter.printQrCode("https://sunmi.com/", 8, 0);

            mPrinter.lineWrap(1);
            mPrinter.setFontZoom(fontsizeContent, fontsizeContent);
            mPrinter.printText(ResourcesUtils.getString(context, R.string.print_thanks));
            mPrinter.flush();

            //测试此打印机128码宽度极限：28个字符--------------------------
//            mPrinter.lineWrap(1);
//            mPrinter.printText("code128");
//            mPrinter.flush();
//            mPrinter.lineWrap(1);
//            mPrinter.printBarCode("{C1234567890123456789012345678",8,2,96,2);
//            mPrinter.flush();
            //-----------------------------------------------------------

            mPrinter.lineWrap(3);
            mPrinter.cutPaper(0, 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printText(String string,boolean bCut){
        try {
            if (mPrinter.getPrinterStatus() != 0) {
                return;
            }
            if(string.isEmpty())
                return;

            mPrinter.printText(string);
            if(bCut)
                mPrinter.cutPaper(2,0);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void printImage(Bitmap bitmap,boolean bCut){
        try {
            if (mPrinter.getPrinterStatus() != 0) {
                return;
            }
//            if(bitmap == null)
//                return;

            if (bitmap.getWidth() > 384) {
                int newHeight = (int) (1.0 * bitmap.getHeight() * 384 / bitmap.getWidth());
                bitmap = BitmapUtils.scale(bitmap, 384, newHeight);
            }
            mPrinter.printBitmap(bitmap, 0);
            if(bCut)
                mPrinter.cutPaper(2,0);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private String formatTitle(int width) {
        Log.e("@@@@@", width + "=======");

        String[] title = {
                ResourcesUtils.getString(context, R.string.shop_car_goods_name),
                ResourcesUtils.getString(context, R.string.shop_car_number),
                ResourcesUtils.getString(context, R.string.shop_car_unit_money),
        };
        StringBuffer sb = new StringBuffer();
        int blank1 = width - String_length(title[0]);
        int blank2 = width - String_length(title[1]);

        sb.append(title[0]);
        sb.append(addblank(blank1));

        sb.append(title[1]);
        sb.append(addblank(blank2));

        sb.append(title[2] + "(" + ResourcesUtils.getString(context, R.string.print_rmb) + ")");

//        int w1 = width / 3;
//        int w2 = width / 3 + 2;
//        String str = String.format("%-" + w1 + "s%-" + w2 + "s%s", title[0], title[1], title[2]);
        return sb.toString();
    }

    private void printNewline(String str, int width) throws Exception {
        List<String> strings = Utils.getStrList(str, width);
        for (String string : strings) {
            mPrinter.printText(string);
            mPrinter.flush();
        }
    }

    private void printGoods(MenuBean menuBean, int fontsizeContent, String divide2, int payMode, int width) throws Exception {
        int blank1;
        int blank2;

        int maxNameWidth = isZh() ? (width - 2) / 2 : (width - 2);
        StringBuffer sb = new StringBuffer();
        for (MenuBean.ListBean listBean : menuBean.getList()) {
            sb.setLength(0);
            String name = listBean.getParam2();

            String name1 = name.length() > maxNameWidth ? name.substring(0, maxNameWidth) : "";


            blank1 = width - String_length(name.length() > maxNameWidth ? name1 : name) + 1;
            blank2 = width - 2;

            sb.append(name.length() > maxNameWidth ? name1 : name);
            sb.append(addblank(blank1));

            sb.append(1);
            sb.append(addblank(blank2));

            sb.append(listBean.getParam3().replace(ResourcesUtils.getString(context, R.string.units_money), ""));
            mPrinter.printText(sb.toString() + "");
            mPrinter.flush();
            if (name.length() > maxNameWidth) {
                printNewline(name.substring(maxNameWidth), maxNameWidth);
            }

        }
        mPrinter.printText(divide2);
        mPrinter.flush();

        String total = ResourcesUtils.getString(context, R.string.print_total_payment);
        String real = ResourcesUtils.getString(context, R.string.print_real_payment);

        sb.setLength(0);
        blank1 = width * 2 - String_length(total) - 1;
        blank2 = width * 2 - String_length(real) - 1;
        sb.append(total);
        sb.append(addblank(blank1));
        sb.append(menuBean.getKVPList().get(0).getValue());
        mPrinter.printText(sb.toString() + "");
        mPrinter.flush();

        sb.setLength(0);
        sb.append(real);
        sb.append(addblank(blank2));
        switch (payMode) {
            case PayDialog.PAY_MODE_2:
            case PayDialog.PAY_MODE_3:
            case PayDialog.PAY_MODE_4:
            case PayDialog.PAY_MODE_5:
                sb.append(PayDialog.PayMoney);
                break;
            default:
                sb.append("0.00");
                break;
        }
        mPrinter.printText(sb.toString() + "");
        mPrinter.flush();
        sb.setLength(0);
    }

    private String formatData(Date nowTime) {
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return time.format(nowTime);
    }

    private String addblank(int count) {
        String st = "";
        if (count < 0) {
            count = 0;
        }
        for (int i = 0; i < count; i++) {
            st = st + " ";
        }
        return st;
    }

    private static final byte ESC = 0x1B;// Escape

    //改行距
    private byte[] lineSpacing(int spacingSize) {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 0x33;
        result[2] = (byte)spacingSize;
        return result;
    }

    /**
     * 字体加粗
     */
    private byte[] boldOn() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 69;
        result[2] = 0xF;
        return result;
    }

    /**
     * 取消字体加粗
     */
    private byte[] boldOff() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 69;
        result[2] = 0;
        return result;
    }

    private boolean isZh() {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

    private byte[] mCmd = new byte[24];

    public synchronized int setCharSize(int hsize, int vsize) {
        int Width = 0;
        if (hsize == 0) {
            Width = 0;
        }
        if (hsize == 1) {
            Width = 16;
        }
        if (hsize == 2) {
            Width = 32;
        }
        if (hsize == 3) {
            Width = 48;
        }
        if (hsize == 4) {
            Width = 64;
        }
        if (hsize == 5) {
            Width = 80;
        }
        if (hsize == 6) {
            Width = 96;
        }

        if (hsize == 7) {
            Width = 112;
        }

        if (Width <= 0) {
            Width = 0;
        }

        if (Width >= 112) {
            Width = 112;
        }

        if (vsize <= 0) {
            vsize = 0;
        }

        if (vsize >= 7) {
            vsize = 7;
        }

        int Mul = Width + vsize;
        this.mCmd[0] = 29;
        this.mCmd[1] = 33;
        this.mCmd[2] = (byte) Mul;

        return /*this.mPrinter.writeIO(this.mCmd, 0, 3, 2000)*/1;
    }


    private int String_length(String rawString) {
        return rawString.replaceAll("[\\u4e00-\\u9fa5]", "SH").length();
    }
}
