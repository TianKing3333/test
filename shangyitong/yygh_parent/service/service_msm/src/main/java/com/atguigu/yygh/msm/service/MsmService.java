package com.atguigu.yygh.msm.service;

import com.atguigu.yygh.vo.msm.MsmVo;

/**
 * @program: yygh_parent
 * @description:
 * @author: 江天赐
 * @create: 2022-04-18 21:51
 */
public interface MsmService {
    //发送手机验证码
    boolean send(String phone, String code);

    //mq使用发送短信
    boolean send(MsmVo msmVo);
}
