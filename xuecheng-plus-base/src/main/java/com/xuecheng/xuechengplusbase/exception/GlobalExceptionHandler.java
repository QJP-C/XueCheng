package com.xuecheng.xuechengplusbase.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description TODO
 * @author qjp
 * @date 2023/3/10 11:22
 * @version 1.0
 */
@Slf4j
@RestControllerAdvice //控制器增强
public class GlobalExceptionHandler {
    /**
    * @description 对项目的自定义异常进行处理
    * @param e
    * @return com.xuecheng.xuechengplusbase.exception.RestErrorResponse
    * @author qjp
    * @date 2023/3/10 11:30
    */
    @ExceptionHandler(XueChengPlusException.class)          //捕获指定异常
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)       //响应状态码500
    public RestErrorResponse customException(XueChengPlusException e){
        //记录异常
        log.error("系统异常{}",e.getErrMessage(),e);
        //解析出异常信息
        String errMessage = e.getErrMessage();
        return new RestErrorResponse(errMessage);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(Exception e){
        //记录异常
        log.error("系统异常{}",e.getMessage(),e);
        return new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        List<String> erros = bindingResult.getFieldErrors().stream().map(item -> item.getDefaultMessage()).collect(Collectors.toList());

        //将List中的错误信息拼接起来
        String errMessage = StringUtils.join(erros, ",");

        //记录异常
        log.error("系统异常{},{}",e.getMessage(),errMessage);
        return new RestErrorResponse(errMessage);
    }
}
