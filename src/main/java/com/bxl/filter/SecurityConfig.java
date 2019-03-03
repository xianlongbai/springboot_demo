package com.bxl.filter;

import com.bxl.role.MyAuthenticationLogoutHander;
import com.bxl.role.MyAuthenticationProvider;
import com.bxl.role.MyUserDetailsService;
import com.bxl.role.jwt.JWTAuthenticationEntryPoint;
import com.bxl.role.jwt.JWTAuthenticationFilter;
import com.bxl.role.jwt.JWTAuthorizationFilter;
import com.bxl.role.jwt.handler.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Created by root on 2018/9/29.
 */
@Configuration
@EnableWebSecurity
//为什么要配置这个? 作用：开启注解赋权
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyAuthenticationProvider myprovider;  //注入我们自己的AuthenticationProvider
    @Autowired
    private AuthenticationSuccessHandler myAuthenticationSuccessHandler;
    @Autowired
    private AuthenticationFailureHandler myAuthenticationFailHander;
    @Autowired
    private MyAuthenticationLogoutHander myAuthenticationLogoutHander;

    @Autowired
    private MyUserDetailsService userDetailsService;
    @Autowired
    private DataSource dataSource;   //是在application.properites


    @Autowired
    AjaxAuthenticationEntryPoint authenticationEntryPoint;//未登陆时返回 JSON 格式的数据给前端（否则为 html）
    @Autowired
    AjaxAuthenticationSuccessHandler authenticationSuccessHandler; //登录成功返回的 JSON 格式数据给前端（否则为 html）
    @Autowired
    AjaxAuthenticationFailureHandler authenticationFailureHandler; //登录失败返回的 JSON 格式数据给前端（否则为 html）
    @Autowired
    AjaxLogoutSuccessHandler logoutSuccessHandler;//注销成功返回的 JSON 格式数据给前端（否则为 登录时的 html）
    @Autowired
    AjaxAccessDeniedHandler accessDeniedHandler;//无权访问返回的 JSON 格式数据给前端（否则为 403 html 页面）

