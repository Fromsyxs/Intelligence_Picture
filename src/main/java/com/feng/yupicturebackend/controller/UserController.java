package com.feng.yupicturebackend.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.feng.yupicturebackend.annotation.AuthCheck;
import com.feng.yupicturebackend.common.BaseResponse;
import com.feng.yupicturebackend.common.DeleteResponse;
import com.feng.yupicturebackend.common.ResultUtils;
import com.feng.yupicturebackend.constant.UserConstant;
import com.feng.yupicturebackend.exception.BusinessException;
import com.feng.yupicturebackend.exception.ErrorCode;
import com.feng.yupicturebackend.exception.ThrowUtils;
import com.feng.yupicturebackend.model.dto.user.*;
import com.feng.yupicturebackend.model.entity.User;
import com.feng.yupicturebackend.model.vo.LoginUserVO;
import com.feng.yupicturebackend.model.vo.UserVO;
import com.feng.yupicturebackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        log.info("user login: {}", userLoginRequest);
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        LoginUserVO loginUserVO = userService.userLogin(userLoginRequest, request);
        return ResultUtils.success(loginUserVO);
    }

    @GetMapping("get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        log.info("get login user");
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.getUserLoginVO(loginUser));
    }

    /**
     * 用户注销
     * @param request 用户注销req
     * @return boolean
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogin(HttpServletRequest request) {
        log.info("user logout: {}", request);
        Boolean userLogout = userService.userLogout(request);
        return ResultUtils.success(userLogout);
    }

    /**
     * 管理员添加用户
     * @param userAddRequest 用户添加请求体
     * @return 用户id
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        log.info("add user: {}", userAddRequest);
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        boolean saveResult = userService.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "添加用户失败");
        }
        return ResultUtils.success(user.getId());
    }

    /**
     * 根据id获取用户（仅限管理员）
     * @param id 用户id
     * @return 用户信息
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id) {
        log.info("get user by id: {}", id);
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR, "请求参数错误");
        User user = userService.getById(id);
        return ResultUtils.success(user);
    }

    /**
     * 根据id获取包装类（仅限管理员）
     * @param id 用户id
     * @return 用户包装类
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVoById(long id) {
        log.info("get userVo by id: {}", id);
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR, "请求参数错误");
        BaseResponse<User> userBaseResponse = getUserById(id);
        User user = userBaseResponse.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 根据id删除用户（仅限管理员）
     * @param deleteResponse 用户删除请求体
     * @return boolean
     */
    @DeleteMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteResponse deleteResponse) {
        log.info("delete user by id: {}", deleteResponse.getId());
        if (ObjectUtil.isEmpty(deleteResponse) ||  deleteResponse.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }
        boolean result = userService.removeById(deleteResponse.getId());
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        log.info("update user: {}", userUpdateRequest);
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新用户失败");
        }
        return ResultUtils.success(result);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserVO>> listUserVoByPage(@RequestBody UserQueryRequest userQueryRequest) {
        log.info("list user vo by page: {}", userQueryRequest);
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        Page<User> userVoPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, size, userVoPage.getTotal());
        List<UserVO> userList = userService.getUserVOList(userVoPage.getRecords());
        userVOPage.setRecords(userList);
        return ResultUtils.success(userVOPage);
    }
}
