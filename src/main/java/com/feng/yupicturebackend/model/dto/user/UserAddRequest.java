package com.feng.yupicturebackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: xiafeng
 * @Date: 2025/07/14/14:16
 *
 * @Description: 管理员添加用户请求体
 */
@Data
public class UserAddRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色: user, admin
     */
    private String userRole;

    private static final long serialVersionUID = 1L;
}

