package com.bxl.controller;

import com.bxl.entity.UserInfo;
import com.bxl.service.FirstService;
import org.codehaus.groovy.ast.GenericsType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by root on 2018/9/29.
 */

@RestController
@RequestMapping("/common")
public class CommonController {


    @Autowired
    FirstService firstService;


    @RequestMapping(value = "/test")
    @ResponseBody
    public String test(){
        return "我是公开的方法！！！";
    }


    @RequestMapping("/index")
    @ResponseBody
    public Object whoIm(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("username", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        modelAndView.setViewName("index");
        return modelAndView;
    }


    @RequestMapping(value = "/getUserInfo", method =GET)
    @ResponseBody
    public UserInfo getUserInfo (String username) {
        return firstService.findUserInfoByUserName(username);
    }


}
