package com.su.blog.service;

import com.alibaba.fastjson.JSONObject;
import com.su.blog.entity.Article;
import com.su.blog.util.Result.ResultPage;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

/**
 * @ClassName: CategoryService
 * @Description:
 * @Author: liuxiaoxiang
 * @Date: 2022/5/10 22:49
 * @Version:
 */
public interface SearchService {


    @Async
    void init() throws Exception;

    ResultPage list(String keyword, Integer page) throws Exception;
}
