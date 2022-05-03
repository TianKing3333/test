package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @program: yygh_parent
 * @description:
 * @author: 江天赐
 * @create: 2022-04-12 22:54
 */
public interface HospitalService {
    //创建医院上传的方法
    void save(Map<String, Object> switchMap);

    //实现根据医院编号查询
    Hospital getByHoscode(String hoscode);

    //条件查询带分页
    Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

    //更新医院上线状态
    void updateStatus(String id, Integer status);

    //医院详情信息
    Map<String,Object> getHospById(String id);

    //通过医院编号获取医院名称
    String getHospName(String hoscode);

    //根据医院名称查询
    List<Hospital> findByHosName(String hosname);

    //根据医院编号获取预约挂号详情
    Map<String, Object> item(String hoscode);
}
