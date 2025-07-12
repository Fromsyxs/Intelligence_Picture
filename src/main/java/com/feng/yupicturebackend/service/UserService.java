package com.feng.yupicturebackend.service;

import com.feng.yupicturebackend.model.dto.UserLoginRequest;
import com.feng.yupicturebackend.model.dto.UserRegisterRequest;
import com.feng.yupicturebackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.feng.yupicturebackend.model.vo.LoginUserVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author 15298
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-07-10 22:56:55
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册req
     * @return 新用户 id
     */
    long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录req
     * @param request req
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

    /**
     * 获取加密后的密码
     * @param userPassword 用户密码
     * @return 加密后的密码
     */
    String getEntryPassword(String userPassword);

    /**
     * 获取脱敏类的用户信息
     * @param user 用户
     * @return 脱敏后的用户信息
     */
    LoginUserVO getUserLoginVO(User user);

    /**
     * 获取当前登陆用户
     * @param request 获取请求
     * @return user
     */
    User getLoginUser(HttpServletRequest request);
}
