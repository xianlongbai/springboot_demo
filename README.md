# SpringSecurity+Mybatis+JWT+Redis实现登录权限认证（前后分离）

## JWT介绍(由3部分组成)
* header：是一个 JSON 对象，描述 JWT 的元数据
* payload：是一个 JSON 对象，用来存放实际需要传递的数据
* signature：是对前两部分的签名，防止数据篡改
> ### ssl证书生成
> ```keytool -genkeypair -alias jwt -keypass 123456 -storepass 123456  -dname "C=baixianlong,ST=beijing,L=beijing,O=talkingdata,OU=talkingdata,CN=chain"  -keyalg RSA -keysize 2048 -validity 3650 -keystore  D://server.keystore```
> ### ssl证书查看
> ```keytool -list -v -keystore  server.keystore```
> ### ssl证书导出
> ```keytool -exportcert -keystore server.keystore -file server.cer -alias serverkey -storepass 11111```

## 本项目实现原理
* 1、在实际开发中，我们只需要在一个单独项目中去颁发token(这里耦合在一起)，而其他的项目中去按照相同的规则去解析这个token，这样就可以实现单点登录，需要注意的就是在解析token时的签名密码要一致或者对应
* 2、未过期和过期的token可以利用黑白名单进行过滤,存于redis或者其它DB中，从而实现对过期和无效的token进行管理
* 3、整合oauth实现授权第三方登录（暂未实现）

## 实现
### 登录认证
* 1、增加jwt依赖，实现一个jwt工具类
* 2、实现一个根据用户名获取用户的接口
* 3、创建一个JwtUser类实现UserDetails接口
* 4、配置用户认证拦截器，如：JWTAuthenticationFilter继承于UsernamePasswordAuthenticationFilter
* 5、配置权限鉴定拦截器，如：JWTAuthenticationFilter继承于BasicAuthenticationFilter
* 6、在SecurityConfig中配置密码加密方式、验证策略(如：.addFilter(new JWTAuthorizationFilter(authenticationManager())) .addFilter(new JWTAuthenticationFilter(authenticationManager())))
* 7、增加注册、登录、测试接口进行认证，springsecurity默认提供的登录接口是post类型：/login  
### 鉴权
* 1、创建token的时候将用户role放入payload
* 2、JWTAuthenticationFilter中通过JwtUser获取用户角色从而创建token
* 3、JWTAuthorizationFilter中创建一个新的token时,增加了权限赋值
* 4、通过在SecurityConfig中配置或者开启注解对接口进行赋权
* 5、新建一个类JWTAuthenticationEntryPoint实现一下接口AuthenticationEntryPoint，进行统一结果处理
### 总结
* 1、自定义各种处理器，如登录成功、登录失败、登出成功、统一异常结果处理等等
* 2、继承OncePerRequestFilter抽象类，实现自己的业务逻辑

