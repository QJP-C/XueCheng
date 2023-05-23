package com.xuecheng.xuechengplus.base.exception;

import lombok.Data;

/**
 * @author qjp
 * @version 1.0
 * @description 本项目的自定义异常类型
 * @date 2023/3/10 11:10
 */
@Data
public class XueChengPlusException extends RuntimeException {
    private String errMessage;

    public XueChengPlusException() {
    }

    public XueChengPlusException(String message) {
        super(message);
        this.errMessage = message;
    }

    public static void cast(String message){
        throw new XueChengPlusException(message);
    }
    public static void cast(CommonError error){
        throw new XueChengPlusException(error.getErrMessage());
    }
}
