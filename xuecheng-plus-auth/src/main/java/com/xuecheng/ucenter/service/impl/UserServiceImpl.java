package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.mapper.XcMenuMapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcMenu;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义security框架的获取用户信息方法
 */
@Slf4j
@Component
public class UserServiceImpl implements UserDetailsService {

    @Resource //注入spring容器
    ApplicationContext applicationContext;

    @Resource
    XcUserMapper xcUserMapper;

    @Resource
    XcMenuMapper xcMenuMapper;

    //传入的请求认证的参数就是AuthParamsDto
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        //将传入的json串转成AuthParamsDto对象
        AuthParamsDto authParamsDto = null;
        try {
            authParamsDto = JSON.parseObject(s,AuthParamsDto.class);
        } catch (Exception e) {
            throw new RuntimeException("请求认证参数不符合要求");
        }

        //认证类型，有password wx。。。
        String authType = authParamsDto.getAuthType();
        //根据认证类型 从spring中取出指定的bean
        String beanName =authType+"_authservice";
        AuthService authService = applicationContext.getBean(beanName, AuthService.class);
        //调用统一的该类型的方法
        XcUserExt xcUserExt = authService.execute(authParamsDto);
        //封装 XcUserExt 用户信息为 UserDetails

        String username = authParamsDto.getUsername();
        //查询username（账号）查询数据库
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));

        //查询到用户不存在，要返回null即可，spring security 框架会抛出异常用户不存在
        if (xcUser == null) return null;

        //如果查到了用户拿到正确的密码，最终封装成一个UserDetails对象给 spring security 框架返回，由框架进行密码的比对

        //根据userDetails生成令牌
        return getUserPrincipal(xcUserExt);

    }

    /**
     * 查询用户信息
     * @param xcUser
     * @return
     */
    private UserDetails getUserPrincipal(XcUserExt xcUser) {
        String password = xcUser.getPassword();
        //根据用户id查询用户的权限
        String[] authorities = {"test"};
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(xcUser.getId());
        if (xcMenus.size()>0){
            ArrayList<String> permissions = new ArrayList<>();
            xcMenus.forEach(m-> permissions.add(m.getCode()));//拿到用户所拥有的权限
            authorities = permissions.toArray(new String[0]);
        }
        //去掉敏感数据
        xcUser.setPassword(null);
        //将用户信息转为Json
        String userJson = JSON.toJSONString(xcUser);
        return User.withUsername(userJson).password(password).authorities(authorities).build();
    }
}
