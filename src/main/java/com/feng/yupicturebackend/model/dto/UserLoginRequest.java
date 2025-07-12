package com.feng.yupicturebackend.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : 缘在风里
 * @createDate : 2025/7/10 23:16
 *
 * 用户登录请求
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = -5642036518831707499L;
    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;


}
