package com.atguigu.easyexcel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @program: yygh_parent
 * @description:
 * @author: 江天赐
 * @create: 2022-03-30 20:51
 */
@Data
public class UserData {
    //设置表头
    @ExcelProperty(value="用户编号",index=0)
    private int uid;
    @ExcelProperty(value="用户名称",index=1)
    private String username;

}
