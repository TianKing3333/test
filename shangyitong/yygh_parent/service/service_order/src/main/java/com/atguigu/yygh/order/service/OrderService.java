package com.atguigu.yygh.order.service;

import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import com.atguigu.yygh.vo.order.OrderQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * @program: yygh_parent
 * @description:
 * @author: 江天赐
 * @create: 2022-04-26 20:03
 */
public interface OrderService extends IService<OrderInfo> {
    //生成挂号订单
    Long saveOrder(String scheduleId, Long patientId);

    //根据订单id查询订单详情
    OrderInfo getOrder(String orderId);

    //订单列表（条件查询带分页）
    IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo);

    /**
     * 订单详情
     *
     * @param orderId
     * @return
     */
    Map<String, Object> show(Long orderId);

    /**
     * 取消预约
     *
     * @param orderId
     * @return
     */
    Boolean cancelOrder(Long orderId);

    /**
     * 就诊提醒
     */
    void patientTips();

    //预约统计方法
    Map<String,Object>getCountMap(OrderCountQueryVo orderCountQueryVo);
}
