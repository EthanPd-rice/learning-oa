package com.imooc.oa.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.oa.entity.User;
import com.imooc.oa.service.UserServer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {

    private UserServer userServer = new UserServer();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        Map result = new LinkedHashMap();
        try{
            User user = userServer.checkLogin(username,password);
            result.put("code","0");
            result.put("message","success");
        }catch (Exception e){
            e.printStackTrace();
            result.put("code",e.getClass().getSimpleName());
            result.put("message",e.getMessage());
        }
        ObjectMapper objectMapper = new ObjectMapper();
        //JsonInclude.Include.NON_EMPTY表示只包含属性值不为空的属性。当使用该选项时，
        // ObjectMapper在将Java对象转换为JSON字符串时，只会包含那些属性值不为空的属性，
        // 而对于值为null、空字符串或集合为空的属性，则会被忽略。
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        String json = objectMapper.writeValueAsString(result);
        response.getWriter().println(json);
    }

    public UserServer getUserServer() {
        return userServer;
    }

    public void setUserServer(UserServer userServer) {
        this.userServer = userServer;
    }
}
