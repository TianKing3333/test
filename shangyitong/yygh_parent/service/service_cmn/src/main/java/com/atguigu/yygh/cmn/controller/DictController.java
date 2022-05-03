package com.atguigu.yygh.cmn.controller;

import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @program: yygh_parent
 * @description:
 * @author: 江天赐
 * @create: 2022-03-30 19:01
 */
@Api(value = "数据字典接口")
@RestController
@RequestMapping("admin/cmn/dict")
//@CrossOrigin //解决跨域问题 已用springcloud的gateway解决
public class DictController {

    @Autowired
    private DictService dictService;

    //导入数据字典
    @ApiOperation("导入")
    //一般来讲使用MultipartFile这个类主要是来实现以表单的形式进行文件上传功能。
    //MultipartFile file 这里的file一定要与前端的From Data中Content-Disposttionz中的name值一样
    //否则报null异常
    @PostMapping("importData")
    public Result importData(MultipartFile file) {
        dictService.importData(file);
        return Result.ok();
    }

    @ApiOperation("导出")
    //导出数据字典接口
    @GetMapping("exportData")
    public void exportDict(HttpServletResponse response) {
        dictService.exportDictData(response);
    }


    //根据dictCode获取下级节点
    @ApiOperation(value = "根据dictCode获取下级节点")
    @GetMapping("findByDictCode/{dictCode}")
    public Result findByDictCode(@PathVariable String dictCode) {
        List<Dict> list = dictService.findByDictCode(dictCode);
        return Result.ok(list);
    }

    //根据数据id查询子数据列表
    @ApiOperation(value = "根据数据id查询子数据列表")
    @GetMapping("findChildData/{id}")
    public Result findChildData(@PathVariable Long id) {
        List<Dict> list = dictService.findChlidData(id);
        return Result.ok(list);
    }

    //根据dictCode和value查询
    @ApiOperation(value = "根据dictCode和value查询")
    @GetMapping("getName/{dictCode}/{value}")
    public String getName(@PathVariable String dictCode,
                          @PathVariable String value){
        String dictName = dictService.getDictName(dictCode, value);
        return dictName;
    }

    //根据value查询
    @ApiOperation(value = "根据dictCode和value查询")
    @GetMapping("getName/{value}")
    public String getName(@PathVariable String value) {
        String dictName = dictService.getDictName("", value);
        return dictName;
    }
}
