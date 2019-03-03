package com.bxl.controller;

import com.bxl.dao.TestGoodsMapper;
import com.bxl.dao.UserInfoMapper;
import com.bxl.entity.TestGoods;
import com.bxl.service.FirstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


/**
 * Created by root on 2017/11/5.
 */

@Controller
@RequestMapping("/frist")
public class FirstController {


    @Autowired
    FirstService firstService;

    @Autowired
    TestGoodsMapper testGoodsMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;


    @RequestMapping(value = "/hello")
    @ResponseBody
    public String hello(){
        return "hello word";
    }



    @RequestMapping("/whoim")
    @ResponseBody
    public Object whoIm(){
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }



//    @Autowired
//    UserService userService;

    @RequestMapping("/list")
    public String userList2(Model model) throws Exception {

        List<Map<String,Object>> resList = new ArrayList();
        for (int i = 0; i < 5; i++) {
            Map<String,Object> map = new HashMap();
            map.put("sort",i+1);
            map.put("name","bxl");
            resList.add(map);
        }
        model.addAttribute("hello","Hello, Spring Boot!");
        model.addAttribute("userList", resList);
        return "/demo/list";
    }


    @RequestMapping(value = "/testDB", method = GET)
    @ResponseBody
    public Map<String, Object> testDB (Integer gid) {
        Map<String, Object> resMap = new HashMap<String, Object>();
        resMap = firstService.findGoodById(gid);
        return resMap;
    }

    @RequestMapping(value = "/testAuto", method = GET)
    @ResponseBody
    public TestGoods testAuto (Integer key) {
        Map<String, Object> resMap = new HashMap<String, Object>();
        TestGoods testGoods = testGoodsMapper.selectByPrimaryKey(key);
        return testGoods;
    }

    @RequestMapping(value = "/deleteById/{id}", method = DELETE)
    @ResponseBody
    public String deleteById (@PathVariable Integer id) {
        testGoodsMapper.deleteByPrimaryKey(id);
        return "删除成功！！！";
    }

    @RequestMapping(value = "/create", method = POST)
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")   //通过注解指定其访问权限
    public String create () {
        System.out.println(1111);
        System.out.println(2222);
        return "创建成功成功！！！";
    }

    @RequestMapping("/getUser")
    public String getUser(){
        String result = "我是测试者！！！";
        return result;
    }


}
