package com.xuecheng.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.feigncilent.CheckCodeClient;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.AuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

@Service("password_authservice")
public class PasswordAuthServiceImpl implements AuthService {
    @Resource
    XcUserMapper  xcUserMapper;
    @Resource
    PasswordEncoder passwordEncoder;

    @Resource
    CheckCodeClient checkCodeClient;
    /**
     * @param authParamsDto 认证参数
     * @description 账号密码认证方法
     */
    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        //账号
        String username = authParamsDto.getUsername();

        //远程调用验证码服务接口校验验证码
        String checkcode = authParamsDto.getCheckcode();
        //验证码对应的key
        String checkcodekey = authParamsDto.getCheckcodekey();
        if(StringUtils.isEmpty(checkcode)||StringUtils.isEmpty(checkcodekey)) throw new RuntimeException("请输入验证码");
        Boolean verify = checkCodeClient.verify(checkcodekey, checkcode);
        if (verify==null||!verify) throw new RuntimeException("验证码输入错误");         //验证码错误或 服务降级

        //账号是否存在
        //查询username（账号）查询数据库
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        if (xcUser == null) throw new RuntimeException("账号不存在！");

        //验证密码是否正确
        //正确的密码
        String passwordDb = xcUser.getPassword();
        //输入的密码
        String passwordForm = authParamsDto.getPassword();
        //校验密码
        boolean matches = passwordEncoder.matches(passwordForm, passwordDb);
        if (!matches){
            throw new RuntimeException("密码错误");
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser,xcUserExt);
        return xcUserExt;
    }
}
