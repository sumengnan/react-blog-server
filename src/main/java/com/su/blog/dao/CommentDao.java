package com.su.blog.dao;

import com.su.blog.entity.Comment;
import com.su.blog.entity.vo.CommentBo;

import java.util.List;

public interface CommentDao {


    int deleteByPrimaryKey(Integer id);

    int insert(Comment record);

    int updateByPrimaryKey(Comment record);

    List<CommentBo> getInfoByArticleId(Integer articleId);

    Integer getCount(Integer articleId);
}