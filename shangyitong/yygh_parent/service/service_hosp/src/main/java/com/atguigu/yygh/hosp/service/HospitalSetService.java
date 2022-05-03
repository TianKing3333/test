package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.order.SignInfoVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @program: yygh_parent
 * @description:
 * @author: 江天赐
 * @create: 2022-03-22 18:45
 */
//继承mp的IService
//然后创建这个接口的实现类，这样就可以使用一些现成的方法了
//单表的CRUD，分页查询也有默认实现
public interface HospitalSetService extends IService<HospitalSet> {
    //2.根据传递过来的医院编码，查询数据库，查询签名
    String getSignKey(String hoscode);

    //获取医院签名信息
    SignInfoVo getSignInfoVo(String hoscode);
}
