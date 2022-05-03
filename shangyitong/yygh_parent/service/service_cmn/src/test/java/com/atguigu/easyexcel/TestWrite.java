package com.atguigu.easyexcel;

import com.alibaba.excel.EasyExcel;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: yygh_parent
 * @description:
 * @author: 江天赐
 * @create: 2022-03-30 20:53
 */
public class TestWrite {
    public static void main(String[] args) {
        //构建一个数据list集合
        List<UserData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            UserData data = new UserData();
            data.setUid(i);
            data.setUsername("lucy" + i);
            list.add(data);
        }
        //设置excel文件路径和文件名称
        String fileName = "E:\\excel\\01.xlsx";
        //调用方法实现写操作
        EasyExcel.write(fileName, UserData.class).sheet("用户信息").doWrite(list);
    }
}
