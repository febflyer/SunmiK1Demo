package com.sunmi.sunmik1demo.model;


import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.sunmi.sunmik1demo.bean.BizContentBean;
import com.sunmi.sunmik1demo.bean.MenuBean;
import com.sunmi.sunmik1demo.dialog.PayDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import openapi.AlipayCallBack;
import openapi.AlipayClient;
import openapi.AlipayResponse;
import openapi.DefaultAlipayClient;
import openapi.ZimIdRequester;
import openapi.request.AlipayTradePayRequest;
import openapi.response.AlipayTradePayResponse;


/**
 * Created by zhicheng.liu on 2018/4/2
 * address :liuzhicheng@sunmi.com
 * description :
 */

public class AlipaySmileModel {
    public static String merchantId = "TEST_ZOLOZ_" + "2088521507558680";
    public static String appKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCGwedKn8ClEJWNkuOsWpbyAvjC2zBVOTFy0V0He/PGh6+i8Dpr3p79T+qa0yMPn/YTnunX6zQ8+U70KVYhtb8AM/MOm0hIKxpVk3j7fifzhnjykACbObd17raYVFPoE0V9lo1e+68nYxXGDM2dpnBADL3/YmijOPy3bOJfl9vGY+og0N69t+TmvjpUAyW01UICCpCqYec0cnGqR4+mX7E07XoOn1Q/o6UkqYsN9YZDjd0o6gYr0OmHBl7mj98sDYf7lj6eIVIltlqzSdHETGWQZ3gyHtnySodtk5XMEYK4dtaiV4IGGjt7uO/U8PYmKeGjHcKt5rxxjEqUDAMurwOdAgMBAAECggEAKTmOmVyv4Y9mQQ5LykFYZicUlP/bFGdPwlFcRVrdI8IEsDMPnEpZlyLooX6v/iqwbCY0YZKZw4F9buJqcBual+RMmg5VGYbeIonf/AgHDs/3ljUJinMotdJWV9mMZbXnj1kMXJ9gYQtF8PWfvXfaZS3Avi0L18sqPK75vcbGPDWIfqK/75Nrn5t46R32ebB7cG7eXZpqhhH1gpEsVnDvYq3vBX7e81az0tWudXzqa3kXaMJF0N+iw5kRpwz77BZPn6oHPyl2ShUTNMsDjJeGbT5EtpSdD/GGrhFZvcIPpFJyXgRSRRev9Vb6Rw/92p86HHtKyslmZC5/FuaLeh9rvQKBgQDN5H5iRI1od1KR5XHZE2LujhpBlf8QO8sBfCZ9uVUSKBspBgsTBIGSVYChv/qqOW7mRawMPNLlMKZtn1RdDDiOybBx9EWLH1lhQ8lR3A2/wfypat1MK2QAleKj2NRHuQb3zEE4Wesvmw2+pM2bcXVRm5z1fhozPDblZgF9vKCeTwKBgQCnjY29EOroeJMRf4arD0vgs5Y6afB0uPuqgD+0EWqEPZ6YIxNZUqhSWi3cosjyopx4OVHwcc1PFR2x+CxW43iiPYJEua7DD0IhfiHQsy1OugRGhnnoHwxW9wz/SsVxKGXdHo2yeRI2x7guzL4znO3O5LdLfm0gN23YB4XF2u1QUwKBgQChaQozm66IQVM4EXppRat/fWPUGvV1EosZxOygD5VDwLFaPeScqiGySNmo9MpcfN4WJHS2u1BSYQt4UTpgH96PAksTav7CDHeTqwK+7BbTSA79+ks3FaU2WErvTGNPPxzZUwWmWcfo8FOcK7MMs6vXqHkqdAuyMviOaqOsoR4CPQKBgE77Rh4ilGkjNUhhUeze2AjC/wToHWYYMOHNdLoAH7LYjSLwHdXURe4T/lxsC8d8ab+uXXa3Xm1x6b6T2urWbW4lCnw0/zGJxsWc/HmxjKD4xL0t9Nc7k4iHCoRpDatqvKHAHlFMCw7xlFvTPbF8vwjw/YpDvVMbbdcQrLl4mrdrAoGBAL5jnfxxA1+JY8C7DZq5LZcTJntdD3Dpxgkec6+oI08F/OqrAnO0FETfuXsfzPsljjcFVaKSUUuH5ewwdArU8ExEc48xD7e5FaK0h14Z61z8e9kHZG5y3t+mUXP0LLp53r6MLnE+sfFD12ZVfXKqpBcDjCcZZGi1PeVkzYGFJFKK";
    public static String Public_Key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhsHnSp/ApRCVjZLjrFqW8gL4wtswVTkxctFdB3vzxoevovA6a96e/U/qmtMjD5/2E57p1+s0PPlO9ClWIbW/ADPzDptISCsaVZN4+34n84Z48pAAmzm3de62mFRT6BNFfZaNXvuvJ2MVxgzNnaZwQAy9/2Joozj8t2ziX5fbxmPqINDevbfk5r46VAMltNVCAgqQqmHnNHJxqkePpl+xNO16Dp9UP6OlJKmLDfWGQ43dKOoGK9DphwZe5o/fLA2H+5Y+niFSJbZas0nRxExlkGd4Mh7Z8kqHbZOVzBGCuHbWoleCBho7e7jv1PD2Jinhox3Crea8cYxKlAwDLq8DnQIDAQAB";
    public static String appId =  "2018033002474060";
    public static String payMethod = "zoloz.authentication.customer.smilepay.initialize";
    public static String version = "1.0";
    public static final String url = "https://openapi.alipay.com/gateway.do";
    private AlipayClient alipayClient;
    private MenuBean menuBean;

