package com.atguigu.yygh.oss.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @program: yygh_parent
 * @description:
 * @author: 江天赐
 * @create: 2022-04-20 21:24
 */
public interface FileService {
    //上传文件到阿里云oss
    String upload(MultipartFile file);
}
