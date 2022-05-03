package com.atguigu.yygh.order.client;

import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @program: yygh_parent
 * @description:
 * @author: 江天赐
 * @create: 2022-04-29 22:28
 */
@FeignClient(value="service-order")
@Repository
public interface OrderFeignClient {
    /**
     * 根据排班id获取预约下单数据
     * @param orderCountQueryVo
     * @return
     */
    @PostMapping("/api/order/orderInfo/inner/getCountMap")
    public Map<String,Object> getCountMap(@RequestBody OrderCountQueryVo orderCountQueryVo);
}
