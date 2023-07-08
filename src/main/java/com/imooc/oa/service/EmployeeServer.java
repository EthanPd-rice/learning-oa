package com.imooc.oa.service;

import com.imooc.oa.entity.Employee;
import com.imooc.oa.mapper.EmployeeMapper;
import com.imooc.oa.utils.MybatisUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EmployeeServer {
    public Employee selectById(Long employeeId){
        Employee employee = (Employee) MybatisUtils.executeQuery(sqlSession -> {
            EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
            return mapper.selectById(employeeId);
        });
        return employee;
    }
    public Employee selectLeader(Long employeeId){
        Employee l = (Employee) MybatisUtils.executeUpdate(sqlSession -> {
            EmployeeMapper employeeMapper = sqlSession.getMapper(EmployeeMapper.class);
            Employee employee = employeeMapper.selectById(employeeId);
            Employee leader = null;
            if(employee.getLevel() <7 ){
                //返回部门经理
                Map params = new LinkedHashMap();
                params.put("level","7");
                params.put("departmentId",employee.getDepartmentId());
                List<Employee> employeeList = employeeMapper.selectByParams(params);
                leader = employeeList.get(0);
            }else if(employee.getLevel() == 7){
                Map params = new LinkedHashMap();
                params.put("level","8");
                List<Employee> employeeList = employeeMapper.selectByParams(params);
                leader = employeeList.get(0);
            }else if(employee.getLevel() == 8){
                leader = employee;
            }
            return leader;
        });
        return l;
    }
}
