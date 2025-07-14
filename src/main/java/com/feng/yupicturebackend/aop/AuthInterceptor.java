package com.feng.yupicturebackend.aop;

import com.feng.yupicturebackend.annotation.AuthCheck;
import com.feng.yupicturebackend.exception.BusinessException;
import com.feng.yupicturebackend.exception.ErrorCode;
import com.feng.yupicturebackend.model.entity.User;
import com.feng.yupicturebackend.model.enums.UserRoleEnum;
import com.feng.yupicturebackend.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author: xiafeng
 * @Date: 2025/07/14/11:06
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 从请求中获取登录用户信息
        User loginUser = userService.getLoginUser(request);
        UserRoleEnum enumByValue = UserRoleEnum.getEnumByValue(mustRole);
        // 如果没有指定角色，则直接通过
        if (enumByValue == null) {
            return joinPoint.proceed();
        }
        // 以下代码必须有权限，才能通过
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        if (userRoleEnum == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限");
        }
        // 要求必须由管理员权限，但用户没有管理员权限
        if (UserRoleEnum.ADMIN.equals(enumByValue) && !UserRoleEnum.ADMIN.equals(userRoleEnum)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限");
        }
        //通过权限校验，放行
        return joinPoint.proceed();
    }
}