    public AlipaySmileModel() {

    }

    private AlipayClient getAlipayClient() {
        if (alipayClient == null)
            alipayClient = new DefaultAlipayClient(url, appId, appKey, "json", "UTF-8", Public_Key, "RSA2");
        return alipayClient;
    }

    public Map<String, String> buildMerchantInfo() {
        Map<String, String> merchantInfo = new HashMap();
        merchantInfo.put("partnerId", merchantId);
        merchantInfo.put("merchantId", merchantId);
        merchantInfo.put("appId", appId);
        merchantInfo.put("merchantKey", "");
        merchantInfo.put("storeCode", "TEST");
        merchantInfo.put("brandCode", "TEST");
        merchantInfo.put("areaCode", "");
        merchantInfo.put("geo", "0.000000,0.000000");
        merchantInfo.put("wifiMac", "");
        merchantInfo.put("wifiName", "");
        merchantInfo.put("deviceNum", merchantId);
        merchantInfo.put("deviceMac", "CC:B8:A8:0F:63:3C");
        return merchantInfo;
    }

    public void setGoods(MenuBean menuBean) {
        this.menuBean = menuBean;
    }


    /**
     * 发送mZimId 给服务器获得mZimInitClientData
     *
     * @param metainfo
     */

    public void getInitClientData(String metainfo, final ZimIdRequester.RequestCallback callback) {
        new ZimIdRequester().request(metainfo, new ZimIdRequester.RequestCallback() {
            @Override
            public void onRequestStart() {
                callback.onRequestStart();
            }

            @Override
            public void onRequestResult(final String zimId, final String zimInitClientData) {
                callback.onRequestResult(zimId, zimInitClientData);
            }
        });

    }

    /**
     * 开始扣款
     *
     * @param fToken
     * @param amount
     * @param out_trade_no
     */
    public void startChargeback(final String out_trade_no, final String fToken, final String amount, final String subject, final String store_id, final AlipayModelCallBack alipayModelCallBack) {
        String money = /*"0.01"*/PayDialog.PayMoney;
        AlipayTradePayRequest request = new AlipayTradePayRequest(); //创建API对应的request类
        String json = JSON.toJSONString(buildAlipayRequest(out_trade_no, fToken, money, subject, store_id));
        Log.e("@@@@@", json);
        request.setBizContent(json);
        getAlipayClient().execute(request, new AlipayCallBack() {
            @Override
            public <T extends AlipayResponse> T onResponse(T response) {
                if (response.isSuccess()) {
                    alipayModelCallBack.onPayResult((AlipayTradePayResponse) response);
                } else {
                    alipayModelCallBack.onFail(response.getSubMsg());
                }
                return response;
            }
        });
    }

    private BizContentBean buildAlipayRequest(String out_trade_no, String fToken, String money, String subject, String store_id) {
        BizContentBean bizContentBean = new BizContentBean();
        bizContentBean.setOut_trade_no(out_trade_no);
        bizContentBean.setScene("security_code");
        bizContentBean.setAuth_code(fToken);
        bizContentBean.setSubject(subject);
        bizContentBean.setProduct_code("FACE_TO_FACE_PAYMENT");
        bizContentBean.setTotal_amount(Double.parseDouble(money));
        bizContentBean.setBody(menuBean.getTitle());
        List<BizContentBean.GoodsDetailBean> goods_detail = new ArrayList<>();
        for (MenuBean.ListBean listBean : menuBean.getList()) {
            BizContentBean.GoodsDetailBean goodsDetailBean = new BizContentBean.GoodsDetailBean();
            goodsDetailBean.setBody(listBean.getParam2());
            goodsDetailBean.setGoods_id(listBean.getParam1());
            goodsDetailBean.setGoods_name(listBean.getParam2());
            goodsDetailBean.setPrice(listBean.getParam3().substring(1));
            goodsDetailBean.setQuantity(1);
            goods_detail.add(goodsDetailBean);
        }
        bizContentBean.setGoods_detail(goods_detail);
        bizContentBean.setOperator_id("yx_001");
        bizContentBean.setStore_id(store_id);
        bizContentBean.setTerminal_id("NJ_T_001");
        bizContentBean.setTimeout_express("2m");
        return bizContentBean;
    }

    public interface AlipayModelCallBack {
        void onPayResult(AlipayTradePayResponse result);

        void onFail(String msg);
    }
}
