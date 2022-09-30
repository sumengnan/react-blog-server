package com.su.blog.dao;

import com.su.blog.entity.Article;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ArticleDao {
    int deleteByPrimaryKey(Integer id);

    int deleteByIdList(List<String> list);

    int insert(Article record);

    int updateByPrimaryKey(Article record);

    List<Integer> getListCount(@Param("item") Article item);

    List<Article> getList(@Param("item") Article item);
    List<Article> listAll();

    List<Article> getListByMdFile();

    Article getInfo(Integer id);

    Article getArticleInfo(Integer id);

    Article getInfoByTitle(@Param("title") String title);

    int addViewCount(@Param("id")String id);
}