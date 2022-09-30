package com.su.blog.service;

import com.su.blog.entity.User;
import com.su.blog.entity.vo.UserBo;
import com.su.blog.util.Result.ResultPage;

/**
 * @ClassName: HomeService
 * @Description:
 * @Author: liuxiaoxiang
 * @Date: 2022/5/8 11:00
 * @Version:
 */
public interface UserService {
    /**
     * 注册用户
     *
     * @param user
     * @return
     */
    boolean register(User user);

    /**
     * 登录
     *
     * @param user
     * @return
     */
    User login(UserBo user);

    /**
     * 获取用户列表
     *
     * @param user
     * @return
     */
    ResultPage list(UserBo user);

    void delete(String userId);

    void put(String userId, Boolean notice, Boolean disabledDiscuss);
}
