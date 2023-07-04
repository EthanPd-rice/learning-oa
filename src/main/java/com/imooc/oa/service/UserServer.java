package com.imooc.oa.service;

import com.imooc.oa.entity.User;
import com.imooc.oa.mapper.UserMapper;
import com.imooc.oa.service.exception.LoginException;
import com.imooc.oa.utils.Md5Units;

public class UserServer {
    private UserMapper userMapper = new UserMapper();

    public UserMapper getUserMapper() {
        return userMapper;
    }

    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 根据前台输入进行登录校验
     * @param username 前台输入的用户名
     * @param password 前台输入的密码
     * @return 校验通过后，包含对应用户数据的User实体类
     * @throws LoginException 用户登录异常
     */
    public User checkLogin(String username,String password){
        User user = userMapper.selectByUsername(username);
        if(user==null){
            throw new LoginException("用户不存在");
        }
        String md5 = Md5Units.md5Digest(password,user.getSalt());
        if(!md5.equals(user.getPassword())){
            throw new LoginException("密码错误");
        }
        return user;
    }


}
