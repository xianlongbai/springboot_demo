package com.bxl.role;

import com.bxl.entity.UserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by root on 2019/2/19.
 *
 */
public class JwtUser implements Serializable, UserDetails {

    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private String role;
    private boolean accountNonExpired;   //账号是否过期
    private boolean accountNonLocked;    //账号是否被锁
    private boolean credentialsNonExpired;     //证书是否过期
    private boolean enabled;     //是否开启

    //用来存放用户权限
    private Collection<? extends GrantedAuthority> authorities;

    public JwtUser(){}

    public JwtUser(UserInfo userInfo){
        this.username = userInfo.getUsername();
        this.password = userInfo.getPassword();
        this.authorities = Collections.singleton(new SimpleGrantedAuthority(userInfo.getRole()));
        this.accountNonExpired = userInfo.getAccountNonExpired();
        this.accountNonLocked = userInfo.getAccountNonLocked();
        this.credentialsNonExpired = userInfo.getCredentialsNonExpired();
        this.enabled = userInfo.getEnabled();
        this.authorities = Collections.singleton(new SimpleGrantedAuthority(userInfo.getRole()));
    }

    public JwtUser(String username, String password, String role, boolean accountNonExpired, boolean accountNonLocked,
                   boolean credentialsNonExpired, boolean enabled) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
    }

    public JwtUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;

    }

//提前设定好了权限
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return AuthorityUtils.commaSeparatedStringToAuthorityList(role);
//    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "JwtUser{username='" + username + '\'' + ", password='" + password + '\'' + ", authorities=" + authorities + '}';
    }

}
