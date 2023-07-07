package com.imooc.oa.mapper;

import com.imooc.oa.entity.Notice;
import com.imooc.oa.utils.MybatisUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class NoticeMapperTest {

    @Test
    public void insert() {
        MybatisUtils.executeUpdate(sqlSession -> {
            NoticeMapper noticeMapper = sqlSession.getMapper(NoticeMapper.class);
            Notice notice = new Notice(2l,"测试信息");
            noticeMapper.insert(notice);
            return null;
        });
    }
}