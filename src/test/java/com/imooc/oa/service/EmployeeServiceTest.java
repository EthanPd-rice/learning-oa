package com.imooc.oa.service;

import com.imooc.oa.entity.Employee;
import com.imooc.oa.mapper.EmployeeMapper;
import com.imooc.oa.utils.MybatisUtils;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class EmployeeServiceTest {

    @Test
    public void selectLeader() {
        MybatisUtils.executeQuery(sqlSession -> {
            EmployeeMapper employeeMapper = sqlSession.getMapper(EmployeeMapper.class);
            Map params = new LinkedHashMap();
            params.put("level","7");
            params.put("departmentId","2");
            List<Employee> employees = employeeMapper.selectByParams(params);
            for (Employee employee : employees){
                System.out.println(employee);
            }
            return employees;
        });
    }
}