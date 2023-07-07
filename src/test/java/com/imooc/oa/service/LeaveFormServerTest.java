package com.imooc.oa.service;

import com.imooc.oa.entity.LeaveForm;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
}