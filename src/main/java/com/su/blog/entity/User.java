package com.su.blog.entity;

import com.su.blog.entity.vo.PageBo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class User extends PageBo implements Serializable  {
    private Integer id;
    private Integer userId;

    private String username;

    private String password;

    private String email;

    private Boolean notice;

    private Boolean disabledDiscuss;

    private Byte role;

    private String createdAt;

    private String updatedAt;

    private String github;

    private static final long serialVersionUID = 1L;

}