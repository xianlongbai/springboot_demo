package com.bxl.handler.mybatishandler;

import com.fasterxml.jackson.databind.JavaType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by root on 2018/9/19.
 */


//此处如果不用注解指定jdbcType, 那么，就可以在配置文件中通过"jdbcType"属性指定，
// 同理， javaType 也可通过 @MappedTypes指定
//javaType必须指定，jdbcType可以不指定
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(Integer.class)
public class ExampleTypeHandler extends BaseTypeHandler<String> {


    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String s, JdbcType jdbcType) throws SQLException {
        System.out.println(111);
        ps.setString(i, s);
    }

    @Override
    public String getNullableResult(ResultSet rs, String s) throws SQLException {
        System.out.println(222);
        return rs.getString(s);
    }

    @Override
    public String getNullableResult(ResultSet rs, int i) throws SQLException {
        System.out.println(333);
        return rs.getString(i);
    }

    @Override
    public String getNullableResult(CallableStatement cs, int i) throws SQLException {
        System.out.println(444);
        return cs.getString(i);
    }

}
