package com.oss.test;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;

/**
 * @program: yygh_parent
 * @description:
 * @author: 江天赐
 * @create: 2022-04-20 20:55
 */
public class OssTest {
    public static void main(String[] args) {
        // Endpoint以广州为例，其它Region请按实际情况填写。
        String endpoint = "https://oss-cn-guangzhou.aliyuncs.com";
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = "LTAI5t96w3B9U4pX8GVAw4RU";
        String accessKeySecret = "Zg9Gqzz7BTb804JYST0wjsQdrDFAm6";
        // 填写Bucket名称，例如examplebucket。
        String bucketName = "yygh-jtc-examplebucket";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        // 创建存储空间。
        ossClient.createBucket(bucketName);
        //关闭ossclient
        ossClient.shutdown();
    }
}
