package com.atguigu.yygh.cmn.service;

import com.atguigu.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @program: yygh_parent
 * @description:
 * @author: 江天赐
 * @create: 2022-03-22 18:45
 */
//继承mp的IService
//然后创建这个接口的实现类，这样就可以使用一些现成的方法了
//单表的CRUD，分页查询也有默认实现
public interface DictService extends IService<Dict> {
    //根据数据id查询子数据列表
    List<Dict> findChlidData(Long id);
    //导出数据字典接口
    void exportDictData(HttpServletResponse response);
    //导入数据字典接口
    void importData(MultipartFile file);
    //根据dictCode和value查询
    String getDictName(String dictCode, String value);
    //根据dictCode获取下级节点
    List<Dict> findByDictCode(String dictCode);
}
