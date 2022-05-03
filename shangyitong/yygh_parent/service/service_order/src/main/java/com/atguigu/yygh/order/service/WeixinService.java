package com.atguigu.yygh.order.service;

import java.util.Map;

/**
 * @program: yygh_parent
 * @description:
 * @author: 江天赐
 * @create: 2022-04-28 20:15
 */
public interface WeixinService {
    //生成微信支付二维码
    Map createNative(Long orderId);
    /**
     * 根据订单号去微信第三方查询支付状态
     */
    Map queryPayStatus(Long orderId, String paymentType);
    /***
     * 退款
     * @param orderId
     * @return
     */
    Boolean refund(Long orderId);

}
