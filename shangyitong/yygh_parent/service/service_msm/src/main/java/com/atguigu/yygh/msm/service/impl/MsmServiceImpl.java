package com.atguigu.yygh.msm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.msm.service.MsmService;
import com.atguigu.yygh.vo.msm.MsmVo;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @program: yygh_parent
 * @description:
 * @author: 江天赐
 * @create: 2022-04-18 21:52
 */
@Service
public class MsmServiceImpl implements MsmService {
    @Override
    public boolean send(String phone, String code) {
        //判断手机号是否为空 code：验证码
        if (StringUtils.isEmpty(phone)) {
            return false;
        }
        HashMap<String, Object> result = null;
        CCPRestSmsSDK restAPI = new CCPRestSmsSDK();
        restAPI.init("app.cloopen.com", "8883");
        // 初始化服务器地址和端口，生产环境配置成app.cloopen.com，端口是8883.
        restAPI.setAccount("8a216da8802d68fe01803cb5f62002e2", "f41e1ea5d48645adb02aaed076fe3fe0");
        // 初始化主账号名称和主账号令牌，登陆云通讯网站后，可在控制首页中看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN。
        restAPI.setAppId("8a216da8802d68fe01803cb5f72d02e8");
        // 请使用管理控制台中已创建应用的APPID。
        //设置需要发送的手机号和发送的验证码及过期时间code
        result = restAPI.sendTemplateSMS(phone, "1", new String[]{code, "3"});
        System.out.println("SDKTestGetSubAccounts1 result=" + result);
        if ("000000".equals(result.get("statusCode"))) {
            //正常返回输出data包体信息（map）
            HashMap<String, Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for (String key : keySet) {
                Object object = data.get(key);
                System.out.println(key + " = " + object);
            }
        } else {
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") + " 错误信息= " + result.get("statusMsg"));
        }
        return true;
    }

    @Override
    //mq发送短信封装
    public boolean send(MsmVo msmVo) {
        if (!StringUtils.isEmpty(msmVo.getPhone())) {
            boolean isSend = this.send(msmVo.getPhone(), msmVo.getParam());
            return isSend;
        }
        return false;
    }

    private boolean send(String phone, Map<String, Object> param) {
        //判断手机号是否为空 code：验证码
        if (StringUtils.isEmpty(phone)) {
            return false;
        }
        HashMap<String, Object> result = null;
        CCPRestSmsSDK restAPI = new CCPRestSmsSDK();
        restAPI.init("app.cloopen.com", "8883");
        // 初始化服务器地址和端口，生产环境配置成app.cloopen.com，端口是8883.
        restAPI.setAccount("8a216da8802d68fe01803cb5f62002e2", "f41e1ea5d48645adb02aaed076fe3fe0");
        // 初始化主账号名称和主账号令牌，登陆云通讯网站后，可在控制首页中看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN。
        restAPI.setAppId("8a216da8802d68fe01803cb5f72d02e8");
        // 请使用管理控制台中已创建应用的APPID。
        //设置需要发送的手机号和发送的验证码及过期时间code
//        StringBuffer sb=new StringBuffer();
//        Set<Entry<String, Object>> entrySet = param.entrySet();
//        for (Entry<String, Object> value : entrySet) {
//            sb.append(value.getValue());
//        }
//        String jsonParam = JSONObject.toJSONString(sb.toString().replaceAll(" ",""));
        String code = RandomStringUtils.randomNumeric(6);
        result = restAPI.sendTemplateSMS(phone, "1", new String[]{code, "1"});
        //只能发送验证码，需要企业认证
        System.out.println("SDKTestGetSubAccounts2 result=" + result);
        if ("000000".equals(result.get("statusCode"))) {
            //正常返回输出data包体信息（map）
            HashMap<String, Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for (String key : keySet) {
                Object object = data.get(key);
                System.out.println(key + " = " + object);
            }
        } else {
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") + " 错误信息= " + result.get("statusMsg"));
        }
        return true;
    }

//    public static void main(String[] args) {
//        MsmServiceImpl msmService = new MsmServiceImpl();
//        String code = RandomStringUtils.randomNumeric(6);
//        boolean sms = msmService.send("15873246500", code);
//        System.out.println(sms);
//    }
}
