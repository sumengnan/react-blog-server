package com.su.blog.dao;

import com.su.blog.entity.vo.NameCountBo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TagDao {

    List<NameCountBo> list();

    int insertBatch(@Param("item") List<String> item, @Param("articleId")Integer articleId);

    void deleteByArticleId(@Param("articleId")Integer articleId);
}