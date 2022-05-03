package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.DepartmentRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: yygh_parent
 * @description:
 * @author: 江天赐
 * @create: 2022-04-13 21:15
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;


    @Override
    //添加科室信息
    public void save(Map<String, Object> paramMap) {
        //paramMap转换成department对象
        String paramMapString = JSONObject.toJSONString(paramMap);
        Department department = JSONObject.parseObject(paramMapString, Department.class);
        //根据医院编号和科室编号进行查询
        Department departmentExit = departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(), department.getDepcode());
        //判断
        if (departmentExit != null) {
            departmentExit.setUpdateTime(new Date());
            departmentExit.setIsDeleted(0);
            departmentRepository.save(departmentExit);
        } else {
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    @Override
    //条件分页查询科室
    public Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo) {
        //创建Pageable对象，设置当前页和每页记录数
        //0是第一页
        Pageable pageable = PageRequest.of(page - 1, limit);

        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo, department);
        department.setIsDeleted(0);
        //创建Example对象
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Department> example = Example.of(department, matcher);

        Page<Department> all = departmentRepository.findAll(example, pageable);
        return all;
    }

    @Override
    //删除科室接口
    public void remove(String hoscode, String depcode) {
        //根据医院编号和科室编号查询
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department != null) {
            //调用方法删除
            departmentRepository.deleteById(department.getId());
        }
    }

    @Override
    //根据医院编号，查询医院所有的科室
    public List<DepartmentVo> findDeptTree(String hoscode) {
        //创建List，用于最终数据封装
        List<DepartmentVo> result = new ArrayList<>();

        //根据医院编号，查询医院所有科室信息
        Department department = new Department();
        department.setHoscode(hoscode);
        //通过医院编号查询出此医院所有的科室信息   条件构造
        Example<Department> example = Example.of(department);
        //所有的科室信息
        List<Department> departmentList = departmentRepository.findAll(example);

        //先得到大科室 bigcode分组，获取每个大科室里面子级科室
        //使用java8中的stream
        //对bigcode分组 等到一个<bigcode:"",Department对象的List集合:[{医院编号:"",科室编号:"",科室描述:"",大科室名称:""}]>的Map
        Map<String, List<Department>> deparmentMap =
                departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));
        //遍历map集合 deparmentMap
        for (Map.Entry<String, List<Department>> entry : deparmentMap.entrySet()) {
            //大科室编号
            String bigcode = entry.getKey();
            //得到大科室编号对应的全局数据
            List<Department> departmentList1 = entry.getValue();

            //封装大科室
            DepartmentVo departmentVo1 = new DepartmentVo();
            //传入大科室编号
            departmentVo1.setDepcode(bigcode);
            //传入第一条数据的大科室名称
            departmentVo1.setDepname(departmentList1.get(0).getBigname());
            //封装小科室
            List<DepartmentVo> children = new ArrayList<>();
            for (Department department1 : departmentList1) {
                DepartmentVo departmentVo2 = new DepartmentVo();
                departmentVo2.setDepcode(department1.getDepcode());//传入小科室编号
                departmentVo2.setDepname(department1.getDepname());//传入小科室名称
                //封装到list集合中
                children.add(departmentVo2);//存到children中
            }
            //把小科室list集合放到大科室children里面
            departmentVo1.setChildren(children);
            //放到最终result里面
            result.add(departmentVo1);
        }
        return result;
    }

    @Override
    //根据医院编号、科室名称查询科室名称
    public Object getDepName(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department != null) {
            return department.getDepname();
        }
        return null;
    }

    @Override
    //根据医院编号、科室名称查询科室对象
    public Department getDepartment(String hoscode, String depcode) {
        return departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
    }
}
