package com.atguigu.yygh.hosp.repository;

import com.atguigu.yygh.model.hosp.Department;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @program: yygh_parent
 * @description:
 * @author: 江天赐
 * @create: 2022-04-13 21:13
 */
@Repository
public interface DepartmentRepository extends MongoRepository<Department,String> {
    //添加科室信息
    Department getDepartmentByHoscodeAndDepcode(String hoscode, String depcode);
}
