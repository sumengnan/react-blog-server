package com.su.blog.entity.vo;

import com.su.blog.entity.User;
import lombok.Data;

/**
 * @ClassName: UserVo
 * @Description:
 * @Author: liuxiaoxiang
 * @Date: 2022/5/8 16:43
 * @Version:
 */
@Data
public class UserBo extends User {
    /**
     * 登录使用的用户名
     */
    private String account;
    /**
     * 应用时间
     */
    private String[] rangeDate;

}
