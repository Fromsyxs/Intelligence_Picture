package com.feng.yupicturebackend.service;

import com.feng.yupicturebackend.model.dto.UserRegisterRequest;
import com.feng.yupicturebackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 15298
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-07-10 22:56:55
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户登录req
     * @return 新用户 id
     */
    long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 获取加密后的密码
     * @param userPassword 用户密码
     * @return 加密后的密码
     */
    String getEntryPassword(String userPassword);
}
