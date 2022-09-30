package com.su.blog.util.Result;

/**
 * @ClassName: Code
 * @Description:
 * @Author: liuxiaoxiang
 * @Date: 2022/5/8 14:12
 * @Version:
 */
public enum Code {
    Success(200),
    ParamError(403),
    InnerError(403),
    DefaultError(403);

    private Code(int value) {
        this.value = value;
    }

    private int value = 0;

    public int value() {
        return value;
    }
}
