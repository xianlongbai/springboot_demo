package com.bxl.role.jwt;

import com.alibaba.fastjson.JSON;
import com.bxl.dto.ResultVO;
import com.bxl.enums.ResultEnum;
import com.bxl.filter.SecurityConfig;
import com.bxl.role.MyUserDetailsService;
import com.bxl.role.jwt.handler.AjaxAuthenticationSuccessHandler;
import com.bxl.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 2019/2/24.
 * 验证成功当然就是进行鉴权了，每一次需要权限的请求都需要检查该用户是否有该权限去操作该资源，当然这也是框架帮我们做的，
 * 那么我们需要做什么呢？很简单，只要告诉spring-security该用户是否已登录，是什么角色，拥有什么权限就可以了。
 */
@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter {


    private static final Logger log = LoggerFactory.getLogger(JWTAuthorizationFilter.class);

    @Value("${token.expirationSeconds}")
    private int expirationSeconds;
    @Value("${token.validTime}")
    private int validTime;

    @Autowired
    MyUserDetailsService myUserDetailsService;
    @Autowired
    private RedisUtil redisUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
    //方式一
    /*        String tokenHeader = request.getHeader(JwtTokenUtils.TOKEN_HEADER);
        // 如果请求头中没有Authorization信息则直接放行了
        if (tokenHeader == null || !tokenHeader.startsWith(JwtTokenUtils.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        // 如果请求头中有token，则进行解析，并且设置认证信息
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(tokenHeader));
        super.doFilterInternal(request, response, chain);
        //chain.doFilter(request, response);*/

    //方式二

            String authHeader = request.getHeader("Authorization");
            //获取请求的ip地址
            String currentIp = AccessAddressUtil.getIpAddress(request);

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String authToken = authHeader.substring("Bearer ".length());
                String username = JwtTokenUtil.parseToken(authToken, "_secret");
                String ip = CollectionUtil.getMapValue(JwtTokenUtil.getClaims(authToken), "ip");
                String role = CollectionUtil.getMapValue(JwtTokenUtil.getClaims(authToken), "role");
                //进入黑名单验证
                if (redisUtil.isBlackList(authToken)) {
                    log.info("用户：{}的token：{}在黑名单之中，拒绝访问", username, authToken);
                    response.getWriter().write(JSON.toJSONString(ResultVO.result(ResultEnum.TOKEN_IS_BLACKLIST, false)));
                    return;
                }

                //判断token是否过期
            /*
             * 过期的话，从redis中读取有效时间（比如七天登录有效），再refreshToken（根据以后业务加入，现在直接refresh）
             * 同时，已过期的token加入黑名单
             */
                if (redisUtil.hasKey(authToken)) {//判断redis是否有保存
                    String expirationTime = redisUtil.hget(authToken, "expirationTime").toString();
                    if (JwtTokenUtil.isExpiration(expirationTime)) {
                        //获得redis中用户的token刷新时效
                        String tokenValidTime = (String) redisUtil.getTokenValidTimeByToken(authToken);
                        String currentTime = DateUtil.getTime();
                        //这个token已作废，加入黑名单
                        log.info("{}已作废，加入黑名单", authToken);
                        redisUtil.hset("blacklist", authToken, DateUtil.getTime());

                        if (DateUtil.compareDate(currentTime, tokenValidTime)) {

                            //超过有效期，不予刷新
                            log.info("{}已超过有效期，不予刷新", authToken);
                            response.getWriter().write(JSON.toJSONString(ResultVO.result(ResultEnum.LOGIN_IS_OVERDUE, false)));
                            return;
                        } else {//仍在刷新时间内，则刷新token，放入请求头中
                            String usernameByToken = (String) redisUtil.getUsernameByToken(authToken);
                            username = usernameByToken;//更新username

                            ip = (String) redisUtil.getIPByToken(authToken);//更新ip
                            role = (String) redisUtil.getRoleByToken(authToken);//更新ip

                            //获取请求的ip地址
                            Map<String, Object> map = new HashMap<>();
                            map.put("ip", ip);
                            map.put("role",role);
                            String jwtToken = JwtTokenUtil.generateToken(usernameByToken, expirationSeconds, map);

                            //更新redis
//                            Integer expire = validTime * 24 * 60 * 60 * 1000;//刷新时间
                            Integer expire = 30 * 60;//刷新时间
                            redisUtil.setTokenRefresh(jwtToken, usernameByToken, ip,role,expire);
                            //删除旧的token保存的redis
                            redisUtil.deleteKey(authToken);
                            //新的token保存到redis中
                            redisUtil.setTokenRefresh(jwtToken, username, ip,role,expire);

                            log.info("redis已删除旧token：{},新token：{}已更新redis", authToken, jwtToken);
                            authToken = jwtToken;//更新token，为了后面
                            response.setHeader("Authorization", "Bearer " + jwtToken);
                        }
                    }

                }

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                /*
                 * 加入对ip的验证
                 * 如果ip不正确，进入黑名单验证
                 */
                    if (!StringUtil.equals(ip, currentIp)) {//地址不正确
                        log.info("用户：{}的ip地址变动，进入黑名单校验", username);
                        //进入黑名单验证
                        if (redisUtil.isBlackList(authToken)) {
                            log.info("用户：{}的token：{}在黑名单之中，拒绝访问", username, authToken);
                            response.getWriter().write(JSON.toJSONString(ResultVO.result(ResultEnum.TOKEN_IS_BLACKLIST, false)));
                            return;
                        }
                        //黑名单没有则继续，如果黑名单存在就退出后面
                    }

                    UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);
                    if (userDetails != null) {
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        chain.doFilter(request, response);
    }





    // 这里从token中获取用户信息并新建一个token
    private UsernamePasswordAuthenticationToken getAuthentication(String tokenHeader) {
        String token = tokenHeader.replace(JwtTokenUtils.TOKEN_PREFIX, "");
        String username = JwtTokenUtils.getUsername(token);
//        String role = JwtTokenUtils.getUserRole(token);
        Map<String, Object> claims = JwtTokenUtils.getClaims(token);
        String role = claims.get("rol").toString();
        if (username != null) {
//            return new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
            return new UsernamePasswordAuthenticationToken(username, null, Collections.singleton(new SimpleGrantedAuthority(role)));
        }
        return null;
    }

}
