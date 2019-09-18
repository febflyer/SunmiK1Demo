package com.sunmi.sunmik1demo.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.sunmi.peripheral.printer.SunmiPrinterService;
import com.sunmi.sunmik1demo.R;
import com.sunmi.sunmik1demo.bean.GoodsCode;
import com.sunmi.sunmik1demo.bean.GvBeans;
import com.sunmi.sunmik1demo.bean.MenuBean;
import com.sunmi.sunmik1demo.dialog.PayDialog;
import com.sunmi.sunmik1demo.ui.MainActivity;
import com.sunmi.sunmik1demo.utils.BitmapUtils;
import com.sunmi.sunmik1demo.utils.ResourcesUtils;
import com.sunmi.sunmik1demo.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by zhicheng.liu on 2018/4/4
 * address :liuzhicheng@sunmi.com
 * description :
 */

public class PrinterPresenter {
    private Context context;
    private static final String TAG = "PrinterPresenter";
    public SunmiPrinterService printerService;

    public PrinterPresenter(Context context, SunmiPrinterService printerService) {
        this.context = context;
        this.printerService = printerService;
    }

    public void print(final String json, final int payMode) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                MenuBean menuBean = JSON.parseObject(json, MenuBean.class);
                int fontsizeTitle = 40;
                int fontsizeContent = 30;
                int fontsizeFoot = 35;
                String divide = "**************************************" + "\n";

                String divide2 = "--------------------------------------" + "\n";

                if (MainActivity.isVertical) {
                    divide = "************************" + "\n";
                    divide2 = "------------------------" + "\n";
                }


                int width = divide2.length();
                String goods = formatTitle(width);
                try {
                    if (printerService.updatePrinterState() != 1) {
                        return;
                    }
                    printerService.setAlignment(1, null);
                    printerService.sendRAWData(boldOn(), null);
                    printerService.printTextWithFont(ResourcesUtils.getString(context, R.string.menus_title) + "\n" + ResourcesUtils.getString(context, R.string.print_proofs) + "\n", "", fontsizeTitle, null);
                    printerService.setAlignment(0, null);
                    printerService.sendRAWData(boldOff(), null);
                    printerService.printTextWithFont(divide, "", fontsizeContent, null);
                    printerService.printTextWithFont(ResourcesUtils.getString(context, R.string.print_order_number) + SystemClock.uptimeMillis() + "\n", "", fontsizeContent, null);
                    printerService.printTextWithFont(ResourcesUtils.getString(context, R.string.print_order_time) + formatData(new Date()) + "\n", "", fontsizeContent, null);
                    printerService.printTextWithFont(ResourcesUtils.getString(context, R.string.print_payment_method), "", fontsizeContent, null);
                    switch (payMode) {
                        case PayDialog.PAY_MODE_0:
                            printerService.printTextWithFont(ResourcesUtils.getString(context, R.string.pay_money) + "\n", "", fontsizeContent, null);
                            break;
                        case PayDialog.PAY_MODE_5:
                        case PayDialog.PAY_MODE_2:
                            printerService.printTextWithFont(ResourcesUtils.getString(context, R.string.pay_face) + "\n", "", fontsizeContent, null);
                            break;
                        case PayDialog.PAY_MODE_1:
                        case PayDialog.PAY_MODE_3:
                        case PayDialog.PAY_MODE_4:
                            printerService.printTextWithFont(ResourcesUtils.getString(context, R.string.pay_code) + "\n", "", fontsizeContent, null);
                            break;
                        default:
                            break;
                    }

                    printerService.printTextWithFont(divide, "", fontsizeContent, null);
                    printerService.printTextWithFont(goods + "\n", "", fontsizeContent, null);
                    printerService.printTextWithFont(divide2, "", fontsizeContent, null);
                    printGoods(menuBean, fontsizeContent, divide2, payMode, width);

                    printerService.printTextWithFont(divide, "", fontsizeContent, null);
                    printerService.sendRAWData(boldOn(), null);
                    if (payMode != 0 && payMode != 1) {
                        printerService.printTextWithFont(ResourcesUtils.getString(context, R.string.print_tips_havemoney), "", fontsizeFoot, null);
                    } else {
                        printerService.printTextWithFont(ResourcesUtils.getString(context, R.string.print_tips_nomoney), "", fontsizeFoot, null);
                    }
                    printerService.sendRAWData(boldOff(), null);
                    printerService.lineWrap(4, null);

                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.print_logo);
                    if (bitmap.getWidth() > 384) {
                        int newHeight = (int) (1.0 * bitmap.getHeight() * 384 / bitmap.getWidth());
                        bitmap = BitmapUtils.scale(bitmap, 384, newHeight);
                    }
                    printerService.printBitmap(bitmap, null);
                    printerService.printText("\n\n", null);
                    printerService.printTextWithFont(ResourcesUtils.getString(context, R.string.print_thanks), "", fontsizeContent, null);

