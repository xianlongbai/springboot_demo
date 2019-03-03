package com.bxl.role.detail;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by root on 2019/2/20.
 * 权限控制接口
 */
public interface RbacService {

    boolean hasPermission(HttpServletRequest request, Authentication authentication);
}
