package com.imooc.oa.utils;

import jdk.nashorn.internal.ir.FunctionCall;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.function.Function;

public class MybatisUtils {
    //利用static属于类，不属于对象，且全局唯一
    private static SqlSessionFactory sqlSessionFactory = null;
    //利用静态块在初始化的时候实例化sqlSessionFactory
    static{
        Reader reader = null;
        try {
            //读取mybatis-config.xml配置
            reader = Resources.getResourceAsReader("mybatis-config.xml");
            //构建sqlSessionFactory
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        } catch (IOException e) {
            //初始化错误时，通过抛出异常ExceptionInInitializerError通知调用者
            throw new ExceptionInInitializerError(e);
        }

    }

    /**
     * 执行SELECT查询SQL
     * @param func 要执行查询语句的代码块
     * @return 查询结果
     */
    public static Object executeQuery(Function<SqlSession,Object> func){
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try{
            Object obj = func.apply(sqlSession);
            return obj;
        }finally {
            sqlSession.close();
        }

    }

    /**
     * 执行INSERT/UPDATE/DELETE写操作SQL
     * @param func 要执行的写操作代码
     * @return 写操作后返回的结果
     */
    public static Object executeUpdate(Function<SqlSession,Object> func){
        SqlSession sqlSession = sqlSessionFactory.openSession(false);
        try {
            Object obj = func.apply(sqlSession);
            sqlSession.commit();
            return obj;
        }catch (Exception e) {
            sqlSession.rollback();
            throw e;
        }finally {
            sqlSession.close();
        }
    }
}
