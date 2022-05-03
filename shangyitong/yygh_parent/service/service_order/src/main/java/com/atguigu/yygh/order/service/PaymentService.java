package com.atguigu.yygh.order.service;

import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * @program: yygh_parent
 * @description:
 * @author: 江天赐
 * @create: 2022-04-28 20:33
 */
public interface PaymentService extends IService<PaymentInfo> {
    //向支付记录表添加信息
    void savePaymentInfo(OrderInfo orderInfo, Integer status);
    /**
     * 支付成功
     */
    void paySuccess(String out_trade_no, Integer status, Map<String, String> resultMap);
    /**
     * 获取支付记录
     * @param orderId
     * @param paymentType
     * @return
     */
    PaymentInfo getPaymentInfo(Long orderId, Integer paymentType);

}
