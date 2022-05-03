package com.atguigu.easyexcel;

import com.alibaba.excel.EasyExcel;

/**
 * @program: yygh_parent
 * @description:
 * @author: 江天赐
 * @create: 2022-03-30 21:00
 */
public class TestRead {
    public static void main(String[] args) {
        //读取的文件路径
        String fileName="E:\\excel\\01.xlsx";
        //调用方法实现读取操作
        EasyExcel.read(fileName,UserData.class,new ExcelListener()).sheet().doRead();
    }
}
