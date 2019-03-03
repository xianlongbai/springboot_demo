package com;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@SpringBootApplication
@MapperScan(basePackages = "com.bxl.dao")
public class BootCloudApplication {

	public static void main(String[] args) {
		SpringApplication.run(BootCloudApplication.class, args);
	}



}
