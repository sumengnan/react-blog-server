package com.su.blog.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Reply implements Serializable {
    private Integer id;

    private Date createdAt;

    private Date updatedAt;

    private Integer articleId;

    private Integer commentId;

    private Integer userId;

    private String content;

    private static final long serialVersionUID = 1L;

    public Reply() {
    }

    public Reply(Integer articleId, Integer commentId, Integer userId, String content) {
        this.articleId = articleId;
        this.commentId = commentId;
        this.userId = userId;
        this.content = content;
    }
}