package com.feng.yupicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.feng.yupicturebackend.model.dto.user.UserLoginRequest;
import com.feng.yupicturebackend.model.dto.user.UserQueryRequest;
import com.feng.yupicturebackend.model.dto.user.UserRegisterRequest;
import com.feng.yupicturebackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.feng.yupicturebackend.model.vo.LoginUserVO;
import com.feng.yupicturebackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    /**
     * 用户注销
     * @param request 用户注销req
     * @return 用户脱敏后的数据
     */
    Boolean userLogout(HttpServletRequest request);

    /**
     * 获取脱敏后的用户信息
     * @param user user
     * @return userVO
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏后的用户信息列表
     * @param user user
     * @return userVO
     */
    List<UserVO> getUserVOList(List<User> user);

    /**
     * 获取查询条件
     * @param userQueryRequest userQueryRequest
     * @return QueryWrapper<User>
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);


}
