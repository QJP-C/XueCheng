package com.xuecheng.xuechengplusbase.exception;

import java.io.Serializable;

/**
 * @description 错误响应参数包装类  和前端约定返回的异常信息
 * @author qjp
 * @date 2023/3/10 11:07
 * @version 1.0
 */
public class RestErrorResponse implements Serializable {

    private String errMessage;

    public RestErrorResponse(String errMessage){
        this.errMessage= errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }
}
