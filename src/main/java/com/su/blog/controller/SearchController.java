package com.su.blog.controller;

import com.su.blog.entity.vo.ArticleBo;
import com.su.blog.service.ArticleService;
import com.su.blog.service.SearchService;
import com.su.blog.util.ParamUtil;
import com.su.blog.util.Result.Result;
import com.su.blog.util.Result.ResultPage;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * @ClassName: User
 * @Description:
 * @Author: liuxiaoxiang
 * @Date: 2022/5/8 9:27
 * @Version:
 */
@RestController
@CrossOrigin
public class SearchController {

    @Autowired
    private SearchService searchService;
    @Autowired
    private ArticleService articleService;
    @PostConstruct
    public void init() throws Exception {
        searchService.init();
    }
    /**
     * 获取列表
     *
     * @return
     */
    @GetMapping("/search")
    public ResponseEntity list(@RequestParam(required = false) String keyword,@RequestParam(required = false,defaultValue = "0") Integer page) throws Exception {
        if(StringUtils.isBlank(keyword)){
            ArticleBo bo =new ArticleBo();
            bo.setPage(bo.getPage() == 0 ? 1 : bo.getPage());
            bo.setPageSize(bo.getPageSize() == 0 ? 10 : bo.getPageSize());
            return Result.Success(articleService.list(bo));
        }else {
            return Result.Success(searchService.list(keyword,page));
        }
    }

}
