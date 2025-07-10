package com.feng.yupicturebackend.service.imp;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feng.yupicturebackend.exception.BusinessException;
import com.feng.yupicturebackend.exception.ErrorCode;
import com.feng.yupicturebackend.model.dto.UserRegisterRequest;
import com.feng.yupicturebackend.model.entity.User;
import com.feng.yupicturebackend.model.enums.UserRoleEnum;
import com.feng.yupicturebackend.service.UserService;
import com.feng.yupicturebackend.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
* @author 15298
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-07-10 22:56:55
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    private static final int USER_ACCOUNT_LENGTH = 6;

    private static final int USER_PASSWORD_LENGTH = 8;

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
        // 参数校验
        if (StrUtil.hasBlank(userRegisterRequest.getUserAccount(), userRegisterRequest.getUserPassword(), userRegisterRequest.getCheckPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户登录请求参数异常");
        }
        if (userRegisterRequest.getUserAccount().length() < USER_ACCOUNT_LENGTH){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号小于6位");
        }
        if (userRegisterRequest.getUserPassword().length() < USER_PASSWORD_LENGTH || userRegisterRequest.getCheckPassword().length() < USER_PASSWORD_LENGTH){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码小于8位");
        }
        if (!userRegisterRequest.getUserPassword().equals(userRegisterRequest.getCheckPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }

        // 检查数据账号是否和数据库中已有的重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userRegisterRequest.getUserAccount());
        Long count = this.baseMapper.selectCount(queryWrapper);
        if (count > 0){
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
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
        }
        return user.getId();

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




