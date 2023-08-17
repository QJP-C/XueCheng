package com.xuecheng.ucenter.service.impl;

import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.service.AuthService;
import org.springframework.stereotype.Service;

@Service("wx_authservice")
public class WxAuthServiceImpl implements AuthService {
    /**
     * @param authParamsDto 认证参数
     * @description 微信扫码认证方法
     */
    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        return null;
    }
}