//    @Autowired
//    JWTAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    JWTAuthorizationFilter jwtAuthorizationFilter;


    /**
     * 配置TokenRepository
     *
     * @return
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        // 配置数据源
        jdbcTokenRepository.setDataSource(dataSource);
        // 第一次启动的时候自动建表（可以不用这句话，自己手动建表，源码中有语句的）
//         jdbcTokenRepository.setCreateTableOnStartup(true);
        return jdbcTokenRepository;
    }






//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        super.configure(web);
//    }
    //no.3
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        //4
        //整合jwt
        http
            //cors跨域共享机制
            .cors()
            .and()
            .csrf().disable()
            .authorizeRequests()
            // 测试用资源，需要验证了的用户才能访问
                .antMatchers("/common/**").permitAll()
//                .antMatchers(HttpMethod.DELETE,"/frist/deleteById/**").hasAuthority("ADMIN")
            .antMatchers(HttpMethod.DELETE,"/frist/**").hasRole("ADMIN")
            //其它资源都不需要验证
            .anyRequest().authenticated()
            .and()
            .formLogin()  //开启登录, 定义当需要用户登录时候，转到的登录页面(这种情况下不支持接受json格式参数)
//                .loginPage("/test/login.html")
//                .loginProcessingUrl("/login")
            .successHandler(authenticationSuccessHandler) // 登录成功
            .failureHandler(authenticationFailureHandler) // 登录失败
            .permitAll()
            .and()
            .logout()//默认注销行为为logout
            .logoutUrl("/logout")
            .logoutSuccessHandler(logoutSuccessHandler)
            .permitAll()
            .and()
//            .addFilter(new JWTAuthorizationFilter(authenticationManager()))
//            .addFilter(new JWTAuthenticationFilter(authenticationManager()))
            // 不需要session
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            //统一结果处理
            .and()
            .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
            ;


        // 记住我
        http.rememberMe()
                .rememberMeParameter("remember-me")
                .userDetailsService(userDetailsService)
                .tokenValiditySeconds(1000);

        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler); // 无权访问 JSON 格式的数据
        http
//            .addFilterBefore(new JWTAuthenticationFilter(authenticationManager()),UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);





        //3
        //自定义登录逻辑处理,增加了rememberMe功能
        /**http
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login/form")
                .successHandler(myAuthenticationSuccessHandler)
                .failureHandler(myAuthenticationFailHander)
                .permitAll()
                .and()
                .logout().permitAll().invalidateHttpSession(true)  //logout()默认接口为/logout
                .deleteCookies("JSESSIONID").logoutSuccessHandler(myAuthenticationLogoutHander)
                .and()
                .rememberMe()
                .rememberMeParameter("remember-me").userDetailsService(userDetailsService)
                .tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(60)  //配置Cookie过期时间为60秒
                .and()
                .authorizeRequests()
                .antMatchers("/common").permitAll()  //表示不需要权限
//                .antMatchers("/common/test").permitAll()  //表示不需要权限
//                .antMatchers("/frist/whoim").hasRole("ADMIN")   //这就表示/whoim的这个资源需要有ROLE_ADMIN的这个角色才能访问。不然就会提示拒绝访问
//                .anyRequest().authenticated()   //必须经过认证以后才能访问
                //其中 @rbacService 就是我们自己声明的bean，在RbacServiceImpl实现类的头部注解中。
                .anyRequest().access("@rbacService.hasPermission(request,authentication)")
                .and()
                .csrf().disable();
            */

        //2
        //自定义登录逻辑处理
        /*http
            .formLogin()
            .loginPage("/login")
            .loginProcessingUrl("/login/form")
            .successHandler(myAuthenticationSuccessHandler)
            .failureHandler(myAuthenticationFailHander)
            .permitAll()
            .and()
            .authorizeRequests()
            .antMatchers("/common/test").permitAll()  //表示不需要权限
            .antMatchers("/frist/whoim").hasRole("ADMIN")   //这就表示/whoim的这个资源需要有ROLE_ADMIN的这个角色才能访问。不然就会提示拒绝访问
            .anyRequest().authenticated()   //必须经过认证以后才能访问
            .and()
            .csrf().disable();*/



        //1
        /*http.formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login/form")
                .failureUrl("/login-error")
                .permitAll()  //表单登录，permitAll()表示这个不需要验证 登录页面，登录失败页面
                .and()
                //其他地址的访问均需验证权限
                .authorizeRequests()
                .antMatchers("/common/test").permitAll()
                .anyRequest().authenticated()
                .and()
                //禁用跨站请求伪造
                .csrf().disable();*/
        //允许所有用户访问"/"和"/home"
        /*http.authorizeRequests()
                .antMatchers("/common/test").permitAll()
                //其他地址的访问均需验证权限
                .anyRequest().authenticated()
                .and()
                .formLogin()
                //指定登录页是"/login"
                .loginPage("/login")
                //指定登录跳转链接action
                .loginProcessingUrl("/login/form")
                //指定登录失败跳转页
                .failureUrl("/login-error")
                //默认登录成功跳转页
                .defaultSuccessUrl("/frist/list")//登录成功后默认跳转到"/hello"
                .permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/home")//退出登录后的默认url是"/home"
                .permitAll();*/

    }

/************************************************用户管理************************************************************/
    //方式一：
    //指定可以等的用户名密码
  /*  @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("admin")
                .password("admin")
                .roles("USER");
    }*/

    //方式二：
    //重写以下方法
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//            .withUser("admin").password("admin").roles("USER")
//            .and()
//            .withUser("test").password("test").roles("test");
//    }

    //方式三：
    //以上两种方式只供玩玩,接下来自定义
    /**
     * no.2
    spring security的原理，spring security的原理就是使用很多的拦截器对URL进行拦截，以此来管理登录验证和用户权限验证。
        用户登陆，会被AuthenticationProcessingFilter拦截，调用AuthenticationManager的实现，
        而且AuthenticationManager会调用ProviderManager来获取用户验证信息（不同的Provider调用的服务不同，
        因为这些信息可以是在数据库上，可以是在LDAP服务器上，可以是xml配置文件上等），
        如果验证通过后会将用户的权限信息封装一个User放到spring的全局缓存SecurityContextHolder中，以备后面访问资源时使用。
        所以我们要自定义用户的校验机制的话，我们只要实现自己的AuthenticationProvider就可以了。
        在用AuthenticationProvider 这个之前，我们需要提供一个获取用户信息的服务，实现  UserDetailsService 接口
        重点：
        用户名密码->(Authentication(未认证)  ->  AuthenticationManager ->AuthenticationProvider->UserDetailService->UserDetails->Authentication(已认证）
     */
   @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.authenticationProvider(myprovider);
//        auth
//        .inMemoryAuthentication()
//            .withUser("admin").password("123456").roles("USER")
//            .and()
//            .withUser("test").password("test123").roles("ADMIN");
    }

    //方式四
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
//    }


    //no.1 加密密码的，安全第一嘛~
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }



    // 处理密码加密解密逻辑
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }


    //no.4   web安全相关
    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }


    //设置 跨域请求参数，处理跨域请求
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }




}
