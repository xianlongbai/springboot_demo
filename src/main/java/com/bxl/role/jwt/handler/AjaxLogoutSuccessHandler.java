package com.bxl.role.jwt.handler;

import com.alibaba.fastjson.JSON;
import com.bxl.dto.ResultVO;
import com.bxl.enums.ResultEnum;
import com.bxl.utils.DateUtil;
import com.bxl.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by root on 2019/2/27.
 */

@Component
public class AjaxLogoutSuccessHandler implements LogoutSuccessHandler {


    @Autowired
    private RedisUtil redisUtil;

    private static final Logger log = LoggerFactory.getLogger(AjaxLogoutSuccessHandler.class);

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");

        String authHeader = httpServletRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            final String authToken = authHeader.substring("Bearer ".length());
            //将token放入黑名单中
            redisUtil.hset("blacklist", authToken, DateUtil.getTime());
            log.info("token：{}已加入redis黑名单",authToken);
        }
        httpServletResponse.getWriter().write(JSON.toJSONString(ResultVO.result(ResultEnum.USER_LOGOUT_SUCCESS,true)));
    }

}
