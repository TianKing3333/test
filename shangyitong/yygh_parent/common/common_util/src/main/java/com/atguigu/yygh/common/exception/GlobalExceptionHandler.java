package com.atguigu.yygh.common.exception;

import com.atguigu.yygh.common.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @program: yygh_parent
 * @description:
 * @author: 江天赐
 * @create: 2022-03-23 18:54
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(YyghException.class)
    //出现Exception，被注接的方法会执行
    @ResponseBody//没有@controller注解，需要显示添加，输出json数据
    public Result error(YyghException e) {
        e.printStackTrace();
        return Result.fail();
    }
}
