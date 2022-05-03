package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @program: yygh_parent
 * @description:
 * @author: 江天赐
 * @create: 2022-04-13 21:14
 */
@Repository
public interface DepartmentService {
    //添加科室信息
    void save(Map<String, Object> paramMap);

    //查询科室接口
    Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo);

    //删除科室
    void remove(String hoscode, String depcode);

    //根据医院编号，查询医院所有的科室
    List<DepartmentVo> findDeptTree(String hoscode);

    //根据医院编号、科室名称查询科室编号
    Object getDepName(String hoscode, String depcode);

    //根据医院编号、科室名称查询科室对象
    Department getDepartment(String hoscode, String depcode);
}
