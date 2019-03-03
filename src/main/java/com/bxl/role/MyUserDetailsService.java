package com.bxl.role;

import com.bxl.dao.UserInfoMapper;
import com.bxl.entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Created by root on 2019/2/19.
 */
@Component
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserInfoMapper userInfoMapper;


    //1、这里可以注入用户信息查询接口

    /**@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //这里可以去数据库去获取用户信息
        if(username.equals("admin")) {
            //假设返回的用户信息如下;
            //注意一下就是 .hasRole("ADMIN"),那么给用户的角色时就要用:ROLE_ADMIN
            JwtUser jwtUser =new JwtUser("admin", "admin", "ROLE_ADMIN01", true,true,true, true);
            return jwtUser;
        }
        return null;
    }*/


    //2、jwt测试
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        UserInfo user = userInfoMapper.findUserInfoByUserName(s);
        return new JwtUser(user);
    }



}
