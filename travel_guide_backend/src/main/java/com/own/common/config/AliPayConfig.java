package com.own.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "alipay")
public class AliPayConfig {
   /**
    * appId
    */
   private String appId;
   /**
    * 应用私钥
    */
   private String appPrivateKey;
   /**
    * 支付宝私钥
    */
   private String alipayPublicKey;
   /**
    * 支付成功回调接口
    */
   private String notifyUrl;
   /**
    * 应用公钥
    */
   private String appPublicKey;
   /**
    * 支付成功前端跳转
    */
   private String returnUrl;
   /**
    * 支付宝网关
    */
   private String gatewayUrl;
   /**
    * 格式化
    */
   private String format;
   /**
    * 字符集
    */
   private String charset;
   /**
    * 加密类型
    */
   private String signType;
}