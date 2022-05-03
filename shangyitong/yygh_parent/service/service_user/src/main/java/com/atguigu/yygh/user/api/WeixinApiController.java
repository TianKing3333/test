package com.atguigu.yygh.user.api;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.helper.JwtHelper;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.user.utils.ConstantWxPropertiesUtil;
import com.atguigu.yygh.user.utils.HttpClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: yygh_parent
 * @description: 微信操作接口
 * @author: 江天赐
 * @create: 2022-04-20 09:53
 */
@Controller //不会返回数据为了跳转方便 @RestController会返回json数据
@RequestMapping("/api/ucenter/wx")
public class WeixinApiController {
    @Autowired
    private UserInfoService userInfoService;

    //1.生成维信扫描的二维码
    //返回生成二维码需要参数
    @GetMapping("getLoginParam")
    @ResponseBody //返回json数据
    public Result genQrConnect() {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("appid", ConstantWxPropertiesUtil.WX_OPEN_APP_ID);
            map.put("scope", "snsapi_login");
            String wxOpenRedirectUrl = ConstantWxPropertiesUtil.WX_OPEN_REDIRECT_URL;
            wxOpenRedirectUrl = URLEncoder.encode(wxOpenRedirectUrl, "utf-8");
            map.put("redirect_uri", wxOpenRedirectUrl);
            map.put("state", System.currentTimeMillis() + "");
            return Result.ok(map);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    //2.回调的方法，得到扫描人信息
    @GetMapping("callback")
    public String callack(String code, String state) {
        //第一步：获取临时票据code
        System.out.println("code:" + code);
        //第二部 拿着code和微信id和密钥。请求微信固定地址，得到两个值
        //使用code和appid以及appscrect换取access_token

        //%s 占位符
        StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");

        String accessTokenUrl = String.format(baseAccessTokenUrl.toString(),
                ConstantWxPropertiesUtil.WX_OPEN_APP_ID,
                ConstantWxPropertiesUtil.WX_OPEN_APP_SECRET,
                code);
        //使用htppclient请求这个地址
        try {
            String accesstokenInfo = HttpClientUtils.get(accessTokenUrl);
            System.out.println("accesstokenInfo:" + accesstokenInfo);
            //从返回字符串获取两个值 openid 和 access_token
            JSONObject jsonObject = JSONObject.parseObject(accesstokenInfo);
            String openid = jsonObject.getString("openid");
            String access_token = jsonObject.getString("access_token");

            //根据openid判断数据库是否存在微信的扫码人信息
           UserInfo userInfoExit= userInfoService.selectWxInfoOpenid(openid);
           UserInfo userInfo=userInfoService.selectWxInfoOpenid(openid);
           if(userInfoExit==null) {
                //第三步 拿着openid 和 access_token请求微信地址，得到扫描人的信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                String userInfoUrl = String.format(baseUserInfoUrl, access_token, openid);
                String resultInfo = HttpClientUtils.get(userInfoUrl);
                System.out.println("resultInfo:" + resultInfo);
                //解析用户信息
                JSONObject jsonObject1 = JSONObject.parseObject(resultInfo);
                //用户昵称
                String nickname = jsonObject1.getString("nickname");
                //用户头像
                String headimgurl = jsonObject1.getString("headimgurl");

                //获取扫码人信息添加数据库
                userInfo = new UserInfo();
                userInfo.setNickName(nickname);
                userInfo.setOpenid(openid);
                userInfo.setStatus(1);
                userInfoService.save(userInfo);
            }
            //返回name和token字符串
            Map<String, String> map = new HashMap<>();
            String name = userInfo.getName();
            if(StringUtils.isEmpty(name)) {
                name = userInfo.getNickName();
            }
            if(StringUtils.isEmpty(name)) {
                name = userInfo.getPhone();
            }
            map.put("name", name);
            //判断userInfo是否有手机号，如果手机号为空，返回openid
            //如果手机号不为空，返回openid是空字符串
            //前端判断：如果openid不为空，绑定手机号，如果openid为空，不需要绑定手机号

            if(StringUtils.isEmpty(userInfo.getPhone())) {
                map.put("openid", userInfo.getOpenid());
            } else {
                map.put("openid", "");
            }
            String token = JwtHelper.createToken(userInfo.getId(), name);
            map.put("token", token);
            //跳转到前端页面
            return "redirect:" + ConstantWxPropertiesUtil.YYGH_BASE_URL + "/weixin/callback?token="+map.get("token")+"&openid="+map.get("openid")+"&name="+URLEncoder.encode(map.get("name"),"utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}