package com.imooc.oa.service;

import com.imooc.oa.entity.Notice;
import com.imooc.oa.mapper.NoticeMapper;
import com.imooc.oa.utils.MybatisUtils;

import java.util.List;

public class NoticeServer {
    public List<Notice> selectByReceiverId(Long receiverId){
        return (List<Notice>) MybatisUtils.executeQuery(sqlSession -> {
            return sqlSession.getMapper(NoticeMapper.class).selectByReceiverId(receiverId);
        });
    }
}
