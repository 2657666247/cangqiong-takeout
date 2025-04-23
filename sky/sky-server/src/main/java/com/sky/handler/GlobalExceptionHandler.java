package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler(BaseException.class)
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 处理SQL异常
     * @param exception
     * @return
     */
    @ExceptionHandler
    public Result SQLExceptionHandler(SQLIntegrityConstraintViolationException exception){
        System.out.println(exception);
        String msg=exception.getMessage();
        String[] split = msg.split(" ");
        if (msg.contains("Duplicate entry")){
            String name = split[2];
            log.error("异常信息:{}",name+MessageConstant.ALREADY_EXISTS);
        }else{
            log.error("异常信息:{}",MessageConstant.UNKNOWN_ERROR);
        }
        return Result.error(MessageConstant.ALREADY_EXISTS);
    }

}
