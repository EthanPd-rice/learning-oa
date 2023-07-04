package com.imooc.oa.service;

import com.imooc.oa.entity.Node;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class RbacServerTest {
    RbacServer rbacServer = new RbacServer();
    @Test
    public void selectNodeByUserId() {
        List<Node> nodes = rbacServer.selectNodeByUserId(1L);
        for(Node node : nodes){
            System.out.println(node.getNodeName());
        }
    }
}