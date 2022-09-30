package com.su.blog.util.Result;

/**
 * @ClassName: Code
 * @Description:
 * @Author: liuxiaoxiang
 * @Date: 2022/5/8 14:12
 * @Version:
 */
public class Tips {
    //权限
    public static final String AuthError = "权限错误";
    public static final String ServerError = "后端服务异常";

    //注册
    public static final String USER_EMAIL_EXIST_ALREADY = "邮箱已被注册";
    public static final String USER_NAME_EXIST_ALREADY = "用户名已被占用";
    public static final String REGISTER_FAIL = "注册失败";

    //登录
    public static final String USER_OR_PASSWORD_ERROR = "用户或密码错误";
    public static final String LOGIN_ERR_TIME = "账号已锁定,剩余时间：";

    //用户
    public static final String DELETE_ERROR = "删除失败";

    //评论
    public static final String DISCUSS_AUTH_ERROR = "没有评论权限";
    public static final String DISCUSS_ERROR = "评论创建失败";


}
