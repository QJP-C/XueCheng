package com.xuecheng.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * 重写了DaoAuthenticationProvider的校验的密码的方法，因为我们统一了认证入口，有一些认证方式不需要校验密码
 */
@Component
public class DaoAuthenticationProviderCustom extends DaoAuthenticationProvider {
    //将我们自己写的userDetail获取方法注入
    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService){
        super.setUserDetailsService(userDetailsService);
    }


    //框架自带的校验密码方法我们重写了  以为不是所有登陆方式都需要校验  需要校验时我们自己去校验
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

    }
}
