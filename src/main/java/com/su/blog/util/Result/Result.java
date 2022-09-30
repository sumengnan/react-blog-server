package com.su.blog.util.Result;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @ClassName: 错误信息时提示
 * @Description:
 * @Author: liuxiaoxiang
 * @Date: 2022/5/8 11:09
 * @Version:
 */
@Data
public final class Result {
    private int status;
    private String name;
    private String message;

    private Result() {
    }

    private Result(int status, String name, String message) {
        this.status = status;
        this.name = name;
        this.message = message;
    }


    public static ResponseEntity ResponseEntityError(String message) {
        return ResponseEntityError(HttpStatus.FORBIDDEN, message);
    }

    public static ResponseEntity ResponseEntityError(HttpStatus status) {
        return new ResponseEntity(status);
    }

    public static ResponseEntity ResponseEntityError() {
        return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    public static ResponseEntity ParamError() {
        return ResponseEntityError(HttpStatus.FORBIDDEN, "参数错误");
    }

    public static ResponseEntity ResponseEntityError(HttpStatus status, String message) {
        Result result = new Result(status.value(), status.name(), message);
        return new ResponseEntity(result, status);
    }

    public static ResponseEntity Success(Object data) {
        return new ResponseEntity(data, HttpStatus.OK);
    }

    public static ResponseEntity Success() {
        return new ResponseEntity(HttpStatus.OK);
    }
}
