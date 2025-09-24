package com.feng.yupicturebackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : 缘在风里
 * @createDate : 2025/7/10 23:16
 *
 * 用户注册请求
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = -5642036518831707499L;
    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String checkPassword;

}