                    printerService.lineWrap(4, null);
                    printerService.cutPaper(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private String formatTitle(int width) {
        Log.e("@@@@@", width + "=======");

        String[] title = {
                ResourcesUtils.getString(context, R.string.shop_car_goods_name),
                ResourcesUtils.getString(context, R.string.menus_unit_price),
                ResourcesUtils.getString(context, R.string.menus_unit_num),
                ResourcesUtils.getString(context, R.string.shop_car_unit_money),
        };
        StringBuffer sb = new StringBuffer();
        int blank1 = width * 1 / 3 - String_length(title[0]);
        int blank2 = width * 1 / 4 - String_length(title[1]);
        int blank3 = width * 1 / 4 - String_length(title[2]);

        sb.append(title[0]);
        sb.append(addblank(blank1));

        sb.append(title[1]);
        sb.append(addblank(blank2));

        sb.append(title[2]);
        sb.append(addblank(blank3));

        sb.append(title[3]);

//        int w1 = width / 3;
//        int w2 = width / 3 + 2;
//        String str = String.format("%-" + w1 + "s%-" + w2 + "s%s", title[0], title[1], title[2]);
        return sb.toString();
    }

    private void printNewline(String str, int width, int fontsizeContent) throws RemoteException {
        List<String> strings = Utils.getStrList(str, width);
        for (String string : strings) {
            printerService.printTextWithFont(string + "\n", "", fontsizeContent, null);
        }
    }

    private void printGoods(MenuBean menuBean, int fontsizeContent, String divide2, int payMode, int width) throws RemoteException {
        int blank1;
        int blank2;
        int blank3;
        int maxNameWidth = isZh() ? (width * 1 / 3 - 2) / 2 : (width * 1 / 3 - 2);

        StringBuffer sb = new StringBuffer();
        for (MenuBean.ListBean listBean : menuBean.getList()) {
            sb.setLength(0);

            String name = listBean.getParam2();
            String name1 = name.length() > maxNameWidth ? name.substring(0, maxNameWidth) : "";

            blank1 = width * 1 / 3 - String_length(name.length() > maxNameWidth ? name1 : name) + 1;

            GvBeans gvBeans = GoodsCode.getInstance().getGvBeansByCode(listBean.getCode());
            blank2 = width * 1 / 4 - String_length(gvBeans.getPrice());

            sb.append(name.length() > maxNameWidth ? name1 : name);
            sb.append(addblank(blank1));

            sb.append(gvBeans.getPrice());
            sb.append(addblank(blank2));

            if (listBean.getType() == 1) {
                sb.append(listBean.getNet() / 1000.000f);
                blank3 = width * 1 / 4 - (listBean.getNet() / 1000.000f + "").length();
            } else {
                sb.append(1);
                blank3 = width * 1 / 4 - 1;
            }

            sb.append(addblank(blank3));
            sb.append(listBean.getParam3());
            printerService.printTextWithFont(sb.toString() + "\n", "", fontsizeContent, null);

            if (name.length() > maxNameWidth) {
                printNewline(name.substring(maxNameWidth), maxNameWidth, fontsizeContent);
            }

        }
        printerService.printTextWithFont(divide2, "", fontsizeContent, null);
        String total = ResourcesUtils.getString(context, R.string.print_total_payment);
        String real = ResourcesUtils.getString(context, R.string.print_real_payment);

        sb.setLength(0);
        blank1 = width * 5 / 6 - String_length(total) - menuBean.getKVPList().get(0).getValue().length();
        blank2 = width * 5 / 6 - String_length(real) - menuBean.getKVPList().get(0).getValue().length();
        ;
        sb.append(total);
        sb.append(addblank(blank1));
        sb.append(ResourcesUtils.getString(context, R.string.units_money_units));
        sb.append(menuBean.getKVPList().get(0).getValue());

        printerService.printTextWithFont(sb.toString() + "\n", "", fontsizeContent, null);
        sb.setLength(0);
        sb.append(real);
        sb.append(addblank(blank2));
        sb.append(ResourcesUtils.getString(context, R.string.units_money_units));

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

        printerService.printTextWithFont(sb.toString() + "\n", "", fontsizeContent, null);
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

    private int String_length(String rawString) {
        return rawString.replaceAll("[\\u4e00-\\u9fa5]", "SH").length();
    }
}
