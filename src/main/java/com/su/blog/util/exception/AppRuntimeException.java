package com.su.blog.util.exception;


import lombok.Data;
import org.springframework.http.HttpStatus;
@Data
public class AppRuntimeException extends RuntimeException {
    private HttpStatus status;
    private String data;

    public AppRuntimeException(HttpStatus status, String data) {
        this.status = status;
        this.data = data;
    }

    public AppRuntimeException(String data) {
        this.status = HttpStatus.FORBIDDEN;
        this.data = data;
    }


    @Override
    public String getLocalizedMessage() {
        return data;
    }

}
