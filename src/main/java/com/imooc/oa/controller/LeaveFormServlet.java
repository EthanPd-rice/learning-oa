package com.imooc.oa.controller;

import com.imooc.oa.entity.Employee;
import com.imooc.oa.entity.LeaveForm;
import com.imooc.oa.service.LeaveFormServer;
import com.imooc.oa.utils.MybatisUtils;
import com.imooc.oa.utils.ResponseUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@WebServlet("/api/leave/*")
public class LeaveFormServlet extends HttpServlet {
    private LeaveFormServer leaveFormServer = new LeaveFormServer();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        //https://localhost/api/leave/create
        String uri = request.getRequestURI();
        String methodName = uri.substring(uri.lastIndexOf("/")+1);
        //create创建请假单、list查询请假单，audit调用审核方法
        if(methodName.equals("create")){
            this.create(request, response);
        }else if(methodName.equals("list")){
            this.list(request, response);
        }else if(methodName.equals("audit")){
            this.audit(request, response);
        }

    }

    private void create(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LeaveForm form = new LeaveForm();
        String employeeID = request.getParameter("eid");
        String formType = request.getParameter("formType");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        String reason = request.getParameter("reason");
        form.setEmployeeId(Long.parseLong(employeeID));
        form.setFormType(Integer.parseInt(formType));
        form.setCreateTime(new Date());
        form.setStartTime(new Date(Long.parseLong(startTime)));
        form.setEndTime(new Date(Long.parseLong(endTime)));
        form.setReason(reason);
        System.out.println(form);
        ResponseUtils resp = null;
        try{
            leaveFormServer.createLeaveForm(form);
            resp = new ResponseUtils();
        }catch (Exception e){
            e.printStackTrace();
            resp = new ResponseUtils(e.getClass().getSimpleName(),e.getMessage());
        }
        response.getWriter().println(resp.toJsonString());
    }

    public void list(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String employeeId = request.getParameter("eid");
        ResponseUtils resp = null;
        try{
        List<Map> list = leaveFormServer.getLeaveFormList("process",Long.parseLong(employeeId));
        resp = new ResponseUtils().put("list",list);
        }catch (Exception e){
            e.printStackTrace();
            resp = new ResponseUtils(e.getClass().getSimpleName(),e.getMessage());
        }
        response.getWriter().println(resp.toJsonString());
    }

    public void audit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String result = request.getParameter("result");
        String reason = request.getParameter("reason");
        String formId = request.getParameter("formId");
        String eid = request.getParameter("eid");
        ResponseUtils responseUtils = null;
        try{
            leaveFormServer.audit(Long.parseLong(formId),Long.parseLong(eid),result,reason);
            responseUtils = new ResponseUtils();
        }catch (Exception e){
            e.printStackTrace();
            responseUtils = new ResponseUtils(e.getClass().getSimpleName(),e.getMessage());
        }
        response.getWriter().println(responseUtils.toJsonString());




    }
}
