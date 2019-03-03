package com.bxl.filter;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Created by root on 2019/2/20.
 */

//@Configuration
//public class DataSourcConfig {
//
//    @Bean(name = "dataSource")
//    @ConfigurationProperties(prefix = "spring.datasource")
//    @Primary
//    public DataSource dataSource() throws SQLException {
//        //jdbc的方式
//        return DataSourceBuilder.create().build();
//    }
//}
