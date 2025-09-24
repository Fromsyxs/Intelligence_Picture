package com.feng.yupicturebackend.service.imp;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feng.yupicturebackend.exception.BusinessException;
import com.feng.yupicturebackend.exception.ErrorCode;
import com.feng.yupicturebackend.model.dto.user.UserLoginRequest;
import com.feng.yupicturebackend.model.dto.user.UserQueryRequest;
import com.feng.yupicturebackend.model.dto.user.UserRegisterRequest;
import com.feng.yupicturebackend.model.entity.User;
import com.feng.yupicturebackend.model.enums.UserRoleEnum;
import com.feng.yupicturebackend.model.vo.LoginUserVO;
import com.feng.yupicturebackend.model.vo.UserVO;
import com.feng.yupicturebackend.service.UserService;
import com.feng.yupicturebackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.feng.yupicturebackend.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author 15298
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-07-10 22:56:55
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    private static final int USER_ACCOUNT_LENGTH = 6;

    private static final int USER_PASSWORD_LENGTH = 8;

    private static final int USER_ACCOUNT_COUNT = 0;
    // 盐值
    private static final String USER_SALT = "YUPI";

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户登录req
     * @return 用户 id
     */
    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        log.info("user begin to register {}", userRegisterRequest);
        // 参数校验
        if (StrUtil.hasBlank(userRegisterRequest.getUserAccount(), userRegisterRequest.getUserPassword(), userRegisterRequest.getCheckPassword())) {
            log.info("user register failed, userAccount or password is blank");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户登录请求参数异常");
        }
        if (userRegisterRequest.getUserAccount().length() < USER_ACCOUNT_LENGTH){
            log.info("user register failed, userAccount is too short");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号小于6位");
        }
        if (!userRegisterRequest.getUserPassword().equals(userRegisterRequest.getCheckPassword())) {
            log.info("user register failed, userPassword is wrong");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }
        if (userRegisterRequest.getUserPassword().length() < USER_PASSWORD_LENGTH || userRegisterRequest.getCheckPassword().length() < USER_PASSWORD_LENGTH){
            log.info("user register failed, userPassword is too short");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码小于8位");
        }

        // 检查数据账号是否和数据库中已有的重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userRegisterRequest.getUserAccount());
        Long count = this.baseMapper.selectCount(queryWrapper);
        if (count > 0){
            log.info("user register failed, userAccount is exist");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }

        // 密码加密
        String newPassword = getEntryPassword(userRegisterRequest.getUserPassword());
        User user = new User();
        user.setUserAccount(userRegisterRequest.getUserAccount());
        user.setUserPassword(newPassword);
        user.setUserName("无名");
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean saveResult = this.save(user);
        if (!saveResult){
            log.info("user register failed, database error");
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
        }
        log.info("user success to register {}", user.getId());
        return user.getId();

    }

    @Override
    public LoginUserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        log.info("user begin to login {}", userLoginRequest);
        // 校验
        if (StrUtil.hasBlank(userLoginRequest.getUserAccount(), userLoginRequest.getUserPassword())){
            log.info("user login failed, userAccount or password is blank");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userLoginRequest.getUserAccount().length() < USER_ACCOUNT_LENGTH){
            log.info("user login failed, userAccount is too short");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号错误");
        }
        if (userLoginRequest.getUserPassword().length() < USER_PASSWORD_LENGTH){
            log.info("user login failed, userPassword is too short");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码小于8位");
        }
        // 对用户传入的密码进行加密
        String newPassword = getEntryPassword(userLoginRequest.getUserPassword());
        // 查询数据库中用户是否存在（不存在，抛异常）
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userLoginRequest.getUserAccount());
        if (this.baseMapper.selectCount(queryWrapper) == USER_ACCOUNT_COUNT) {
            log.info("user login failed, userAccount is not exist");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "此账号不存在");
        }
        if (!newPassword.equals(this.baseMapper.selectOne(queryWrapper).getUserPassword())){
            log.info("user login failed, userAccount or password is wrong");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        queryWrapper.eq("userPassword", newPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        if (user == null){
            log.info("user login failed, userAccount or userPassword cannot match");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不存在");
        }
        // 保存用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        log.info("user success to login {}", user.getId());
        return this.getUserLoginVO(user);
    }

    /**
     * 获取脱敏类的用户信息
     * @param user 用户
     * @return 脱敏后的用户信息
     */
    @Override
    public LoginUserVO getUserLoginVO(User user) {
        if(user == null){
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        Object userOjb = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userOjb;
        if (currentUser == null || currentUser.getId() == null){
            log.info("the current user is not logged in");
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        Long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null){
            log.info("the current user is not exist");
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    @Override
    public Boolean getLogout(HttpServletRequest request) {
        try {
            Object userOjb = request.getSession().getAttribute(USER_LOGIN_STATE);
            // 移除登录态
            request.getSession().removeAttribute(USER_LOGIN_STATE);
            return true;
        } catch (BusinessException e) {
            log.warn("UserServiceImpl#getLogout business exception", e);
            return false;
        } catch (Exception e) {
            log.warn("UserServiceImpl#getLogout exception", e);
            return false;
        }
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getListUserVO(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream()
                .map(this::getUserVO)
                .collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), "ascend".equals(sortOrder), sortField);
        return queryWrapper;
    }


    /**
     * 获取加密后的密码
     * @param userPassword 用户密码
     * @return 加密后的密码
     */
    @Override
    public String getEntryPassword(String userPassword) {
        String newPassword = DigestUtil.md5Hex(userPassword + USER_SALT);
        return DigestUtils.md5DigestAsHex(newPassword.getBytes());
    }
}




