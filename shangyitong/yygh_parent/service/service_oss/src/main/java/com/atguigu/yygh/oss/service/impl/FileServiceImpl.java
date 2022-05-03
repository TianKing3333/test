package com.atguigu.yygh.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.atguigu.yygh.oss.service.FileService;
import com.atguigu.yygh.oss.utils.ConstantOssPropertiesUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @program: yygh_parent
 * @description:
 * @author: 江天赐
 * @create: 2022-04-20 21:24
 */
@Service
public class FileServiceImpl implements FileService {
    @Override
    //上传文件到阿里云oss
    public String upload(MultipartFile file) {
        // Endpoint以广州为例，其它Region请按实际情况填写。
        String endpoint = ConstantOssPropertiesUtils.ENDPOINT;
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = ConstantOssPropertiesUtils.ACCESS_KEY_ID;
        String accessKeySecret = ConstantOssPropertiesUtils.SECRET;
        // 填写Bucket名称，例如examplebucket。
        String bucketName = ConstantOssPropertiesUtils.BUCKET;
        try {
            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            //上传文件流
            InputStream inputStream = file.getInputStream();
            String fileName = file.getOriginalFilename();
            //生成随机的唯一值，使用uuid，添加到文件名称中
            //UUID是指在一台机器上生成的数字，它保证对在同一时空中的所有机器都是唯一的。
            //组成：当前日期和时间+时钟序列+全局唯一的IEEE机器识别号（网卡MAC地址或其他）
            //唯一缺陷在于生成的结果串会比较长。
            //标准的UUID格式为：xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx (8-4-4-4-12)。
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            //01.jpg,只能加到文件前面
            fileName = uuid + fileName;
            //按照当前日期，创建文件夹，上传到创建文件夹里面
            // /2022/04/21/uuid01.jpg
            String timeUrl = new DateTime().toString("yyyy/MM/dd");
            //Object名称在使用UTF-8编码后长度必须在 1-1023字节之间，
            // 而且不能包含回车、换行、以及xml1.0不支持的字符，同时也不能以“/”或者“\”开头。
            //fileName = "/"+timeUrl + "/" + fileName; 错误
            fileName = timeUrl + "/" + fileName;
            //调用方法实现上传
            ossClient.putObject(bucketName, fileName, inputStream);
            //关闭ossClient
            ossClient.shutdown();
            //上传之后返回文件路径
            String url = "http://" + bucketName + "." + endpoint + "/" + fileName;
            //2022/04/21/7a6ccda5528c409da3791e3ff51f9179 2.jpeg
            //fileName:2.jpeg uuid:7a6ccda5528c409da3791e3ff51f9179
            //String timeUrl = new DateTime().toString("yyyy/MM/dd"); 2022/04/21/
            return url;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
