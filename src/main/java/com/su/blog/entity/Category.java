package com.su.blog.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Category implements Serializable {
    private Integer id;

    private String name;

    private Integer articleId;

    private static final long serialVersionUID = 1L;

}