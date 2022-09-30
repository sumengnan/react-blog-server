package com.su.blog.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Ip implements Serializable {
    private Integer id;

    private Boolean auth;

    private Integer userId;

    private String ip;

    private static final long serialVersionUID = 1L;

}