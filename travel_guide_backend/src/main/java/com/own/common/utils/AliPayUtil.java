package com.own.common.utils;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.y8e.common.exception.BaseException;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.own.common.config.AliPayConfig;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;

@Component
@Slf4j
public class AliPayUtil {

    public static String toPay(String goodsName, String orderNo, BigDecimal orderMoney, String notifyApi, String returnRouter) {
        AliPayConfig aliPayConfig = SpringUtil.getBean(AliPayConfig.class);
        //创建Client，调用通用SDK提到的Client，用来调用支付宝的API  固定
        AlipayClient alipayClient = new DefaultAlipayClient(aliPayConfig.getGatewayUrl(), aliPayConfig.getAppId(), aliPayConfig.getAppPrivateKey(),
                aliPayConfig.getFormat(), aliPayConfig.getCharset(), aliPayConfig.getAppPublicKey(), aliPayConfig.getSignType());

        //创建Request并设置参数   固定参数，要传递其他参数可去查阅帮助文档
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();

        request.setNotifyUrl(aliPayConfig.getNotifyUrl() + notifyApi);
        request.setReturnUrl(aliPayConfig.getReturnUrl() + returnRouter);
        JSONObject jsonObject = new JSONObject();

        log.info("回调地址：" + request.getNotifyUrl());
        log.info("同步返回：" + request.getReturnUrl());

        jsonObject.put("out_trade_no", orderNo);
        jsonObject.put("total_amount", orderMoney);
        jsonObject.put("subject", goodsName);
        jsonObject.put("product_code", "FAST_INSTANT_TRADE_PAY");
        String s = JSONUtil.toJsonStr(jsonObject);
        request.setBizContent(s.replaceAll("&quot", ""));
        String form = "";
        try {
            // 调用SDK生成表单
            form = alipayClient.pageExecute(request).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return form;
    }

    public void toPay(String goodsName, String orderNo, BigDecimal orderMoney, HttpServletResponse httpResponse) {
        //创建Client，调用通用SDK提到的Client，用来调用支付宝的API  固定
        AliPayConfig aliPayConfig = SpringUtil.getBean(AliPayConfig.class);
        AlipayClient alipayClient = new DefaultAlipayClient(aliPayConfig.getGatewayUrl(), aliPayConfig.getAppId(), aliPayConfig.getAppPrivateKey(),
                aliPayConfig.getFormat(), aliPayConfig.getCharset(), aliPayConfig.getAppPublicKey(), aliPayConfig.getSignType());

        //创建Request并设置参数   固定参数，要传递其他参数可去查阅帮助文档
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();

        request.setNotifyUrl(aliPayConfig.getNotifyUrl());
        request.setReturnUrl(aliPayConfig.getReturnUrl());
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("out_trade_no", orderNo);
        jsonObject.put("total_amount", orderMoney);
        jsonObject.put("subject", goodsName);
        jsonObject.put("product_code", "FAST_INSTANT_TRADE_PAY");
        String s = JSONUtil.toJsonStr(jsonObject);
        request.setBizContent(s.replaceAll("&quot", ""));
        String form = "";
        try {
            // 调用SDK生成表单
            form = alipayClient.pageExecute(request).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        httpResponse.setContentType("text/html;charset=" + aliPayConfig.getCharset());
        try {
            httpResponse.getWriter().write(form);

            httpResponse.getWriter().flush();
            httpResponse.getWriter().close();
        } catch (IOException e) {
            throw new BaseException("跳转错误");
        }
    }
}
