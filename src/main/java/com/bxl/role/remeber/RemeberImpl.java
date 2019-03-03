package com.bxl.role.remeber;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Created by root on 2019/2/20.
 */
//@Component
public class RemeberImpl {

//    @Autowired
//    private DataSource dataSource;   //是在application.properites


    /**
     * 记住我功能的token存取器配置
     * @return
     */
//    @Bean
//    public PersistentTokenRepository persistentTokenRepository() {
//        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
//        tokenRepository.setDataSource(dataSource);
//        return tokenRepository;
//    }

}
