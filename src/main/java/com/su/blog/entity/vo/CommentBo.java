package com.su.blog.entity.vo;

import com.su.blog.entity.Comment;
import com.su.blog.entity.Reply;
import com.su.blog.entity.User;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * @ClassName: CommentBo
 * @Description:
 * @Author: liuxiaoxiang
 * @Date: 2022/5/14 9:21
 * @Version:
 */
@Data
public class CommentBo extends Comment {
    private Integer userId;
    private Integer commentId;
    private List<Reply> replies;
    private List<User> user;

    public List<Reply> getReplies() {
        if (replies == null) {
            return new LinkedList<>();
        }
        return replies;
    }

    public void setReplies(List<Reply> replies) {
        if (replies == null) {
            replies = new LinkedList<>();
        }
        this.replies = replies;
    }
}
