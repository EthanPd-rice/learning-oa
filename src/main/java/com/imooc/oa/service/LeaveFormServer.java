package com.imooc.oa.service;

import com.imooc.oa.entity.Employee;
import com.imooc.oa.entity.LeaveForm;
import com.imooc.oa.entity.Notice;
import com.imooc.oa.entity.ProcessFlow;
import com.imooc.oa.mapper.EmployeeMapper;
import com.imooc.oa.mapper.LeaveFormMapper;
import com.imooc.oa.mapper.NoticeMapper;
import com.imooc.oa.mapper.ProcessFlowMapper;
import com.imooc.oa.service.exception.LeaveFormException;
import com.imooc.oa.utils.MybatisUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LeaveFormServer {
    /**
     * 创建请假单
     *
     * @param form 前端输入的请假单数据
     * @return 持久化后的请假单对象
     */
    public LeaveForm createLeaveForm(LeaveForm form) {
        EmployeeServer employeeServer = new EmployeeServer();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH时");
        LeaveForm f = (LeaveForm) MybatisUtils.executeUpdate(sqlSession -> {
            //1.持久化form表单数据，8级以下员工表单状态为processing,8级（总经理）状态为approved
            EmployeeMapper employeeMapper = sqlSession.getMapper(EmployeeMapper.class);
            Employee employee = employeeMapper.selectById(form.getEmployeeId());
            if (employee.getLevel() == 8) {
                form.setState("approved");
            } else {
                form.setState("processing");
            }
            LeaveFormMapper leaveFormMapper = sqlSession.getMapper(LeaveFormMapper.class);
            leaveFormMapper.insert(form);
            NoticeMapper noticeMapper = sqlSession.getMapper(NoticeMapper.class);
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
            if (employee.getLevel() < 7) {
                ProcessFlow flow2 = new ProcessFlow();
                Employee dmanage = employeeServer.selectLeader(employee.getEmployeeId());
                flow2.setFormId(form.getFormId());
                flow2.setOperatorId(dmanage.getEmployeeId());
                flow2.setAction("audit");
                flow2.setCreateTime(new Date());
                flow2.setOrderNo(2);
                flow2.setState("process");
                long diff = form.getEndTime().getTime() - form.getStartTime().getTime();
                float hours = diff / (1000 * 60 * 60) * 1f;
                if (hours >= 72) {
                    flow2.setIsLast(0);
                    processFlowMapper.insert(flow2);
                    ProcessFlow flow3 = new ProcessFlow();
                    Employee manage = employeeServer.selectLeader(dmanage.getEmployeeId());
                    flow3.setFormId(form.getFormId());
                    flow3.setOperatorId(manage.getEmployeeId());
                    flow3.setAction("audit");
                    flow3.setCreateTime(new Date());
                    flow3.setOrderNo(3);
                    flow3.setState("ready");
                    flow3.setIsLast(1);
                    processFlowMapper.insert(flow3);
                } else {
                    flow2.setIsLast(1);
                    processFlowMapper.insert(flow2);
                }
                String notice1 = String.format("您的请假申请[%s-%s]已提交，等待上级审批"
                        ,sdf.format(form.getStartTime().getTime())
                        ,sdf.format(form.getEndTime().getTime()));
                noticeMapper.insert(new Notice(form.getEmployeeId(),notice1));
                String notice2 = String.format("%s-%s提起请假申请[%s-%s],请尽快审批"
                        ,employee.getTitle(),employee.getName()
                        ,sdf.format(form.getStartTime().getTime())
                        ,sdf.format(form.getEndTime().getTime()));
                noticeMapper.insert(new Notice(dmanage.getEmployeeId(),notice2));
            } else if (employee.getLevel() == 7) {
                //3.2 7级员工，仅生成总经理审批任务。
                ProcessFlow flow2 = new ProcessFlow();
                Employee manage = employeeServer.selectLeader(employee.getEmployeeId());
                flow2.setFormId(form.getFormId());
                flow2.setOperatorId(manage.getEmployeeId());
                flow2.setAction("audit");
                flow2.setCreateTime(new Date());
                flow2.setOrderNo(2);
                flow2.setState("process");
                flow2.setIsLast(1);
                processFlowMapper.insert(flow2);
                String notice1 = String.format("您的请假申请[%s-%s]已提交，等待上级审批"
                        ,sdf.format(form.getStartTime().getTime())
                        ,sdf.format(form.getEndTime().getTime()));
                noticeMapper.insert(new Notice(form.getEmployeeId(),notice1));
                String notice2 = String.format("%s-%s提起请假申请[%s-%s],请尽快审批"
                        ,employee.getTitle(),employee.getName()
                        ,sdf.format(form.getStartTime().getTime())
                        ,sdf.format(form.getEndTime().getTime()));
                noticeMapper.insert(new Notice(manage.getEmployeeId(),notice2));
            } else if (employee.getLevel() == 8) {
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
                String notice1 = String.format("您的请假申请[%s-%s]系统已自动批准通过"
                        ,sdf.format(form.getStartTime().getTime())
                        ,sdf.format(form.getEndTime().getTime()));
                noticeMapper.insert(new Notice(form.getEmployeeId(),notice1));
            }

            return form;

        });


        return f;
    }

    /**
     * 获取指定任务状态以及指定经办人对应的请假单列表
     *
     * @param pfState      ProcessFlow任务状态
     * @param pfOperatorId 经办人编号
     * @return 请假单及相关数据列表
     */
    public List<Map> getLeaveFormList(String pfState, Long pfOperatorId) {
        List<Map> maps = (List<Map>) MybatisUtils.executeQuery(sqlSession ->
                sqlSession.getMapper(LeaveFormMapper.class).selectByParams(pfState, pfOperatorId));
        return maps;
    }


    public void audit(Long formId, Long operatorId, String result, String reason) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH时");
        MybatisUtils.executeUpdate(sqlSession -> {
            ProcessFlowMapper processFlowMapper = sqlSession.getMapper(ProcessFlowMapper.class);
            List<ProcessFlow> flowList = processFlowMapper.selectByFromID(formId);
            if (flowList == null) {
                throw new LeaveFormException("无效的审批流程");
            }
            List<ProcessFlow> processList = flowList.stream().filter(
                    p -> p.getOperatorId()==operatorId && p.getState().equals("process"))
                    .collect(Collectors.toList());
            ProcessFlow process = null;
            if (processList.size() == 0) {
                throw new LeaveFormException("未找到待处理任务节点");
            } else {
                process = processList.get(0);
                process.setState("complete");
                process.setReason(reason);
                process.setResult(result);
                process.setAuditTime(new Date());
                processFlowMapper.update(process);
            }
            LeaveFormMapper leaveFormMapper = sqlSession.getMapper(LeaveFormMapper.class);
            LeaveForm leaveForm = leaveFormMapper.selectById(formId);
            LeaveFormMapper formMapper = sqlSession.getMapper(LeaveFormMapper.class);
            LeaveForm form = formMapper.selectById(formId);
            EmployeeMapper employeeMapper = sqlSession.getMapper(EmployeeMapper.class);
            Employee employee = employeeMapper.selectById(form.getEmployeeId());//表单提交人信息
            Employee operator = employeeMapper.selectById(operatorId);//流程审批人信息
            NoticeMapper noticeMapper = sqlSession.getMapper(NoticeMapper.class);

            //若当前任务是最后一个节点，代表流程结束，更新请假单状态为对应的approved/refuse
            if (process.getIsLast() == 1) {

                leaveForm.setState(result);
                leaveFormMapper.update(leaveForm);
                String strResult = null;
                if(result.equals("approved")){
                    strResult = "批准";
                }else if(result.equals("refused")){
                    strResult = "驳回";
                }
                String notice1 = String.format("您的请假申请[%s-%s]%s%s已%s,审批意见：%s，审批流程已结束"
                        ,sdf.format(form.getStartTime().getTime())
                        ,sdf.format(form.getEndTime().getTime())
                        ,operator.getTitle(),operator.getName()
                        ,strResult,reason);
                noticeMapper.insert(new Notice(form.getEmployeeId(),notice1));
                String  notice2 = String.format("%s-%s提起请假申请[%s-%s]您已%s，审批意见:%s，审批流程已经结束"
                        ,employee.getTitle(),employee.getName()
                        ,sdf.format(form.getStartTime().getTime())
                        ,sdf.format(form.getEndTime().getTime())
                        ,strResult,reason);
                noticeMapper.insert(new Notice(operator.getEmployeeId(),notice2));

            } else {
                //若当前任务不是最后一个节点且审批通过，则下一个节点的状态从ready变为process
                List<ProcessFlow> readyList = flowList.stream().filter(p -> p.getState().equals("ready")).collect(Collectors.toList());
                if (result.equals("approved")) {
                    ProcessFlow readyProcess = readyList.get(0);
                    readyProcess.setState("process");
                    processFlowMapper.update(readyProcess);
                    //消息1: 通知表单提交人,部门经理已经审批通过,交由上级继续审批
                    //您的请假申请[%s-%s]%s%s已批准,审批意见:%s ,请继续等待上级审批" ,
                    String notice1 = String.format("您的请假申请[%s-%s]%s%s已批准,审批意见:%s ,请继续等待上级审批"
                            ,sdf.format(form.getStartTime().getTime())
                            ,sdf.format(form.getEndTime().getTime())
                            ,operator.getTitle(),operator.getName(),reason
                            );
                    noticeMapper.insert(new Notice(employee.getEmployeeId(),notice1));
                    //消息2: 通知总经理有新的审批任务
                    //%s-%s提起请假申请[%s-%s],请尽快审批"
                    String notice2 = String.format("%s-%s提起请假申请[%s-%s],请尽快审批"
                            ,employee.getTitle(),employee.getName()
                            ,sdf.format(form.getStartTime().getTime())
                            ,sdf.format(form.getEndTime().getTime()));
                    noticeMapper.insert(new Notice(readyProcess.getOperatorId(),notice2));
                    //消息3: 通知部门经理(当前经办人),员工的申请单你已批准,交由上级继续审批
                    //%s-%s提起请假申请[%s-%s]您已批准,审批意见:%s,申请转至上级领导继续审批"
                    String notice3 = String.format("%s-%s提起请假申请[%s-%s]您已批准,审批意见:%s,申请转至上级领导继续审批"
                            ,employee.getTitle(),employee.getName()
                            ,sdf.format(form.getStartTime().getTime())
                            ,sdf.format(form.getEndTime().getTime())
                            ,reason);
                    noticeMapper.insert(new Notice(operator.getEmployeeId(),notice3));
                }//若当前任务不是最后一个节点且审批驳回，则后续所有任务状态编程cancel，请假单状态变为refused
                else if (result.equals("refused")) {
                    for (ProcessFlow p : readyList) {
                        p.setState("cancel");
                        processFlowMapper.update(p);
                    }
                    leaveForm.setState("refused");
                    leaveFormMapper.update(leaveForm);
                    //消息1: 通知申请人表单已被驳回
                    //您的请假申请[%s-%s]%s%s已驳回,审批意见:%s,审批流程已结束"
                    String notice1 = String.format("您的请假申请[%s-%s]%s%s已驳回,审批意见:%s,审批流程已结束"
                            ,sdf.format(form.getStartTime().getTime())
                            ,sdf.format(form.getEndTime().getTime())
                            ,operator.getTitle()
                            ,operator.getName()
                            ,reason);
                    noticeMapper.insert(new Notice(form.getEmployeeId(),notice1));
                    //消息2: 通知经办人表单"您已驳回"
                    //%s-%s提起请假申请[%s-%s]您已驳回,审批意见:%s,审批流程已结束"
                    String notice2 = String.format("%s-%s提起请假申请[%s-%s]您已驳回,审批意见:%s,审批流程已结束"
                            ,employee.getTitle(),employee.getName()
                            ,sdf.format(form.getStartTime().getTime())
                            ,sdf.format(form.getEndTime().getTime())
                            ,reason);
                    noticeMapper.insert(new Notice(operator.getEmployeeId(),notice2));
                }
            }
            return null;
        });
    }
}