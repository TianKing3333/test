package com.atguigu.yygh.user.client;

import com.atguigu.yygh.model.user.Patient;
import org.checkerframework.checker.units.qual.A;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @program: yygh_parent
 * @description:
 * @author: 江天赐
 * @create: 2022-04-26 20:19
 */
@FeignClient(value = "service-user")
@Repository
public interface PatientFeignClient {
    //根据就诊人id获取就诊人信息
    @GetMapping("/api/user/patient/inner/get/{id}")
    public Patient getPatientOrder(@PathVariable("id") Long id);
}
