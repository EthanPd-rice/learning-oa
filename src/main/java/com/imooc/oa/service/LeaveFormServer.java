package com.imooc.oa.service;

import com.imooc.oa.entity.Employee;
import com.imooc.oa.entity.LeaveForm;
import com.imooc.oa.entity.ProcessFlow;
import com.imooc.oa.mapper.EmployeeMapper;
import com.imooc.oa.mapper.LeaveFormMapper;
import com.imooc.oa.mapper.ProcessFlowMapper;
import com.imooc.oa.utils.MybatisUtils;

import java.util.Date;

public class LeaveFormServer {
    /**
     * 创建请假单
     * @param form 前端输入的请假单数据
     * @return 持久化后的请假单对象
     */
    public LeaveForm createLeaveForm(LeaveForm form){
        EmployeeService employeeService = new EmployeeService();
        LeaveForm f = (LeaveForm) MybatisUtils.executeUpdate(sqlSession -> {
            //1.持久化form表单数据，8级以下员工表单状态为processing,8级（总经理）状态为approved
            EmployeeMapper employeeMapper = sqlSession.getMapper(EmployeeMapper.class);
            Employee employee = employeeMapper.selectById(form.getEmployeeId());
            if(employee.getLevel() == 8){
                form.setState("approved");
            }else{
                form.setState("processing");
            }
            LeaveFormMapper leaveFormMapper = sqlSession.getMapper(LeaveFormMapper.class);
            leaveFormMapper.insert(form);
            //2.添加第一条流程数据，说明表单已提交，状态为complete
            ProcessFlowMapper processFlowMapper = sqlSession.getMapper(ProcessFlowMapper.class);
            ProcessFlow flow1 = new ProcessFlow();
            flow1.setFormId(form.getFormId());
            flow1.setOperatorId(employee.getEmployeeId());
            flow1.setAction("apply");
            flow1.setCreateTime(new Date());
            flow1.setOrderNo(1);
            flow1.setState("complete");
            flow1.setIsLast(0);
            processFlowMapper.insert(flow1);
            //3.分情况建其余流程数据
            //3.1 7级以下员工，生成部门经理审批任务，请假时间大于72小时，还需生成总经理审批任务。
            if(employee.getLevel()<7){
                ProcessFlow flow2 = new ProcessFlow();
                Employee dmanage = employeeService.selectLeader(employee.getEmployeeId());
                flow2.setFormId(form.getFormId());
                flow2.setOperatorId(dmanage.getEmployeeId());
                flow2.setAction("audit");
                flow2.setCreateTime(new Date());
                flow2.setOrderNo(2);
                flow2.setState("process");
                long time1 = form.getEndTime().getTime();
                long time2 = form.getCreateTime().getTime();
                long diff = form.getEndTime().getTime() - form.getCreateTime().getTime();
                float hours = diff/(1000*60*60)*1f;
                if(hours >= 72){
                    flow2.setIsLast(0);
                    processFlowMapper.insert(flow2);
                    ProcessFlow flow3 = new ProcessFlow();
                    Employee manage = employeeService.selectLeader(dmanage.getEmployeeId());
                    flow3.setFormId(form.getFormId());
                    flow3.setOperatorId(manage.getEmployeeId());
                    flow3.setAction("audit");
                    flow3.setCreateTime(new Date());
                    flow3.setOrderNo(3);
                    flow3.setState("ready");
                    flow3.setIsLast(1);
                    processFlowMapper.insert(flow3);
                }else{
                    flow2.setIsLast(1);
                    processFlowMapper.insert(flow2);
                }

            }else if(employee.getLevel() == 7){
                //3.2 7级员工，仅生成总经理审批任务。
                ProcessFlow flow2 = new ProcessFlow();
                Employee manage = employeeService.selectLeader(employee.getEmployeeId());
                flow2.setFormId(form.getFormId());
                flow2.setOperatorId(manage.getEmployeeId());
                flow2.setAction("audit");
                flow2.setCreateTime(new Date());
                flow2.setOrderNo(2);
                flow2.setState("process");
                flow2.setIsLast(1);
                processFlowMapper.insert(flow2);
            }else if(employee.getLevel() == 8){
                //3.3 8级员工，生成总经理审批任务，系统自动通过。
                ProcessFlow flow2 = new ProcessFlow();
                flow2.setFormId(form.getFormId());
                flow2.setOperatorId(employee.getEmployeeId());
                flow2.setAction("audit");
                flow2.setResult("approved");
                flow2.setReason("自动通过");
                flow2.setCreateTime(new Date());
                flow2.setAuditTime(new Date());
                flow2.setOrderNo(2);
                flow2.setState("complete");
                flow2.setIsLast(1);
                processFlowMapper.insert(flow2);
            }

            return form;

        });


        return f;
    }
}
