package com.atguigu.yygh.user.service;

import com.atguigu.yygh.model.user.Patient;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @program: yygh_parent
 * @description:
 * @author: 江天赐
 * @create: 2022-04-21 16:29
 */
public interface PatientService extends IService<Patient> {
    //获取就诊人列表
    List<Patient> finAllUserId(Long userId);
//    根据id获取就诊人信息
    Patient getPatientId(Long id);
}
