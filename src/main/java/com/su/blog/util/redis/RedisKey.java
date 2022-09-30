package com.su.blog.util.redis;

public enum RedisKey {
    USER_TOKEN("user_token"),
    LOGIN_ERROR_COUNT_UNAME("login_error_count_");

    String key;

    RedisKey(String key) {
        this.key = key;
    }

    public String value() {
        return key;
    }
}
