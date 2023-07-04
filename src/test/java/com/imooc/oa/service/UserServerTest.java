package com.imooc.oa.service;

import com.imooc.oa.entity.User;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserServerTest {
    private UserServer userServer = new UserServer();
    @Test
    public void checkLogin() {
        User user = userServer.checkLogin("m8","test");
        System.out.println(user);
    }
}