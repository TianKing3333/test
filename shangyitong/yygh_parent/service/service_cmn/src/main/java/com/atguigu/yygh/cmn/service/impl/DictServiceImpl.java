package com.atguigu.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.yygh.cmn.listener.DictListener;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: yygh_parent
 * @description:
 * @author: 江天赐
 * @create: 2022-03-22 18:47
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    //根据数据id查询子数据列表
    @Cacheable(value = "dict", keyGenerator = "keyGenerator")
    @Override
    //将返回的数据dictList放到缓存中
    //keyGenerator 是com.atguigu.yygh.common.config.RedisConfig中的
    //根据包名类名方法名生成key
    public List<Dict> findChlidData(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper();
        wrapper.eq("parent_id", id);
        List<Dict> dictList = baseMapper.selectList(wrapper);
        //向list集合每个dict对象中设置hasChildren
        for (Dict dict : dictList) {
            Long dictId = dict.getId();
            boolean isChild = this.isChildren(dictId);
            dict.setHasChildren(isChild);
        }
        return dictList;
    }

    //导出数据字典
    @Override
    public void exportDictData(HttpServletResponse response) {
        //设置下载的相关内容
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = "dict";
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        //查询数据
        List<Dict> dictList = baseMapper.selectList(null);
        //Dict->DictEevo
        List<DictEeVo> dictVoList = new ArrayList<>();
        for (Dict dict : dictList) {
            DictEeVo dictEeVo = new DictEeVo();
            //dictEevo.setId(dict.getId);
            BeanUtils.copyProperties(dict, dictEeVo);
            dictVoList.add(dictEeVo);
        }
        try {
            //调用方法进行写操作
            EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("dict").doWrite(dictVoList);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //导入数据字典
    //DictListener 监听器
    @CacheEvict(value = "dict", allEntries = true)
    @Override
    //allEntries = true 更新操作清空缓存
    public void importData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), DictEeVo.class, new DictListener(baseMapper)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Cacheable(value = "dict", keyGenerator = "keyGenerator")
    @Override
    //根据dictCode和value查询
    public String getDictName(String dictCode, String value) {
        if (StringUtils.isEmpty(dictCode)) {//如果dictCode为空,直接根据value查询
            QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("value", value);
            Dict dict = baseMapper.selectOne(queryWrapper);
            return dict.getName();
        } else {
            Dict dict = this.getDictByDictCode(dictCode);
            //根据parent_Id和value查询
            Long parent_id = dict.getId(); //医院等级
            Dict finalDict = baseMapper.selectOne(new QueryWrapper<Dict>()
                    .eq("parent_id", parent_id)
                    .eq("value", value));
            return finalDict.getName();
        }
    }

    @Override
    //根据dictCode获取下级节点
    public List<Dict> findByDictCode(String dictCode) {
        //根据dictCode获取对应id
        Dict dict = this.getDictByDictCode(dictCode);
        //根据id获取子节点
        List<Dict> list = this.findChlidData(dict.getId());
        return list;
    }

    /**
     * 根据dict_code查询dict对象，得到dict对象
     *
     * @param dictCode
     * @return
     */
    private Dict getDictByDictCode(String dictCode) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dict_code", dictCode);
        Dict dict = baseMapper.selectOne(queryWrapper);
        return dict;
    }

    //判断id下面是否有子节点
    private boolean isChildren(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper();
        wrapper.eq("parent_id", id);
        return baseMapper.selectCount(wrapper) > 0;
    }
}
