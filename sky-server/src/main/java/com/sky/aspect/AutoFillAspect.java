package com.sky.aspect;


import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面，实现自动填充功能
 */
@Slf4j
@Aspect
@Component
public class AutoFillAspect {

    /**
     * 拦截的切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointcut(){}

    /**
     * 前置通知，在方法执行前执行
     */
    @Before("autoFillPointcut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行数据填充");

        MethodSignature signature = (MethodSignature)joinPoint.getSignature();//获取方法签名
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获取方法上的注解对象
        OperationType operationType = autoFill.value();//获取注解中的操作类型

        //获取方法参数
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0){
            return;
        }
        Object object = args[0];//获取方法参数对象

        //赋值
        try {
            Method setUpdateTime = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateUser = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
            Method setCreateUser = object.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
            Method setCreateTime = object.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
            if (operationType == OperationType.INSERT){
                setUpdateTime.invoke(object, LocalDateTime.now());
                setUpdateUser.invoke(object, BaseContext.getCurrentId());
                setCreateUser.invoke(object, BaseContext.getCurrentId());
                setCreateTime.invoke(object, LocalDateTime.now());
            }else {
                setUpdateTime.invoke(object, LocalDateTime.now());
                setUpdateUser.invoke(object, BaseContext.getCurrentId());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


}
