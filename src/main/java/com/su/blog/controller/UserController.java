package com.su.blog.controller;

import com.su.blog.entity.User;
import com.su.blog.entity.vo.UserBo;
import com.su.blog.service.UserService;
import com.su.blog.util.ParamUtil;
import com.su.blog.util.Result.Result;
import com.su.blog.util.Result.ResultPage;
import org.apache.ibatis.annotations.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @ClassName: User
 * @Description:
 * @Author: liuxiaoxiang
 * @Date: 2022/5/8 9:27
 * @Version:
 */
@RestController
@CrossOrigin
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 注册
     *
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody User user) {
        if (ParamUtil.isOneEmpty(user.getUsername(), user.getEmail(), user.getPassword())) {
            return Result.ParamError();
        }
        userService.register(user);
        return Result.Success();
    }

    /**
     * 登录
     *
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody UserBo user) {
        if (ParamUtil.isOneEmpty(user.getAccount(), user.getPassword())) {
            return Result.ParamError();
        }
        User login = userService.login(user);
        return Result.Success(login);
    }

    /**
     * 获取列表
     *
     * @return
     */
    @GetMapping("/user/list")
    public ResponseEntity list(@ModelAttribute UserBo user) {
        user.setPage(user.getPage() == 0 ? 1 : user.getPage());
        user.setPageSize(user.getPageSize() == 0 ? 10 : user.getPageSize());
        ResultPage list = userService.list(user);
        return Result.Success(list);
    }

    /**
     * 更新用户信息
     *
     * @return
     */
    @PostMapping("/user/{userId}")
    public Object updateUser() {
        return true;
    }

    /**
     * 删除用户
     *
     * @return
     */
    @DeleteMapping("/user/{userId}")
    public ResponseEntity delete(@PathVariable("userId") String userId) {
        if (ParamUtil.isOneEmpty(userId)) {
            return Result.ParamError();
        }
        userService.delete(userId);
        return Result.Success();
    }

    /**
     * 修改邮件通知权限
     *
     * @return
     */
    @PutMapping("/user/{userId}")
    public ResponseEntity put(@PathVariable("userId") String userId, @RequestBody User user) {
        if (ParamUtil.isOneEmpty(userId)) {
            return Result.ParamError();
        }
        userService.put(userId,user.getNotice(),user.getDisabledDiscuss());
        return Result.Success();
    }
}
