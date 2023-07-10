package com.imooc.oa.service;

import com.imooc.oa.entity.LeaveForm;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class LeaveFormServerTest {
    LeaveFormServer leaveFormServer = new LeaveFormServer();
    @Test
    public void createLeaveForm1() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        LeaveForm leaveForm = new LeaveForm();
        leaveForm.setEmployeeId(8l);
        leaveForm.setFormType(1);
        leaveForm.setStartTime(sdf.parse("2023070220"));
        leaveForm.setEndTime(sdf.parse("2023071320"));
        leaveForm.setReason("等级小于7，时间大于72小时");
        leaveForm.setCreateTime(new Date());
        leaveForm.setState("processing");
        LeaveForm savedForm = leaveFormServer.createLeaveForm(leaveForm);
        System.out.println(savedForm.getFormId());
    }

    @Test
    public void createLeaveForm2() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        LeaveForm leaveForm = new LeaveForm();
        leaveForm.setEmployeeId(8l);
        leaveForm.setFormType(1);
        leaveForm.setStartTime(sdf.parse("2023070220"));
        leaveForm.setEndTime(sdf.parse("2023070223"));
        leaveForm.setReason("等级小于7，时间少于72小时");
        leaveForm.setCreateTime(new Date());
        leaveForm.setState("processing");
        LeaveForm savedForm = leaveFormServer.createLeaveForm(leaveForm);
        System.out.println(savedForm.getFormId());
    }

    @Test
    public void createLeaveForm3() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        LeaveForm leaveForm = new LeaveForm();
        leaveForm.setEmployeeId(1l);
        leaveForm.setFormType(1);
        leaveForm.setStartTime(sdf.parse("2023070220"));
        leaveForm.setEndTime(sdf.parse("2023070223"));
        leaveForm.setReason("等级等于8");
        leaveForm.setCreateTime(new Date());
        leaveForm.setState("processing");
        LeaveForm savedForm = leaveFormServer.createLeaveForm(leaveForm);
        System.out.println(savedForm.getFormId());
    }

    @Test
    public void createLeaveForm4() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        LeaveForm leaveForm = new LeaveForm();
        leaveForm.setEmployeeId(2l);
        leaveForm.setFormType(1);
        leaveForm.setStartTime(sdf.parse("2023070220"));
        leaveForm.setEndTime(sdf.parse("2023070223"));
        leaveForm.setReason("等级等于7");
        leaveForm.setCreateTime(new Date());
        leaveForm.setState("processing");
        LeaveForm savedForm = leaveFormServer.createLeaveForm(leaveForm);
        System.out.println(savedForm.getFormId());
    }

    @Test
    public void getLeaveFormList() {
        LeaveFormServer leaveFormServer = new LeaveFormServer();
        List<Map> list = leaveFormServer.getLeaveFormList("process",1l);
        System.out.println(list);
    }
    /**
     * 情况1: 72小时以上请假,部门经理同意,总经理同意,流程结束
     * @throws ParseException
     */
    @Test
    public void audit1() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        LeaveForm form = new LeaveForm();
        form.setEmployeeId(3l);
        form.setStartTime(sdf.parse("2020032608"));
        form.setEndTime(sdf.parse("2020040118"));
        form.setFormType(1);
        form.setReason("研发部员工王美美请假单(72小时以上)");
        form.setCreateTime(new Date());
        LeaveForm savedForm = leaveFormServer.createLeaveForm(form);
        System.out.println(savedForm.getFormId());
        leaveFormServer.audit(savedForm.getFormId(),2l,"approved","部门经理同意");
        leaveFormServer.audit(savedForm.getFormId(),1l,"approved","总经理同意");
    }

    /**
     * 情况2: 72小时以上请假,部门经理拒绝,流程结束
     * @throws ParseException
     */
    @Test
    public void audit2() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        LeaveForm form = new LeaveForm();
        form.setEmployeeId(3l);
        form.setStartTime(sdf.parse("2020032608"));
        form.setEndTime(sdf.parse("2020040118"));
        form.setFormType(1);
        form.setReason("研发部员工王美美请假单(72小时以上)");
        form.setCreateTime(new Date());
        LeaveForm savedForm = leaveFormServer.createLeaveForm(form);
        leaveFormServer.audit(savedForm.getFormId(),2l,"refused","部门经理拒绝");
    }

    /**
     * 情况3: 72小时以内请假,部门经理同意,流程结束
     * @throws ParseException
     */
    @Test
    public void audit3() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        LeaveForm form = new LeaveForm();
        form.setEmployeeId(3l);
        form.setStartTime(sdf.parse("2020032608"));
        form.setEndTime(sdf.parse("2020032718"));
        form.setFormType(1);
        form.setReason("研发部员工王美美请假单(72小时以内)");
        form.setCreateTime(new Date());
        LeaveForm savedForm = leaveFormServer.createLeaveForm(form);
        System.out.println(savedForm.getFormId());
        leaveFormServer.audit(savedForm.getFormId(),2l,"approved","部门经理同意");
    }
}