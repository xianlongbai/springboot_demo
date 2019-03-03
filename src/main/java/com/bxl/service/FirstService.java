package com.bxl.service;

import com.bxl.dao.FirstDao;
import com.bxl.dao.UserInfoMapper;
import com.bxl.entity.Goods;
import com.bxl.entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 2019/2/21.
 */
@Service
public class FirstService{

    @Autowired
    private FirstDao firstDao;
    @Autowired
    private UserInfoMapper userInfoMapper;


    public Map<String,Object> findGoodById(Integer gid) {
        Map<String,Object> res = new HashMap<>();
        Goods goods = firstDao.findGoodById(gid);
        res.put("result",goods);
        return res;
    }


    public UserInfo findUserInfoByUserName(String username) {
        return userInfoMapper.findUserInfoByUserName(username);
    }

}
