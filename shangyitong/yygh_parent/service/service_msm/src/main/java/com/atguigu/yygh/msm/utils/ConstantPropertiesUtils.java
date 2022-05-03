package com.atguigu.yygh.msm.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @program: yygh_parent
 * @description:
 * @author: 江天赐
 * @create: 2022-04-18 22:57
 */
@Component
public class ConstantPropertiesUtils implements InitializingBean {

    @Value("${rly.acount}")
    private String regionId;

    @Value("${rly.token}")
    private String accessKeyId;

    @Value("${rly.appId}")
    private String secret;

    public static String ACCOUNT;
    public static String TOKEN;
    public static String APPID;

    @Override
    public void afterPropertiesSet() throws Exception {
        ACCOUNT=regionId;
        TOKEN=accessKeyId;
        APPID=secret;
    }
}
