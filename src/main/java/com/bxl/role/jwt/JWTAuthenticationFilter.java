package com.bxl.role.jwt;

import com.bxl.entity.UserInfo;
import com.bxl.role.JwtUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by root on 2019/2/24.
 *
 * JWTAuthenticationFilter继承于UsernamePasswordAuthenticationFilter
    该拦截器用于获取用户登录的信息，只需创建一个token并调用authenticationManager.authenticate()
    让spring-security去进行验证就可以了，不用自己查数据库再对比密码了，这一步交给spring去操作。
 */

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    private AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;

        //用来自定义登录接口路径
//        super.setFilterProcessesUrl("/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 从输入流中获取到登录的信息
        try {
            //接受json参数
            //UserInfo loginUser = new ObjectMapper().readValue(request.getInputStream(), UserInfo.class);
            //接受form表单参数
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>());
            setDetails(request,usernamePasswordAuthenticationToken);
            return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            //setDetails();
            //setDetails();
            //return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword(), new ArrayList<>()));
        } catch (Exception e) {
            logger.error("接收并解析用户凭证发生异常！！！");
            throw new RuntimeException(e);
        }
    }

    // 成功验证后调用的方法
    // 如果验证成功，就生成token并返回
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // 查看源代码会发现调用getPrincipal()方法会返回一个实现了`UserDetails`接口的对象
        // 所以就是JwtUser啦
        JwtUser jwtUser = (JwtUser) authResult.getPrincipal();
        System.out.println("jwtUser:" + jwtUser.toString());
        String role = "";
        // 因为在JwtUser中存了权限信息，可以直接获取，由于只有一个角色就这么干了
        Collection<? extends GrantedAuthority> authorities = jwtUser.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            role = authority.getAuthority();
        }
        String token = JwtTokenUtils.createToken(jwtUser.getUsername(), role,false);
        // 返回创建成功的token
        // 但是这里创建的token只是单纯的token
        // 按照jwt的规定，最后请求的格式应该是 `Bearer token`
        //response.setHeader("token", JwtTokenUtils.TOKEN_PREFIX + token);
        response.setHeader("Authorization", JwtTokenUtils.  TOKEN_PREFIX + token);
        //验证成功后回到主页,并带上token
        //new DefaultRedirectStrategy().sendRedirect(request, response, "/common/index");
    }

    // 这是验证失败时候调用的方法
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write("authentication failed, reason: " + failed.getMessage());
    }


}
