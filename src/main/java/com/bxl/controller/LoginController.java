package com.bxl.controller;

import com.bxl.dao.UserInfoMapper;
import com.bxl.entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by root on 2018/9/29.
 */

@Controller
@RequestMapping("/")
public class LoginController {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

//    @RequestMapping("/login")
//    public String userLogin() {
//        return "demo-sign";
//    }

    @RequestMapping("/login-error")
    public String loginError(){
        return "login-error";
    }

//    @RequestMapping("/loginout")
//    public String loginOut(){
//        return "login-out";
//    }

    @PostMapping("/register")
    @ResponseBody
    public String registerUser(@RequestBody Map<String, String> registerUser) {
        UserInfo user = new UserInfo();
        user.setUsername(registerUser.get("username"));
        // 记得注册的时候把密码加密一下
        user.setPassword(bCryptPasswordEncoder.encode(registerUser.get("password")));
        user.setRole("ROLE_USER");
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        int insert = userInfoMapper.insert(user);
        return "register success !!!";
    }



}
