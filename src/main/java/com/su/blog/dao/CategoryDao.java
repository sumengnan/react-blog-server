package com.su.blog.dao;

import com.su.blog.entity.vo.NameCountBo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CategoryDao {

    List<NameCountBo> list();

    int insertBatch(@Param("item") List<String> item, @Param("articleId") Integer articleId);

    int deleteByArticleId(@Param("articleId") Integer articleId);
}