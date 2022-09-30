package com.su.blog.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Comment implements Serializable {
    private Integer id;

    private Integer articleId;

    private String createdAt;

    private String updatedAt;

    private Integer userId;

    private String content;

    private static final long serialVersionUID = 1L;

    public Comment() {
    }

    public Comment(Integer articleId, Integer userId, String content) {
        this.articleId = articleId;
        this.userId = userId;
        this.content = content;
    }
}