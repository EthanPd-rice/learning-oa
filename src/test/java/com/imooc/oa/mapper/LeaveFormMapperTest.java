package com.imooc.oa.mapper;

import com.imooc.oa.entity.LeaveForm;
import com.imooc.oa.utils.MybatisUtils;
import org.junit.Test;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;


public class LeaveFormMapperTest {

    @Test
    public void insert() {
        MybatisUtils.executeUpdate(sqlSession -> {
            LeaveFormMapper leaveFormMapper = sqlSession.getMapper(LeaveFormMapper.class);
            LeaveForm leaveForm = new LeaveForm();

            leaveForm.setEmployeeId(1l);
            leaveForm.setFormType(1);
            Date startTime = null;
            Date endTime = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            try {
                startTime = sdf.parse("2023-07-06 08:00:00");
                endTime = sdf.parse("2023-07-08 09:07:27");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            leaveForm.setStartTime(startTime);
            leaveForm.setEndTime(endTime);
            leaveForm.setReason("回家种田");
            leaveForm.setCreateTime(new Date());
            leaveForm.setState("processing");
            leaveFormMapper.insert(leaveForm);
            return null;
        });



    }

    @Test
    public void selectByParams() {
        List<Map> list= (List<Map>) MybatisUtils.executeQuery(sqlSession ->
                sqlSession.getMapper(LeaveFormMapper.class)
                        .selectByParams("process",1l));
        System.out.println(list);

    }
}