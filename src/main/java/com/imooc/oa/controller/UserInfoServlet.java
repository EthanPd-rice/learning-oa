package com.imooc.oa.controller;

import com.imooc.oa.entity.Node;
import com.imooc.oa.service.RbacServer;
import com.imooc.oa.utils.MybatisUtils;
import com.imooc.oa.utils.ResponseUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@WebServlet("/api/user_info")
public class UserInfoServlet extends HttpServlet {
    RbacServer rbacServer = new RbacServer();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uid = request.getParameter("uid");
        List<Node> nodes = rbacServer.selectNodeByUserId(Long.parseLong(uid));
        List<Map> treeList = new ArrayList<>();
        Map module = null;
        for(Node node : nodes){
            if(node.getNodeType() == 1){
                module = new LinkedHashMap();
                module.put("node",node);
                module.put("children",new ArrayList<>());
                treeList.add(module);
            }else if(node.getNodeType() == 2){
                List children = (List)module.get("children");
                children.add(node);
            }
        }
        String json = new ResponseUtils().put("nodeList",treeList).toJsonString();
        response.setContentType("application/json;charset=utf8");
        response.getWriter().println(json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }
}
