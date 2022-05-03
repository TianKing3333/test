package com.atguigu.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.enums.PaymentTypeEnum;
import com.atguigu.yygh.enums.RefundStatusEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.model.order.RefundInfo;
import com.atguigu.yygh.order.service.OrderService;
import com.atguigu.yygh.order.service.PaymentService;
import com.atguigu.yygh.order.service.RefundInfoService;
import com.atguigu.yygh.order.service.WeixinService;
import com.atguigu.yygh.order.utils.ConstantPropertiesUtils;
import com.atguigu.yygh.order.utils.HttpClient;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @program: yygh_parent
 * @description:
 * @author: 江天赐
 * @create: 2022-04-28 20:15
 */
@Service
public class WeixinServiceImpl implements WeixinService {
    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RefundInfoService refundInfoService;

    @Override
    //生成微信支付二维码
    public Map createNative(Long orderId) {
        try {
            //从redis获取数据
            Map payMap = (Map) redisTemplate.opsForValue().get(orderId.toString());
            if (payMap != null) {
                return payMap;
            }
            //1.根据orderId获取订单信息
            OrderInfo orderInfo = orderService.getById(orderId);
            //2.向支付记录表添加信息
            paymentService.savePaymentInfo(orderInfo, PaymentTypeEnum.WEIXIN.getStatus());
            //设置参数，调用微信生成二维码接口
            //把参数转换成xml格式，使用商户key进行加密
            Map paramMap = new HashMap();
            paramMap.put("appid", ConstantPropertiesUtils.APPID);
            paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            String body = orderInfo.getReserveDate() + "就诊" + orderInfo.getDepname();
            paramMap.put("body", body);
            paramMap.put("out_trade_no", orderInfo.getOutTradeNo());
            //paramMap.put("total_fee", order.getAmount().multiply(new BigDecimal("100")).longValue()+"");
            paramMap.put("total_fee", "1");//为了测试使用，统一写这个值
            paramMap.put("spbill_create_ip", "127.0.0.1");
            paramMap.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify");
            paramMap.put("trade_type", "NATIVE");
            //2、HTTPClient来根据URL访问第三方接口并且传递参数
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            //client设置参数
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY));
            client.setHttps(true);//支持https请求
            client.post();
            //3、返回第三方的数据
            String xml = client.getContent();
            //xml转成map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            //TODO:
            System.out.println("resultMap" + resultMap);
            //4、封装返回结果集
            Map map = new HashMap<>();
            map.put("orderId", orderId);
            map.put("totalFee", orderInfo.getAmount());
            map.put("resultCode", resultMap.get("result_code"));
            map.put("codeUrl", resultMap.get("code_url"));//二维码地址
            if (null != resultMap.get("result_code")) {
                //微信支付二维码2小时过期，可采取2小时未支付取消订单
                //                              key                 value   超时时间    单位
                redisTemplate.opsForValue().set(orderId.toString(), map, 120, TimeUnit.MINUTES);
            }
            return map;
        } catch (Exception exception) {
            exception.printStackTrace();
            return new HashMap<>();
        }
    }

    /**
     * 根据订单号去微信第三方查询支付状态
     */
    @Override
    public Map queryPayStatus(Long orderId, String paymentType) {
        try {
            //根据orderId获取订单信息
            OrderInfo orderInfo = orderService.getById(orderId);
            //1、封装参数
            Map paramMap = new HashMap<>();
            paramMap.put("appid", ConstantPropertiesUtils.APPID);
            paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
            paramMap.put("out_trade_no", orderInfo.getOutTradeNo());
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            //2、设置请求
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY));
            client.setHttps(true);//支持https
            client.post();
            //3、返回第三方的数据，转成Map
            String xml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            System.out.println(resultMap);
            //4、返回
            return resultMap;
        } catch (Exception e) {
            return null;
        }
    }

    /***
     * 微信退款
     * @param orderId
     * @return
     */
    @Override
    public Boolean refund(Long orderId) {
        try {
            //获取到支付记录信息
            PaymentInfo paymentInfo = paymentService.getPaymentInfo(orderId, PaymentTypeEnum.WEIXIN.getStatus());
            //添加信息到退款记录表
            RefundInfo refundInfo = refundInfoService.saveRefundInfo(paymentInfo);
            //判断当前订单数据是否已经退款
            if (refundInfo.getRefundStatus().intValue() == RefundStatusEnum.REFUND.getStatus().intValue()) {
                return true;
            }
            //调用微信接口实现退款
            //封装需要参数
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("appid", ConstantPropertiesUtils.APPID);
            //公众账号ID
            paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
            //商户编号
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            paramMap.put("transaction_id", paymentInfo.getTradeNo());
            //微信订单号
            paramMap.put("out_trade_no", paymentInfo.getOutTradeNo());
            //商户订单编号
            paramMap.put("out_refund_no", "tk" + paymentInfo.getOutTradeNo());
            //商户退款单号
            //TODO：上线修改回来
//       paramMap.put("total_fee",paymentInfoQuery.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
//       paramMap.put("refund_fee",paymentInfoQuery.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
            paramMap.put("total_fee", "1");//退款一分钱
            paramMap.put("refund_fee", "1");

            String paramXml = WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY);
            //设置调用接口内容
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/secapi/pay/refund");
            client.setXmlParam(paramXml);
            client.setHttps(true);
            //设置证书信息
            client.setCert(true);
            client.setCertPassword(ConstantPropertiesUtils.PARTNER);
            client.post();//调用
            //3、返回第三方的数据
            String xml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            if (null != resultMap && WXPayConstants.SUCCESS.equalsIgnoreCase(resultMap.get("result_code"))) {
                refundInfo.setCallbackTime(new Date());
                refundInfo.setTradeNo(resultMap.get("refund_id"));
                refundInfo.setRefundStatus(RefundStatusEnum.REFUND.getStatus());
                refundInfo.setCallbackContent(JSONObject.toJSONString(resultMap));
                refundInfoService.updateById(refundInfo);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
