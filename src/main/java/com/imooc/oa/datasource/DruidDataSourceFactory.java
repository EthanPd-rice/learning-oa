package com.imooc.oa.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.datasource.DataSourceFactory;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;

import javax.sql.DataSource;
import java.sql.SQLException;

public class DruidDataSourceFactory extends UnpooledDataSourceFactory {
    public DruidDataSourceFactory(){
        this.dataSource = new DruidDataSource();
    }
    @Override
    public DataSource getDataSource(){
        try {
            ((DruidDataSource)this.dataSource).init();
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }
        return this.dataSource;
    }
}
