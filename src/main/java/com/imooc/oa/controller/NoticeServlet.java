package com.imooc.oa.controller;

import com.imooc.oa.entity.Notice;
import com.imooc.oa.service.NoticeServer;
import com.imooc.oa.utils.ResponseUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/notice/list")
public class NoticeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String employeeId = request.getParameter("eid");
        NoticeServer noticeServer = new NoticeServer();
        ResponseUtils responseUtils = null;
        try{
            List<Notice> noticeList = noticeServer.selectByReceiverId(Long.parseLong(employeeId));
            responseUtils = new ResponseUtils().put("list",noticeList);
        }catch (Exception e){
            e.printStackTrace();
            responseUtils = new ResponseUtils(e.getClass().getSimpleName(),e.getMessage());
        }
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().println(responseUtils.toJsonString());
    }
}
