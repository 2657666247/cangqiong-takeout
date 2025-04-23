package com.sky.aspectj;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;

@Aspect
@Slf4j
@Component
public class AutoFillAspect {
    @Pointcut("@annotation(com.sky.annotation.AutoFill)" + "&& execution(* com.sky.mapper.*.*(..))")
    public void pointCount() {
    }
    @Before("pointCount()")
    public void beforeAutoFillAspect(JoinPoint joinPoint) throws InvocationTargetException, IllegalAccessException {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        //获取方法中的注解
        AutoFill autoFill = method.getAnnotation(AutoFill.class);
        //获取注解中的值
        OperationType value = autoFill.value();

        HashMap<Object, Method[]> methodHashMap = new HashMap<>();
        //存放所有方法和参数对象
        for (Object arg : args) {
            Class<?> argClass = arg.getClass();
            Method[] methods = argClass.getDeclaredMethods();
            methodHashMap.put(arg,methods);
        }
        //判断是更新操作还是添加操作
        if (value.equals(OperationType.INSERT)) {
            for (Map.Entry<Object, Method[]> entry : methodHashMap.entrySet()) {
                Object key = entry.getKey();
                for (Method method1 : entry.getValue()) {
                    if (method1.getName().equals(AutoFillConstant.SET_UPDATE_TIME) || method.getName().equals(AutoFillConstant.SET_CREATE_TIME)) {
                        method1.invoke(key, LocalDateTime.now());
                    }
                    if (method1.getName().equals(AutoFillConstant.SET_CREATE_USER) || method.getName().equals(AutoFillConstant.SET_UPDATE_USER)) {
                        method1.invoke(key, BaseContext.getCurrentId());
                    }
                }

            }
        } else if (value.equals(OperationType.UPDATE)) {
            for (Map.Entry<Object, Method[]> entry : methodHashMap.entrySet()) {
                Object key = entry.getKey();
                for (Method method1 : entry.getValue()) {
                    if (method1.getName().equals(AutoFillConstant.SET_UPDATE_TIME)) {
                        method1.invoke(key, LocalDateTime.now());
                    }
                    if (method1.getName().equals(AutoFillConstant.SET_UPDATE_USER)) {
                        method1.invoke(key, BaseContext.getCurrentId());
                    }
                }

            }
        } else {
            throw new IllegalArgumentException("自动填充操作类型参数异常！");
        }
    }

}
