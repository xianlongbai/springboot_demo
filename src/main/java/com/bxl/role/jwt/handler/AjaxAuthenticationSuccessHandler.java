package com.bxl.role.jwt.handler;

import com.alibaba.fastjson.JSON;
import com.bxl.dto.ResultVO;
import com.bxl.enums.ResultEnum;
import com.bxl.role.JwtUser;
import com.bxl.role.jwt.JwtTokenUtils;
import com.bxl.utils.AccessAddressUtil;
import com.bxl.utils.JwtTokenUtil;
import com.bxl.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 2019/2/27.
 */
@Component
public class AjaxAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${token.expirationSeconds}")
    private int expirationSeconds;
    @Value("${token.validTime}")
    private int validTime;
    @Autowired
    private RedisUtil redisUtil;

    private static final Logger log = LoggerFactory.getLogger(AjaxAuthenticationSuccessHandler.class);


    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        //方式一
        /*JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        String role = "";
        // 因为在JwtUser中存了权限信息，可以直接获取，由于只有一个角色就这么干了
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            role = authority.getAuthority();
        }
        String jwtToken = JwtTokenUtils.createToken(userDetails.getUsername(), role,false);
        //将生成的token放入header
        httpServletResponse.setHeader("Authorization", JwtTokenUtils.TOKEN_PREFIX + jwtToken);
        httpServletResponse.getWriter().write(JSON.toJSONString(ResultVO.result(ResultEnum.USER_LOGIN_SUCCESS,jwtToken,true)));*/

        //方式二
        //1、根据ip、role、name生成token
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        String role = "";
        // 因为在JwtUser中存了权限信息，可以直接获取，由于只有一个角色就这么干了
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            role = authority.getAuthority();
        }
        String ip = AccessAddressUtil.getIpAddress(httpServletRequest);
        Map<String,Object> map = new HashMap<>();
        map.put("ip",ip);
        map.put("role",role);
        //token的有效期为5分钟
        String jwtToken = JwtTokenUtil.generateToken(userDetails.getUsername(), expirationSeconds, map);
        //2、设置token刷新的截止时间为
//        Integer expire = validTime*24*60*60*1000;
        Integer expire = 30*60;  //半小时
        //获取请求的ip地址
        String currentIp = AccessAddressUtil.getIpAddress(httpServletRequest);
        redisUtil.setTokenRefresh(jwtToken,userDetails.getUsername(),currentIp,role,expire);
        log.info("用户{}登录成功，信息已保存至redis",userDetails.getUsername());

        //将生成的token放入header
        httpServletResponse.setHeader("Authorization", JwtTokenUtils.TOKEN_PREFIX + jwtToken);
        httpServletResponse.getWriter().write(JSON.toJSONString(ResultVO.result(ResultEnum.USER_LOGIN_SUCCESS,jwtToken,true)));

    }


}
