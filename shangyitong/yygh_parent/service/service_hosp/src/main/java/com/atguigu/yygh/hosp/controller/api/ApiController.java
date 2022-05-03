package com.atguigu.yygh.hosp.controller.api;

import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.helper.HttpRequestHelper;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.common.utils.MD5;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @program: yygh_parent
 * @description:
 * @author: 江天赐
 * @create: 2022-04-12 22:58
 */
@Api(tags = "医院管理API接口")
@RestController
@RequestMapping("/api/hosp")
public class ApiController {
    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private HospitalSetService hospitalSetService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private ScheduleService scheduleService;

    //删除排班接口
    @ApiOperation(value = "删除排班接口")
    @PostMapping("schedule/remove")
    public Result remove(HttpServletRequest request) {
        Map<String, Object> paramMap = CheckSignKey(request);
        //医院编号
        String hoscode = (String) paramMap.get("hoscode");
        //科室编号
        String hosScheduleId = (String) paramMap.get("hosScheduleId");
        scheduleService.remove(hoscode, hosScheduleId);
        return Result.ok();
    }

    //查询排班接口
    @ApiOperation(value = "查询排班接口")
    @PostMapping("schedule/list")
    public Result findSchedule(HttpServletRequest request) {
        Map<String, Object> paramMap = CheckSignKey(request);
        //paramMap.get("hoscode")为空，page默认为1,不为空，取到这个值
        int page = StringUtils.isEmpty(paramMap.get("page")) ? 1 : Integer.parseInt((String) paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit")) ? 1 : Integer.parseInt((String) paramMap.get("limit"));
        //医院编号
        String hoscode = (String) paramMap.get("hoscode");
        //科室编号
        String depcode = (String) paramMap.get("depcode");
        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode(hoscode);
        scheduleQueryVo.setHoscode(depcode);
        //调用service方法
        Page<Schedule> pageMode1 = scheduleService.findPageSchedule(page, limit, scheduleQueryVo);
        return Result.ok(pageMode1);
    }

    //上传排班接口
    @ApiOperation(value = "上传排班接口")
    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request) {
        Map<String, Object> paramMap = CheckSignKey(request);
        scheduleService.save(paramMap);
        return Result.ok();
    }

    //删除科室接口
    @ApiOperation(value = "删除科室接口")
    @PostMapping("department/remove")
    public Result removeDepartment(HttpServletRequest request) {
        //TODO:签名校验
        Map<String, Object> paramMap = CheckSignKey(request);
        String hoscode = (String) paramMap.get("hoscode");
        String depcode = (String) paramMap.get("depcode");
        //调用service方法
        departmentService.remove(hoscode, depcode);
        return Result.ok();
    }

    //查询科室接口
    @ApiOperation(value = "查询科室接口")
    @PostMapping("department/list")
    public Result findDepartment(HttpServletRequest request) {
        //TODO:签名校验
        Map<String, Object> paramMap = CheckSignKey(request);
        //paramMap.get("hoscode")为空，page默认为1,不为空，取到这个值
        int page = StringUtils.isEmpty(paramMap.get("page")) ? 1 : Integer.parseInt((String) paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit")) ? 1 : Integer.parseInt((String) paramMap.get("limit"));

        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode((String) paramMap.get("hoscode"));
        //调用service方法
        Page<Department> pageMode1 = departmentService.findPageDepartment(page, limit, departmentQueryVo);
        return Result.ok(pageMode1);
    }

    //上传科室
    @ApiOperation(value = "上传科室接口")
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request) {
        //调用service的方法
        departmentService.save(CheckSignKey(request));
        return Result.ok();
    }

    //实现根据医院编号查询医院
    @ApiOperation(value = "根据医院编号查询医院")
    @PostMapping("hospital/show")
    public Result getHospital(HttpServletRequest request) {
        //调用service的方法，根据医院编号查询
        Hospital hospital = hospitalService.getByHoscode((String) CheckSignKey(request).
                get("hoscode"));
        return Result.ok(hospital);
    }

    //上传医院的接口
    @ApiOperation(value = "上传医院的接口")
    @PostMapping("/saveHospital")
    public Result saveHosp(HttpServletRequest request) {
        Map<String, Object> paramMap = CheckSignKey(request);
        //传输过程中”+“转换成了” “，因此需要转回,不转换图片显示不出
        String logoData = paramMap.get("logoData").toString();
        logoData = logoData.replaceAll(" ", "+");
        paramMap.put("logoData", logoData);
        //调用service的方法
        hospitalService.save(paramMap);
        return Result.ok();

    }

    //校验HttpServletRequest传来的医院信息中的sign是否与数据库中的sign一致
    //根据传来的医院信息的hoscode查询出sign,
    public Map<String, Object> CheckSignKey(HttpServletRequest request) {
        //获取传来的医院信息 将http请求的json数据转成Map
        Map<String, String[]> requestMap = request.getParameterMap();

        //将传递过来的数据由String[]->Object
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        //获取医院传递的编号
        String hoscode = (String) paramMap.get("hoscode");
        //1.获取医院传递过来的签名,这个签名是MD5加密的
        String hospSign = (String) paramMap.get("sign");
        //2.根据传递过来的医院编码，查询数据库，查询签名

        //调用数据库中的方法查询签名
        String signKey = hospitalSetService.getSignKey(hoscode);

        //3把数据库查询签名进行MD5加密
        String signKeyMd5 = MD5.encrypt(signKey);

        //4判断签名是否一致
        if (hospSign.equals(signKeyMd5)) {
            //抛出签名异常
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        return paramMap;
    }
}
