package com.feng.yupicturebackend.controller;

import com.feng.yupicturebackend.common.BaseResponse;
import com.feng.yupicturebackend.common.ResultUtils;
import com.feng.yupicturebackend.exception.ErrorCode;
import com.feng.yupicturebackend.exception.ThrowUtils;
import com.feng.yupicturebackend.model.dto.UserRegisterRequest;
import com.feng.yupicturebackend.model.entity.User;
import com.feng.yupicturebackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author : 缘在风里
 * @createDate : 2025/7/11 0:09
 */

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求体
     * @return 用户id
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody  UserRegisterRequest userRegisterRequest) {
        log.info("register user: {}", userRegisterRequest);
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        long result = userService.userRegister(userRegisterRequest);
        return ResultUtils.success(result);
    }
}
